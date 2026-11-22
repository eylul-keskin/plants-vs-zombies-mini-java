package pvz;

public class SunFlower extends Plant {
    public SunFlower(int row, int col) {
        super(PlantType.SUN_FLOWER, row, col, GameConstants.SUNFLOWER_HEALTH);
        setCooldown1(GameConstants.SUNFLOWER_SUN_INTERVAL);
    }
}
