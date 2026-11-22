package pvz;

import java.util.Random;

public class WaveSpawner extends Thread {
    private final GameEngine gameEngine;
    private final int waveNumber;
    private final int totalSpawns;
    private final int alreadySpawned;
    private final int spawnDelayMs;
    private final double healthMultiplier;
    private final Random random;
    private volatile boolean cancelled;

    public WaveSpawner(
            GameEngine gameEngine,
            int waveNumber,
            int totalSpawns,
            int alreadySpawned,
            int spawnDelayMs,
            double healthMultiplier
    ) {
        this.gameEngine = gameEngine;
        this.waveNumber = waveNumber;
        this.totalSpawns = totalSpawns;
        this.alreadySpawned = alreadySpawned;
        this.spawnDelayMs = spawnDelayMs;
        this.healthMultiplier = healthMultiplier;
        this.random = new Random();
        this.cancelled = false;
        setName("WaveSpawner-" + waveNumber);
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            for (int index = alreadySpawned; index < totalSpawns; index++) {
                if (!gameEngine.shouldWaveSpawnerContinue(this) || cancelled) {
                    return;
                }

                gameEngine.waitWhilePaused(this);
                if (!gameEngine.shouldWaveSpawnerContinue(this) || cancelled) {
                    return;
                }

                String zombieType = pickZombieType();
                int row = random.nextInt(GameConstants.ROWS);
                gameEngine.queueWaveSpawn(this, new SpawnRequest(zombieType, row, healthMultiplier));

                waitBetweenSpawns();
            }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        } finally {
            gameEngine.markWaveSpawnerFinished(this);
        }
    }

    public void cancelSpawner() {
        cancelled = true;
        interrupt();
    }

    private void waitBetweenSpawns() throws InterruptedException {
        int remaining = spawnDelayMs;
        while (remaining > 0) {
            if (!gameEngine.shouldWaveSpawnerContinue(this) || cancelled) {
                return;
            }
            gameEngine.waitWhilePaused(this);
            int sleepTime = Math.min(remaining, 120);
            Thread.sleep(sleepTime);
            remaining -= sleepTime;
        }
    }

    private String pickZombieType() {
        String[] choices = waveNumber == 1
                ? GameConstants.WAVE_ONE_ZOMBIES
                : GameConstants.WAVE_TWO_ZOMBIES;
        return choices[random.nextInt(choices.length)];
    }
}
