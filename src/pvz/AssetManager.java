package pvz;

import java.awt.Color;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {
    private final Map<String, Image> images;

    public AssetManager() {
        images = new HashMap<>();
        loadAllAssets();
    }

    private void loadAllAssets() {
        images.put("BasicZombie.jpg", GardenArt.creature(new Color(77, 99, 141), new Color(160, 231, 249), 45, 2));
        images.put("CherryBomb.jpg", GardenArt.burstBud());
        images.put("FastZombie.jpg", GardenArt.creature(new Color(118, 81, 138), new Color(248, 208, 122), 40, -5));
        images.put("IconImage.png", GardenArt.sun());
        images.put("menu.png", GardenArt.ground(new Color(38, 70, 64), new Color(15, 33, 43)));
        images.put("PeaShooter.jpg", GardenArt.turret(new Color(76, 166, 138), new Color(175, 234, 160)));
        images.put("RunZombie.jpg", GardenArt.creature(new Color(175, 94, 77), new Color(237, 214, 141), 37, -1));
        images.put("Shovel.png", GardenArt.tool());
        images.put("ShovelBack.png", GardenArt.ground(new Color(69, 80, 67), new Color(34, 54, 56)));
        images.put("SnowPea.jpg", GardenArt.turret(new Color(64, 147, 181), new Color(163, 233, 251)));
        images.put("Sun.gif", getWindowIconImage());
        images.put("sunBoard.png", images.get("ShovelBack.png"));
        images.put("SunFlower.png", GardenArt.bloom());
        images.put("TankZombie.jpg", GardenArt.creature(new Color(58, 110, 111), new Color(255, 215, 111), 62, 10));
        images.put("WallNut.png", GardenArt.stone());
        images.put("yard.jpg", GardenArt.ground(new Color(85, 129, 89), new Color(45, 91, 75)));
    }

    public Image getImage(String fileName) {
        return images.get(fileName);
    }

    public Image getBasicZombieImage() {
        return getImage("BasicZombie.jpg");
    }

    public Image getCherryBombImage() {
        return getImage("CherryBomb.jpg");
    }

    public Image getFastZombieImage() {
        return getImage("FastZombie.jpg");
    }

    public Image getWindowIconImage() {
        return getImage("IconImage.png");
    }

    public Image getMenuBackgroundImage() {
        return getImage("menu.png");
    }

    public Image getPeaShooterImage() {
        return getImage("PeaShooter.jpg");
    }

    public Image getRunZombieImage() {
        return getImage("RunZombie.jpg");
    }

    public Image getShovelImage() {
        return getImage("Shovel.png");
    }

    public Image getShovelBackImage() {
        return getImage("ShovelBack.png");
    }

    public Image getSnowPeaImage() {
        return getImage("SnowPea.jpg");
    }

    public Image getSunImage() {
        return getImage("Sun.gif");
    }

    public Image getSunBoardImage() {
        return getImage("sunBoard.png");
    }

    public Image getSunFlowerImage() {
        return getImage("SunFlower.png");
    }

    public Image getTankZombieImage() {
        return getImage("TankZombie.jpg");
    }

    public Image getWallNutImage() {
        return getImage("WallNut.png");
    }

    public Image getYardImage() {
        return getImage("yard.jpg");
    }

    public Image getPlantImage(PlantType plantType) {
        return switch (plantType) {
            case PEA_SHOOTER -> getPeaShooterImage();
            case SUN_FLOWER -> getSunFlowerImage();
            case WALL_NUT -> getWallNutImage();
            case SNOW_PEA -> getSnowPeaImage();
            case CHERRY_BOMB -> getCherryBombImage();
        };
    }

    public Image getZombieImage(String zombieType) {
        return switch (zombieType) {
            case "BasicZombie" -> getBasicZombieImage();
            case "FastZombie" -> getFastZombieImage();
            case "RunZombie" -> getRunZombieImage();
            case "TankZombie" -> getTankZombieImage();
            default -> null;
        };
    }
}
