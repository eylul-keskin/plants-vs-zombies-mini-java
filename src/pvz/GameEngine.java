package pvz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.Timer;

public class GameEngine {
    private final AssetManager assetManager;
    private final Board board;
    private final List<Zombie> zombies;
    private final List<Bullet> bullets;
    private final List<Sun> suns;
    private final Queue<SpawnRequest> pendingSpawns;
    private final Object pauseLock;
    private final Random random;
    private final Timer timer;

    private Runnable uiRefreshCallback;
    private volatile GameState state;
    private volatile boolean running;
    private volatile WaveSpawner activeWaveSpawner;

    private PlantType selectedPlantType;
    private int sunAmount;
    private int normalSpawnCount;
    private int normalSpawnTimer;
    private int currentWave;
    private boolean waveActive;
    private int waveSpawnedCount;
    private int waveRemainingToSpawn;
    private int waveCompletedCount;
    private boolean waveSpawnerFinished;
    private String statusMessage;
    private int statusMessageTimer;

    public GameEngine(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.board = new Board();
        this.zombies = new ArrayList<>();
        this.bullets = new ArrayList<>();
        this.suns = new ArrayList<>();
        this.pendingSpawns = new ConcurrentLinkedQueue<>();
        this.pauseLock = new Object();
        this.random = new Random();
        this.timer = new Timer(GameConstants.UPDATE_DELAY, event -> updateGame());
        timer.setInitialDelay(0);
        running = true;
        resetToNewGameState();
        timer.stop();
    }

    public void setUiRefreshCallback(Runnable uiRefreshCallback) {
        this.uiRefreshCallback = uiRefreshCallback;
        refreshUi();
    }

    public void startNewGame() {
        resetToNewGameState();
        timer.start();
        refreshUi();
    }

    public void loadGame() throws IOException {
        SaveLoadManager.LoadedState loadedState = SaveLoadManager.loadGame();
        applyLoadedState(loadedState);
    }

    public void saveGame() throws IOException {
        if (state != GameState.PAUSED) {
            throw new IllegalStateException("Game can only be saved while paused.");
        }
        processPendingSpawns();
        SaveLoadManager.saveGame(this);
        setStatusMessage("Game saved to " + GameConstants.SAVE_FILE_PATH, 2500);
        refreshUi();
    }

    public void shutdown() {
        running = false;
        timer.stop();
        cancelCurrentWaveSpawner();
        synchronized (pauseLock) {
            pauseLock.notifyAll();
        }
    }

    private void resetToNewGameState() {
        timer.stop();
        cancelCurrentWaveSpawner();
        running = true;
        board.clear();
        zombies.clear();
        bullets.clear();
        suns.clear();
        pendingSpawns.clear();

        state = GameState.IN_GAME;
        selectedPlantType = null;
        sunAmount = GameConstants.INITIAL_SUN;
        normalSpawnCount = 0;
        normalSpawnTimer = GameConstants.NORMAL_SPAWN_INTERVAL;
        currentWave = 0;
        waveActive = false;
        waveSpawnedCount = 0;
        waveRemainingToSpawn = 0;
        waveCompletedCount = 0;
        waveSpawnerFinished = true;
        statusMessage = "Choose a plant from the top panel.";
        statusMessageTimer = 0;
    }

    private void applyLoadedState(SaveLoadManager.LoadedState loadedState) {
        timer.stop();
        cancelCurrentWaveSpawner();
        running = true;
        board.clear();
        zombies.clear();
        bullets.clear();
        suns.clear();
        pendingSpawns.clear();

        state = loadedState.gameState;
        selectedPlantType = loadedState.selectedPlantType;
        sunAmount = loadedState.sunAmount;
        normalSpawnCount = loadedState.normalSpawnCount;
        normalSpawnTimer = loadedState.timeUntilNextSpawn;
        currentWave = loadedState.currentWave;
        waveActive = loadedState.waveActive;
        waveSpawnedCount = loadedState.waveSpawnedCount;
        waveRemainingToSpawn = loadedState.waveRemainingCount;
        waveCompletedCount = loadedState.waveCompletedCount;
        waveSpawnerFinished = !waveActive || waveRemainingToSpawn == 0;

        for (Plant plant : loadedState.plants) {
            board.placePlant(plant);
        }
        zombies.addAll(loadedState.zombies);
        bullets.addAll(loadedState.bullets);
        suns.addAll(loadedState.suns);

        if (waveActive && waveRemainingToSpawn > 0) {
            startWave(currentWave, waveSpawnedCount);
        }

        if (state == GameState.IN_GAME) {
            timer.start();
        } else {
            timer.stop();
        }

        statusMessage = "Game loaded. Click Resume to continue.";
        statusMessageTimer = 0;
        refreshUi();
    }

