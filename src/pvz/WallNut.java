package pvz;

public class WallNut extends Plant {
    public WallNut(int row, int col) {
        super(PlantType.WALL_NUT, row, col, GameConstants.WALLNUT_HEALTH);
    }
}
