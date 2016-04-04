package ach7nbh2game.main;

public class Constants {

    // client display settings
    public static final int clientMapHeight = 21;
    public static final int clientMapWidth = 63;

    // server world map settings
    public static final int mapHeight = 31;
    public static final int mapWidth = 81;

    // client action helper definitions
    public enum Action {MOVE, SHOOT_GUN, DIG, SHOOT_ROCKET}
    public enum Direction {UP, LEFT, DOWN, RIGHT}

    // client stat definitions
    public static final int clientHealth = 100;

    // bullet tiers
    public static final int bullet1 = 25;
    public static final int bulletBatchSize = 3;
    public static final int rocketBatchSize = 1;

    // gun tiers
    public static final int gun1 = 1;
    public static final int gun2 = 2;

    // rocket launcher tiers
    public static final int rocket1 = 120;

    // pickaxe tiers
    public static final int pickaxe1 = 10;
    public static final int pickaxe2 = 20;
    public static final int pickaxe3 = 30;

    // wall vars
    public static final int wall1 = 60;
    public static final double bulletDropFreq = 0.5;
    public static final double rocketDropFreq = 0.25;
    public static final double healthDropFreq = 0.25;

    // pickups
    public static final int healthPack = 50;

    // cavern tiers
    public enum CavernSize {LARGE, MEDIUM}

    // all the items
    public enum Item {GUN1, GUN2, RL0, RL1, ROCKET, PICK1, PICK2, PICK3, BULLET1, HEALTH}

    // use selected tool
    public enum Tool {GUN, PICKAXE, ROCEKT}

    // game time-sensitive settings
    public static final int serverTicksPerSecond = 30;
    public static final int clientUpdatesPerSecond = 30;
    public static final int clientUpdatesFrequency = serverTicksPerSecond / clientUpdatesPerSecond;

    // networking options
    public static final int bufferSize = 20000000;

    // client-side sounds
    public interface GameSound {}
    public enum ServerToClientSound implements GameSound {
        GUN_FIRE, GUN_WHIFF,
        PICKAXE_HIT_WALL, PICKAXE_HIT_PLAYER, PICKAXE_WHIFF,
        BULLET_HIT_WALL, BULLET_HIT_PLAYER, BULLET_HIT_BULLET,
        PICKUP_ITEM,
        PLAYER_SPAWNS, PLAYER_DIES,
        ROCKET_LAUNCH, ROCKET_EXPLODE}

    // game mechanics tuning parameters
    public static final int killsToWin = 2;
    public static double bountyPercent = 0.15;
    public static final int initAmmo = 10;
    public static final int initRocketAmmo = 1;

    // miscellaneous sugar
    public static String defaultHostname = "cutthroat.pwnz.org";

}
