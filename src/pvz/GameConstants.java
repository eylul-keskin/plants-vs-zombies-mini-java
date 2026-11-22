package pvz;

public final class GameConstants {
    public static final int ROWS = 5;
    public static final int COLS = 9;

    public static final int CELL_WIDTH = 100;
    public static final int CELL_HEIGHT = 90;

    public static final int BOARD_X = 90;
    public static final int BOARD_Y = 40;
    public static final int BOARD_WIDTH = COLS * CELL_WIDTH;
    public static final int BOARD_HEIGHT = ROWS * CELL_HEIGHT;

    public static final int SELECTION_PANEL_HEIGHT = 160;
    public static final int GAME_PANEL_WIDTH = 1120;
    public static final int GAME_PANEL_HEIGHT = 560;
    public static final int FRAME_WIDTH = 1140;
    public static final int FRAME_HEIGHT = 790;

    public static final int UPDATE_DELAY = 40;

    public static final int INITIAL_SUN = 250;
    public static final int SUN_VALUE = 25;
    public static final int SUN_SIZE = 42;

    public static final int BULLET_SIZE = 14;
    public static final int PEA_DAMAGE = 20;
    public static final double BULLET_SPEED = 9.0;
    public static final double SNOW_BULLET_SPEED = 8.0;

    public static final int PEA_SHOOTER_FIRE_INTERVAL = 1200;
    public static final int SNOW_PEA_FIRE_INTERVAL = 1400;
    public static final int SUNFLOWER_SUN_INTERVAL = 7000;
    public static final int CHERRY_BOMB_FUSE = 1200;

    public static final int PEA_SHOOTER_HEALTH = 100;
    public static final int SUNFLOWER_HEALTH = 90;
    public static final int WALLNUT_HEALTH = 450;
    public static final int SNOW_PEA_HEALTH = 100;
    public static final int CHERRY_BOMB_HEALTH = 60;

    public static final int PEA_SHOOTER_COST = 100;
    public static final int SUNFLOWER_COST = 50;
    public static final int WALLNUT_COST = 50;
    public static final int SNOW_PEA_COST = 175;
    public static final int CHERRY_BOMB_COST = 150;

    public static final int BASIC_ZOMBIE_HEALTH = 120;
    public static final int FAST_ZOMBIE_HEALTH = 90;
    public static final int RUN_ZOMBIE_HEALTH = 80;
    public static final int TANK_ZOMBIE_HEALTH = 280;

    public static final double BASIC_ZOMBIE_SPEED = 0.70;
    public static final double FAST_ZOMBIE_SPEED = 1.00;
    public static final double RUN_ZOMBIE_SPEED = 1.25;
    public static final double TANK_ZOMBIE_SPEED = 0.50;

    public static final int ZOMBIE_ATTACK_DAMAGE = 10;
    public static final int ZOMBIE_ATTACK_INTERVAL = 700;
    public static final int SLOW_DURATION = 2500;
    public static final double SLOW_MULTIPLIER = 0.50;

    public static final int ZOMBIE_WIDTH = 78;
    public static final int ZOMBIE_HEIGHT = 92;
    public static final int PLANT_WIDTH = 70;
    public static final int PLANT_HEIGHT = 74;

    public static final int NORMAL_SPAWN_INTERVAL = 5000;
    public static final int NORMAL_SPAWNS_PER_WAVE_TRIGGER = 8;
    public static final int TOTAL_WAVES = 2;

    public static final int WAVE_ONE_TOTAL_SPAWNS = 6;
    public static final int WAVE_TWO_TOTAL_SPAWNS = 8;
    public static final int WAVE_ONE_DELAY = 1800;
    public static final int WAVE_TWO_DELAY = 1200;
    public static final double WAVE_ONE_HEALTH_MULTIPLIER = 1.20;
    public static final double WAVE_TWO_HEALTH_MULTIPLIER = 1.55;

    public static final String SAVE_FILE_PATH = "saves/savegame.txt";

    public static final String[] WAVE_ONE_ZOMBIES = {"BasicZombie", "FastZombie", "RunZombie"};
    public static final String[] WAVE_TWO_ZOMBIES = {"BasicZombie", "FastZombie", "RunZombie", "TankZombie"};

    private GameConstants() {
    }
}