    private void updateGame() {
        if (!running || state != GameState.IN_GAME) {
            return;
        }

        processPendingSpawns();
        updatePlants();
        updateZombies();
        updateBullets();
        resolveBulletCollisions();
        removeDeadObjects();
        updateNormalProduction();
        checkWaveProgress();
        updateStatusMessageTimer();
        refreshUi();
    }

    private void processPendingSpawns() {
        SpawnRequest spawnRequest;
        while ((spawnRequest = pendingSpawns.poll()) != null) {
            Zombie zombie = Zombie.createZombie(spawnRequest.getZombieType(), spawnRequest.getRow());
            zombie.scaleHealth(spawnRequest.getHealthMultiplier());
            zombies.add(zombie);
        }
    }

    private void updatePlants() {
        List<Plant> plantsToRemove = new ArrayList<>();

        for (int row = 0; row < GameConstants.ROWS; row++) {
            for (int col = 0; col < GameConstants.COLS; col++) {
                Plant plant = board.getPlant(row, col);
                if (plant == null) {
                    continue;
                }

                if (!plant.isAlive()) {
                    plantsToRemove.add(plant);
                    continue;
                }

                decreasePlantCooldowns(plant);

                switch (plant.getType()) {
                    case PEA_SHOOTER -> updateShooterPlant(plant, false);
                    case SUN_FLOWER -> updateSunFlower(plant);
                    case WALL_NUT -> {
                        // WallNut only blocks.
                    }
                    case SNOW_PEA -> updateShooterPlant(plant, true);
                    case CHERRY_BOMB -> updateCherryBomb(plant, plantsToRemove);
                }
            }
        }

        for (Plant plant : plantsToRemove) {
            board.removePlant(plant.getRow(), plant.getCol());
        }
    }

    private void decreasePlantCooldowns(Plant plant) {
        if (plant.getCooldown1() > 0) {
            plant.setCooldown1(Math.max(0, plant.getCooldown1() - GameConstants.UPDATE_DELAY));
        }
        if (plant.getCooldown2() > 0) {
            plant.setCooldown2(Math.max(0, plant.getCooldown2() - GameConstants.UPDATE_DELAY));
        }
    }

    private void updateShooterPlant(Plant plant, boolean snowBullet) {
        double plantCenterX = getCellCenterX(plant.getCol());
        if (plant.getCooldown1() <= 0 && hasZombieAhead(plant.getRow(), plantCenterX)) {
            double bulletStartX = getCellPixelX(plant.getCol()) + GameConstants.PLANT_WIDTH;
            double speed = snowBullet ? GameConstants.SNOW_BULLET_SPEED : GameConstants.BULLET_SPEED;
            bullets.add(new Bullet(plant.getRow(), bulletStartX, GameConstants.PEA_DAMAGE, speed, snowBullet));
            int nextCooldown = snowBullet
                    ? GameConstants.SNOW_PEA_FIRE_INTERVAL
                    : GameConstants.PEA_SHOOTER_FIRE_INTERVAL;
            plant.setCooldown1(nextCooldown);
        }
    }

    private void updateSunFlower(Plant plant) {
        if (plant.getCooldown1() <= 0) {
            createSunAtCell(plant.getRow(), plant.getCol());
            plant.setCooldown1(GameConstants.SUNFLOWER_SUN_INTERVAL);
        }
    }

    private void updateCherryBomb(Plant plant, List<Plant> plantsToRemove) {
        if (!plant.isActiveFlag()) {
            plantsToRemove.add(plant);
            return;
        }

        if (plant.getCooldown1() <= 0) {
            explodeCherryBomb(plant);
            plant.setActiveFlag(false);
            plantsToRemove.add(plant);
        }
    }

    private void explodeCherryBomb(Plant plant) {
        int cellLeft = getCellPixelX(plant.getCol());
        int cellRight = cellLeft + GameConstants.CELL_WIDTH;

        for (Zombie zombie : zombies) {
            if (zombie.getRow() == plant.getRow()
                    && zombie.getCenterX() >= cellLeft
                    && zombie.getCenterX() <= cellRight) {
                zombie.setHealth(0);
            }
        }
    }

