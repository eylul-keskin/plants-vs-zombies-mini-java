package pvz;

public abstract class Plant {
    private final PlantType type;
    private final int row;
    private final int col;
    private final int cost;
    private int maxHealth;
    private int health;
    private int cooldown1;
    private int cooldown2;
    private boolean activeFlag;

    protected Plant(PlantType type, int row, int col, int maxHealth) {
        this.type = type;
        this.row = row;
        this.col = col;
        this.cost = type.getCost();
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.activeFlag = true;
    }

    public static Plant createPlant(PlantType type, int row, int col) {
        return switch (type) {
            case PEA_SHOOTER -> new PeaShooter(row, col);
            case SUN_FLOWER -> new SunFlower(row, col);
            case WALL_NUT -> new WallNut(row, col);
            case SNOW_PEA -> new SnowPea(row, col);
            case CHERRY_BOMB -> new CherryBomb(row, col);
        };
    }

    public PlantType getType() {
        return type;
    }

    public String getDisplayName() {
        return type.getDisplayName();
    }

    public String getAssetFileName() {
        return type.getAssetFileName();
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getCost() {
        return cost;
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

    public int getCooldown1() {
        return cooldown1;
    }

    public void setCooldown1(int cooldown1) {
        this.cooldown1 = cooldown1;
    }

    public int getCooldown2() {
        return cooldown2;
    }

    public void setCooldown2(int cooldown2) {
        this.cooldown2 = cooldown2;
    }

    public boolean isActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(boolean activeFlag) {
        this.activeFlag = activeFlag;
    }
}
