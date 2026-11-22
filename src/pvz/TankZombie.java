package pvz;

public class TankZombie extends Zombie {
    public TankZombie(int row) {
        super("TankZombie", row, GameConstants.TANK_ZOMBIE_HEALTH, GameConstants.TANK_ZOMBIE_SPEED);
    }
}