    private void updateZombies() {
        for (Zombie zombie : zombies) {
            if (!zombie.isAlive()) {
                continue;
            }

            zombie.updateSlowState(GameConstants.UPDATE_DELAY);
            Plant blockingPlant = findBlockingPlant(zombie);

            if (blockingPlant != null) {
                zombie.setAttackTicksRemaining(zombie.getAttackTicksRemaining() - GameConstants.UPDATE_DELAY);
                if (zombie.getAttackTicksRemaining() <= 0) {
                    blockingPlant.takeDamage(GameConstants.ZOMBIE_ATTACK_DAMAGE);
                    zombie.setAttackTicksRemaining(GameConstants.ZOMBIE_ATTACK_INTERVAL);
                    if (!blockingPlant.isAlive()) {
                        board.removePlant(blockingPlant.getRow(), blockingPlant.getCol());
                    }
                }
            } else {
                zombie.setAttackTicksRemaining(GameConstants.ZOMBIE_ATTACK_INTERVAL);
                zombie.setX(zombie.getX() - zombie.getCurrentSpeed());
                if (zombie.getX() <= GameConstants.BOARD_X - GameConstants.ZOMBIE_WIDTH / 2.0) {
                    endGame(GameState.DEFEAT, "A zombie reached the house.");
                    return;
                }
            }
        }
    }

    private Plant findBlockingPlant(Zombie zombie) {
        int centerColumn = getColumnForX(zombie.getCenterX());
        if (board.isInsideBoard(zombie.getRow(), centerColumn)) {
            Plant plant = board.getPlant(zombie.getRow(), centerColumn);
            if (plant != null) {
                return plant;
            }
        }

        int frontColumn = getColumnForX(zombie.getX() + 10);
        if (frontColumn != centerColumn && board.isInsideBoard(zombie.getRow(), frontColumn)) {
            return board.getPlant(zombie.getRow(), frontColumn);
        }

        return null;
    }

    private void updateBullets() {
        for (Bullet bullet : bullets) {
            bullet.move();
        }
    }

    private void resolveBulletCollisions() {
        List<Bullet> bulletsToRemove = new ArrayList<>();

        for (Bullet bullet : bullets) {
            Zombie hitZombie = findHitZombie(bullet);
            if (hitZombie != null) {
                hitZombie.takeDamage(bullet.getDamage());
                if (bullet.isSnowBullet()) {
                    hitZombie.applySlow(GameConstants.SLOW_DURATION);
                }
                bulletsToRemove.add(bullet);
            }
        }

        bullets.removeAll(bulletsToRemove);
    }

    private Zombie findHitZombie(Bullet bullet) {
        Zombie closestZombie = null;
        for (Zombie zombie : zombies) {
            if (!zombie.isAlive() || zombie.getRow() != bullet.getRow()) {
                continue;
            }

            if (bullet.getX() >= zombie.getX() && bullet.getX() <= zombie.getRightX()) {
                if (closestZombie == null || zombie.getX() < closestZombie.getX()) {
                    closestZombie = zombie;
                }
            }
        }
        return closestZombie;
    }

    private void removeDeadObjects() {
        zombies.removeIf(zombie -> !zombie.isAlive());
        bullets.removeIf(bullet -> bullet.getX() > GameConstants.BOARD_X + GameConstants.BOARD_WIDTH + 120);

        for (int row = 0; row < GameConstants.ROWS; row++) {
            for (int col = 0; col < GameConstants.COLS; col++) {
                Plant plant = board.getPlant(row, col);
                if (plant != null && !plant.isAlive()) {
                    board.removePlant(row, col);
                }
            }
        }
    }

    private void updateNormalProduction() {
        if (waveActive || waveCompletedCount >= GameConstants.TOTAL_WAVES) {
            return;
        }

        normalSpawnTimer -= GameConstants.UPDATE_DELAY;
        if (normalSpawnTimer > 0) {
            return;
        }

        normalSpawnTimer = GameConstants.NORMAL_SPAWN_INTERVAL;
        spawnNormalZombie();

        int nextWaveThreshold = GameConstants.NORMAL_SPAWNS_PER_WAVE_TRIGGER * (waveCompletedCount + 1);
        if (!waveActive && normalSpawnCount >= nextWaveThreshold) {
            startWave(waveCompletedCount + 1, 0);
        }
    }

    private void spawnNormalZombie() {
        int row = random.nextInt(GameConstants.ROWS);
        String zombieType = random.nextBoolean() ? "BasicZombie" : "FastZombie";
        zombies.add(Zombie.createZombie(zombieType, row));
        normalSpawnCount++;
    }

