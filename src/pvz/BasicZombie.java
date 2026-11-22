package pvz;

public class BasicZombie extends Zombie {
    public BasicZombie(int row) {
        super("BasicZombie", row, GameConstants.BASIC_ZOMBIE_HEALTH, GameConstants.BASIC_ZOMBIE_SPEED);
    }
}
