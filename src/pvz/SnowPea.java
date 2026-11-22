package pvz;

public class SnowPea extends Plant {
    public SnowPea(int row, int col) {
        super(PlantType.SNOW_PEA, row, col, GameConstants.SNOW_PEA_HEALTH);
        setCooldown1(GameConstants.SNOW_PEA_FIRE_INTERVAL);
    }
}