    private void startWave(int waveNumber, int alreadySpawned) {
        if (waveNumber < 1 || waveNumber > GameConstants.TOTAL_WAVES) {
            return;
        }

        cancelCurrentWaveSpawner();
        waveActive = true;
        currentWave = waveNumber;
        waveSpawnedCount = alreadySpawned;
        waveRemainingToSpawn = Math.max(0, getTotalSpawnsForWave(waveNumber) - alreadySpawned);
        waveSpawnerFinished = waveRemainingToSpawn == 0;

        if (waveRemainingToSpawn > 0) {
            activeWaveSpawner = new WaveSpawner(
                    this,
                    waveNumber,
                    getTotalSpawnsForWave(waveNumber),
                    alreadySpawned,
                    getDelayForWave(waveNumber),
                    getHealthMultiplierForWave(waveNumber)
            );
            activeWaveSpawner.start();
        }

        setStatusMessage("Wave " + waveNumber + " started.", 2000);
    }

    private int getTotalSpawnsForWave(int waveNumber) {
        return waveNumber == 1 ? GameConstants.WAVE_ONE_TOTAL_SPAWNS : GameConstants.WAVE_TWO_TOTAL_SPAWNS;
    }

    private int getDelayForWave(int waveNumber) {
        return waveNumber == 1 ? GameConstants.WAVE_ONE_DELAY : GameConstants.WAVE_TWO_DELAY;
    }

    private double getHealthMultiplierForWave(int waveNumber) {
        return waveNumber == 1
                ? GameConstants.WAVE_ONE_HEALTH_MULTIPLIER
                : GameConstants.WAVE_TWO_HEALTH_MULTIPLIER;
    }

    private void checkWaveProgress() {
        if (!waveActive || !waveSpawnerFinished || !zombies.isEmpty()) {
            return;
        }

        if (currentWave >= GameConstants.TOTAL_WAVES) {
            endGame(GameState.VICTORY, "All waves were defeated.");
            return;
        }

        waveActive = false;
        waveCompletedCount = currentWave;
        waveSpawnedCount = 0;
        waveRemainingToSpawn = 0;
        setStatusMessage("Wave " + currentWave + " cleared.", 2000);
    }

    private void endGame(GameState endState, String message) {
        state = endState;
        timer.stop();
        cancelCurrentWaveSpawner();
        synchronized (pauseLock) {
            pauseLock.notifyAll();
        }
        setStatusMessage(message, 0);
        refreshUi();
    }

    private void updateStatusMessageTimer() {
        if (statusMessageTimer > 0) {
            statusMessageTimer = Math.max(0, statusMessageTimer - GameConstants.UPDATE_DELAY);
            if (statusMessageTimer == 0 && state == GameState.IN_GAME) {
                statusMessage = "Selected: " + (selectedPlantType == null
                        ? "None"
                        : selectedPlantType.getDisplayName());
            }
        }
    }

    private void createSunAtCell(int row, int col) {
        double baseX = getCellPixelX(col) + (GameConstants.CELL_WIDTH - GameConstants.SUN_SIZE) / 2.0;
        double baseY = getCellPixelY(row) + (GameConstants.CELL_HEIGHT - GameConstants.SUN_SIZE) / 2.0;
        double xOffset = random.nextInt(21) - 10;
        double yOffset = random.nextInt(17) - 8;
        suns.add(new Sun(row, baseX + xOffset, baseY + yOffset, GameConstants.SUN_VALUE));
    }

    private boolean hasZombieAhead(int row, double startX) {
        for (Zombie zombie : zombies) {
            if (zombie.isAlive() && zombie.getRow() == row && zombie.getCenterX() > startX) {
                return true;
            }
        }
        return false;
    }

    public boolean collectSunAt(int mouseX, int mouseY) {
        if (state != GameState.IN_GAME) {
            return false;
        }

        for (int i = suns.size() - 1; i >= 0; i--) {
            Sun sun = suns.get(i);
            if (sun.contains(mouseX, mouseY)) {
                suns.remove(i);
                sunAmount += sun.getValue();
                setStatusMessage("Collected +" + sun.getValue() + " sun.", 1200);
                refreshUi();
                return true;
            }
        }
        return false;
    }

    public boolean placeSelectedPlant(int row, int col) {
        if (state != GameState.IN_GAME || !board.isInsideBoard(row, col)) {
            return false;
        }

        if (selectedPlantType == null) {
            setStatusMessage("Choose a plant first.", 1500);
            refreshUi();
            return false;
        }

        if (board.getPlant(row, col) != null) {
            setStatusMessage("This cell already has a plant.", 1500);
            refreshUi();
            return false;
        }

        Plant plant = Plant.createPlant(selectedPlantType, row, col);
        if (sunAmount < plant.getCost()) {
            setStatusMessage("Not enough sun for " + plant.getDisplayName() + ".", 1500);
            refreshUi();
            return false;
        }

        board.placePlant(plant);
        sunAmount -= plant.getCost();
        setStatusMessage(plant.getDisplayName() + " planted.", 1200);
        refreshUi();
        return true;
    }

