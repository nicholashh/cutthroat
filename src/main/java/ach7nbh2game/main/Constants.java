package ach7nbh2game.main;

public class Constants {

    // client display settings
    public static final int clientMapHeight = 21;
    public static final int clientMapWidth = 61;

    // server world map settings
    public static final int mapHeight = 31;
    public static final int mapWidth = 81;

    // client action helper definitions
    public enum Action {MOVE, SHOOT}
    public enum Direction {UP, LEFT, DOWN, RIGHT}

    // client stat definitions
    public static final int clientHealth = 100;

    // bullet tiers
    public static final int bulletTier1 = 25;

    // game time-sensitive settings
    public static final int serverTicksPerSecond = 30;
    public static final int clientUpdatesPerSecond = 30;
    public static final int clientUpdatesFrequency = serverTicksPerSecond / clientUpdatesPerSecond;

    // networking options
    public static final int bufferSize = 20000000;

}
