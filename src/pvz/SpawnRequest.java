package pvz;

public class SpawnRequest {
    private final String zombieType;
    private final int row;
    private final double healthMultiplier;

    public SpawnRequest(String zombieType, int row, double healthMultiplier) {
        this.zombieType = zombieType;
        this.row = row;
        this.healthMultiplier = healthMultiplier;
    }

    public String getZombieType() {
        return zombieType;
    }

    public int getRow() {
        return row;
    }

    public double getHealthMultiplier() {
        return healthMultiplier;
    }
}
