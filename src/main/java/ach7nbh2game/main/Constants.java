package ach7nbh2game.main;

public class Constants {

    // client display settings
    public static final int clientMapHeight = 21;
    public static final int clientMapWidth = 61;

    // server world map settings
    public static final int mapHeight = 31;
    public static final int mapWidth = 81;

    // client action helper definitions
    public enum Action {MOVE, SHOOT, DIG}
    public enum Direction {UP, LEFT, DOWN, RIGHT}

    // client stat definitions
    public static final int clientHealth = 100;
    public static final int initAmmo = 10;

    // bullet tiers
    public static final int bullet1 = 25;

    // gun tiers
    public static final int gun1 = 1;
    public static final int gun2 = 2;

    // pickaxe tiers
    public static final int pickaxe1 = 10;
    public static final int pickaxe2 = 20;
    public static final int pickaxe3 = 30;

    // wall tiers
    public static final int wall1 = 60;

    // cavern tiers
    public enum CavernSize {LARGE, MEDIUM}

    // all the items
    public enum Item {GUN1, GUN2, PICK1, PICK2, PICK3, BULLET1}
    //public static final HashMap<Item, Integer> itemToTier = new HashMap<>();

    // use selected tool
    public enum Tool {GUN, PICKAXE}

    // game time-sensitive settings
    public static final int serverTicksPerSecond = 30;
    public static final int clientUpdatesPerSecond = 30;
    public static final int clientUpdatesFrequency = serverTicksPerSecond / clientUpdatesPerSecond;

    // networking options
    public static final int bufferSize = 20000000;

    // client-side sounds
    public enum GameSound {
        GUN_FIRE, GUN_WHIFF,
        PICKAXE_HIT_WALL, PICKAXE_HIT_PLAYER, PICKAXE_WHIFF,
        BULLET_HIT_WALL, BULLET_HIT_PLAYER,
        PLAYER_SPAWNS, PLAYER_DIES,
        GAME_BEGINS, GAME_ENDS}

}
