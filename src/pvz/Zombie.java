package pvz;

public abstract class Zombie {
    private final String typeName;
    private final int row;
    private int maxHealth;
    private int health;
    private double x;
    private double baseSpeed;
    private double currentSpeed;
    private int slowTicksRemaining;
    private int attackTicksRemaining;

    protected Zombie(String typeName, int row, int maxHealth, double baseSpeed) {
        this.typeName = typeName;
        this.row = row;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.x = GameConstants.BOARD_X + GameConstants.BOARD_WIDTH + 20;
        this.baseSpeed = baseSpeed;
        this.currentSpeed = baseSpeed;
        this.attackTicksRemaining = GameConstants.ZOMBIE_ATTACK_INTERVAL;
    }

    public static Zombie createZombie(String typeName, int row) {
        return switch (typeName) {
            case "BasicZombie" -> new BasicZombie(row);
            case "FastZombie" -> new FastZombie(row);
            case "RunZombie" -> new RunZombie(row);
            case "TankZombie" -> new TankZombie(row);
            default -> throw new IllegalArgumentException("Unknown zombie type: " + typeName);
        };
    }

    public String getTypeName() {
        return typeName;
    }

    public int getRow() {
        return row;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void takeDamage(int damage) {
        health -= damage;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getRightX() {
        return x + GameConstants.ZOMBIE_WIDTH;
    }

    public double getCenterX() {
        return x + GameConstants.ZOMBIE_WIDTH / 2.0;
    }

    public double getBaseSpeed() {
        return baseSpeed;
    }

    public void setBaseSpeed(double baseSpeed) {
        this.baseSpeed = baseSpeed;
    }

    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(double currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public int getSlowTicksRemaining() {
        return slowTicksRemaining;
    }

    public void setSlowTicksRemaining(int slowTicksRemaining) {
        this.slowTicksRemaining = slowTicksRemaining;
    }

    public int getAttackTicksRemaining() {
        return attackTicksRemaining;
    }

    public void setAttackTicksRemaining(int attackTicksRemaining) {
        this.attackTicksRemaining = attackTicksRemaining;
    }

    public void applySlow(int duration) {
        if (duration > slowTicksRemaining) {
            slowTicksRemaining = duration;
        }
        currentSpeed = baseSpeed * GameConstants.SLOW_MULTIPLIER;
    }

    public void updateSlowState(int elapsedMs) {
        if (slowTicksRemaining > 0) {
            slowTicksRemaining = Math.max(0, slowTicksRemaining - elapsedMs);
            currentSpeed = baseSpeed * GameConstants.SLOW_MULTIPLIER;
        } else {
            currentSpeed = baseSpeed;
        }
    }

    public void scaleHealth(double multiplier) {
        maxHealth = Math.max(1, (int) Math.round(maxHealth * multiplier));
        health = maxHealth;
    }
}
