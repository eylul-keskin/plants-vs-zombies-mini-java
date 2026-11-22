package pvz;

public class PeaShooter extends Plant {
    public PeaShooter(int row, int col) {
        super(PlantType.PEA_SHOOTER, row, col, GameConstants.PEA_SHOOTER_HEALTH);
        setCooldown1(GameConstants.PEA_SHOOTER_FIRE_INTERVAL);
    }
}
