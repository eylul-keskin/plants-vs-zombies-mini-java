package pvz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class SaveLoadManager {
    private SaveLoadManager() {
    }

    public static void saveGame(GameEngine gameEngine) throws IOException {
        Path savePath = Path.of(GameConstants.SAVE_FILE_PATH);
        Files.createDirectories(savePath.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(savePath)) {
            writeLine(writer, "STATE|" + gameEngine.getState());
            writeLine(writer, "SUN|" + gameEngine.getSunAmount());
            writeLine(writer, "SELECTED|" + (gameEngine.getSelectedPlantType() == null
                    ? "NONE"
                    : gameEngine.getSelectedPlantType().name()));
            writeLine(writer, "NORMAL|" + gameEngine.getNormalSpawnCount() + "|" + gameEngine.getNormalSpawnTimer());
            writeLine(writer, "WAVE|"
                    + gameEngine.getCurrentWave() + "|"
                    + gameEngine.isWaveActive() + "|"
                    + gameEngine.getWaveSpawnedCount() + "|"
                    + gameEngine.getWaveRemainingToSpawn() + "|"
                    + gameEngine.getWaveCompletedCount());

            for (Plant plant : gameEngine.getBoard().getPlants()) {
                writeLine(writer, "PLANT|"
                        + plant.getType().name() + "|"
                        + plant.getRow() + "|"
                        + plant.getCol() + "|"
                        + plant.getHealth() + "|"
                        + plant.getCooldown1() + "|"
                        + plant.getCooldown2() + "|"
                        + plant.isActiveFlag());
            }

            for (Zombie zombie : gameEngine.getZombies()) {
                writeLine(writer, "ZOMBIE|"
                        + zombie.getTypeName() + "|"
                        + zombie.getRow() + "|"
                        + formatDouble(zombie.getX()) + "|"
                        + zombie.getHealth() + "|"
                        + formatDouble(zombie.getBaseSpeed()) + "|"
                        + formatDouble(zombie.getCurrentSpeed()) + "|"
                        + zombie.getSlowTicksRemaining() + "|"
                        + zombie.getAttackTicksRemaining());
            }

            for (Bullet bullet : gameEngine.getBullets()) {
                writeLine(writer, "BULLET|"
                        + bullet.getRow() + "|"
                        + formatDouble(bullet.getX()) + "|"
                        + bullet.getDamage() + "|"
                        + formatDouble(bullet.getSpeed()) + "|"
                        + bullet.isSnowBullet());
            }

            for (Sun sun : gameEngine.getSuns()) {
                writeLine(writer, "SUNOBJ|"
                        + sun.getRow() + "|"
                        + formatDouble(sun.getX()) + "|"
                        + formatDouble(sun.getY()) + "|"
                        + sun.getValue());
            }
        }
    }

    public static LoadedState loadGame() throws IOException {
        Path savePath = Path.of(GameConstants.SAVE_FILE_PATH);
        if (!Files.exists(savePath)) {
            throw new IOException("Save file not found: " + GameConstants.SAVE_FILE_PATH);
        }

        LoadedState loadedState = new LoadedState();

        try (BufferedReader reader = Files.newBufferedReader(savePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                parseLine(line, loadedState);
            }
        } catch (RuntimeException exception) {
            throw new IOException("Save file is corrupted or invalid.", exception);
        }

        return loadedState;
    }

    private static void parseLine(String line, LoadedState loadedState) {
        String[] parts = line.split("\\|");
        switch (parts[0]) {
            case "STATE" -> loadedState.gameState = GameState.valueOf(parts[1]);
            case "SUN" -> loadedState.sunAmount = Integer.parseInt(parts[1]);
            case "SELECTED" -> loadedState.selectedPlantType = "NONE".equals(parts[1])
                    ? null
                    : PlantType.valueOf(parts[1]);
            case "NORMAL" -> {
                loadedState.normalSpawnCount = Integer.parseInt(parts[1]);
                loadedState.timeUntilNextSpawn = Integer.parseInt(parts[2]);
            }
            case "WAVE" -> {
                loadedState.currentWave = Integer.parseInt(parts[1]);
                loadedState.waveActive = Boolean.parseBoolean(parts[2]);
                loadedState.waveSpawnedCount = Integer.parseInt(parts[3]);
                loadedState.waveRemainingCount = Integer.parseInt(parts[4]);
                loadedState.waveCompletedCount = Integer.parseInt(parts[5]);
            }
            case "PLANT" -> loadedState.plants.add(parsePlant(parts));
            case "ZOMBIE" -> loadedState.zombies.add(parseZombie(parts));
            case "BULLET" -> loadedState.bullets.add(parseBullet(parts));
            case "SUNOBJ" -> loadedState.suns.add(parseSun(parts));
            default -> throw new IllegalArgumentException("Unknown save record: " + parts[0]);
        }
    }

    private static Plant parsePlant(String[] parts) {
        Plant plant = Plant.createPlant(PlantType.valueOf(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        plant.setHealth(Integer.parseInt(parts[4]));
        plant.setCooldown1(Integer.parseInt(parts[5]));
        plant.setCooldown2(Integer.parseInt(parts[6]));
        plant.setActiveFlag(Boolean.parseBoolean(parts[7]));
        return plant;
    }

    private static Zombie parseZombie(String[] parts) {
        Zombie zombie = Zombie.createZombie(parts[1], Integer.parseInt(parts[2]));
        zombie.setX(Double.parseDouble(parts[3]));
        zombie.setHealth(Integer.parseInt(parts[4]));
        zombie.setBaseSpeed(Double.parseDouble(parts[5]));
        zombie.setCurrentSpeed(Double.parseDouble(parts[6]));
        zombie.setSlowTicksRemaining(Integer.parseInt(parts[7]));
        zombie.setAttackTicksRemaining(Integer.parseInt(parts[8]));
        return zombie;
    }

    private static Bullet parseBullet(String[] parts) {
        return new Bullet(
                Integer.parseInt(parts[1]),
                Double.parseDouble(parts[2]),
                Integer.parseInt(parts[3]),
                Double.parseDouble(parts[4]),
                Boolean.parseBoolean(parts[5])
        );
    }

    private static Sun parseSun(String[] parts) {
        return new Sun(
                Integer.parseInt(parts[1]),
                Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3]),
                Integer.parseInt(parts[4])
        );
    }

    private static void writeLine(BufferedWriter writer, String line) throws IOException {
        writer.write(line);
        writer.newLine();
    }

    private static String formatDouble(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    public static class LoadedState {
        GameState gameState = GameState.PAUSED;
        int sunAmount = GameConstants.INITIAL_SUN;
        PlantType selectedPlantType;
        int normalSpawnCount;
        int timeUntilNextSpawn = GameConstants.NORMAL_SPAWN_INTERVAL;
        int currentWave;
        boolean waveActive;
        int waveSpawnedCount;
        int waveRemainingCount;
        int waveCompletedCount;
        final List<Plant> plants = new ArrayList<>();
        final List<Zombie> zombies = new ArrayList<>();
        final List<Bullet> bullets = new ArrayList<>();
        final List<Sun> suns = new ArrayList<>();
    }
}