    public void pauseGame() {
        if (state != GameState.IN_GAME) {
            return;
        }
        timer.stop();
        state = GameState.PAUSED;
        setStatusMessage("Game paused.", 0);
        refreshUi();
    }

    public void resumeGame() {
        if (state != GameState.PAUSED) {
            return;
        }
        state = GameState.IN_GAME;
        synchronized (pauseLock) {
            pauseLock.notifyAll();
        }
        timer.start();
        setStatusMessage("Game resumed.", 1200);
        refreshUi();
    }

    public void setSelectedPlantType(PlantType selectedPlantType) {
        this.selectedPlantType = selectedPlantType;
        if (selectedPlantType == null) {
            setStatusMessage("Plant selection cleared.", 1200);
        } else {
            setStatusMessage("Selected " + selectedPlantType.getDisplayName() + ".", 1200);
        }
        refreshUi();
    }

    public synchronized void queueWaveSpawn(WaveSpawner source, SpawnRequest spawnRequest) {
        if (!running || source != activeWaveSpawner) {
            return;
        }
        pendingSpawns.add(spawnRequest);
        waveSpawnedCount++;
        waveRemainingToSpawn = Math.max(0, getTotalSpawnsForWave(currentWave) - waveSpawnedCount);
    }

    public synchronized void markWaveSpawnerFinished(WaveSpawner source) {
        if (source == activeWaveSpawner) {
            activeWaveSpawner = null;
            waveSpawnerFinished = true;
            waveRemainingToSpawn = Math.max(0, getTotalSpawnsForWave(currentWave) - waveSpawnedCount);
        }
    }

    public void waitWhilePaused(WaveSpawner source) throws InterruptedException {
        synchronized (pauseLock) {
            while (running && source == activeWaveSpawner && state == GameState.PAUSED) {
                pauseLock.wait();
            }
        }
    }

    public boolean shouldWaveSpawnerContinue(WaveSpawner source) {
        return running
                && source == activeWaveSpawner
                && state != GameState.VICTORY
                && state != GameState.DEFEAT;
    }

    private void cancelCurrentWaveSpawner() {
        WaveSpawner waveSpawner = activeWaveSpawner;
        activeWaveSpawner = null;
        if (waveSpawner != null) {
            waveSpawner.cancelSpawner();
        }
    }

    private void setStatusMessage(String statusMessage, int durationMs) {
        this.statusMessage = statusMessage;
        this.statusMessageTimer = durationMs;
    }

    private void refreshUi() {
        if (uiRefreshCallback != null) {
            uiRefreshCallback.run();
        }
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public Board getBoard() {
        return board;
    }

    public List<Zombie> getZombies() {
        return Collections.unmodifiableList(zombies);
    }

    public List<Bullet> getBullets() {
        return Collections.unmodifiableList(bullets);
    }

    public List<Sun> getSuns() {
        return Collections.unmodifiableList(suns);
    }

    public int getSunAmount() {
        return sunAmount;
    }

    public PlantType getSelectedPlantType() {
        return selectedPlantType;
    }

    public GameState getState() {
        return state;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public int getNormalSpawnCount() {
        return normalSpawnCount;
    }

    public int getNormalSpawnTimer() {
        return normalSpawnTimer;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public boolean isWaveActive() {
        return waveActive;
    }

    public int getWaveSpawnedCount() {
        return waveSpawnedCount;
    }

    public int getWaveRemainingToSpawn() {
        return waveRemainingToSpawn;
    }

    public int getWaveCompletedCount() {
        return waveCompletedCount;
    }

    public static int getCellPixelX(int col) {
        return GameConstants.BOARD_X + col * GameConstants.CELL_WIDTH;
    }

    public static int getCellPixelY(int row) {
        return GameConstants.BOARD_Y + row * GameConstants.CELL_HEIGHT;
    }

    public static int getCellCenterX(int col) {
        return getCellPixelX(col) + GameConstants.CELL_WIDTH / 2;
    }

    public static int getCellCenterY(int row) {
        return getCellPixelY(row) + GameConstants.CELL_HEIGHT / 2;
    }

    public static int getColumnForX(double x) {
        return (int) ((x - GameConstants.BOARD_X) / GameConstants.CELL_WIDTH);
    }
}
