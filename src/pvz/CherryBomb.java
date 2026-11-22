package pvz;

public class CherryBomb extends Plant {
    public CherryBomb(int row, int col) {
        super(PlantType.CHERRY_BOMB, row, col, GameConstants.CHERRY_BOMB_HEALTH);
        setCooldown1(GameConstants.CHERRY_BOMB_FUSE);
    }
}
