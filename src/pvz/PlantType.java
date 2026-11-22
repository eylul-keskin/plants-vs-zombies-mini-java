package pvz;

public enum PlantType {
    PEA_SHOOTER("Seed Turret", GameConstants.PEA_SHOOTER_COST, "PeaShooter.jpg"),
    SUN_FLOWER("Sun Bloom", GameConstants.SUNFLOWER_COST, "SunFlower.png"),
    WALL_NUT("Stone Guard", GameConstants.WALLNUT_COST, "WallNut.png"),
    SNOW_PEA("Frost Pod", GameConstants.SNOW_PEA_COST, "SnowPea.jpg"),
    CHERRY_BOMB("Burst Bud", GameConstants.CHERRY_BOMB_COST, "CherryBomb.jpg");

    private final String displayName;
    private final int cost;
    private final String assetFileName;

    PlantType(String displayName, int cost, String assetFileName) {
        this.displayName = displayName;
        this.cost = cost;
        this.assetFileName = assetFileName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getCost() {
        return cost;
    }

    public String getAssetFileName() {
        return assetFileName;
    }
}
