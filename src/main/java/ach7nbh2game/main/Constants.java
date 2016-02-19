package ach7nbh2game.main;

public class Constants {

    // client map view settings
    public static final int clientMapHeight = 11;
    public static final int clientMapWidth = 21;
    public static final int clientSidebarWidth = 15;
    public static final int clientWidthTotal = clientMapWidth + clientSidebarWidth;

    // server world map settings
    public static final int mapHeight = 11;
    public static final int mapWidth = 31;

    // client action helper definitions
    public enum Action {MOVE, SHOOT}
    public enum Direction {UP, LEFT, DOWN, RIGHT}

    // game time-sensitive settings
    public static final int serverTicksPerSecond = 30;
    public static final int clientUpdatesPerSecond = 30;
    public static final int clientUpdatesFrequency = serverTicksPerSecond / clientUpdatesPerSecond;

    // notworking options
    public static final int bufferSize = 300000000;

}
