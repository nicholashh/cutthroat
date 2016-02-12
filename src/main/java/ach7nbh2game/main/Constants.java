package ach7nbh2game.main;

public class Constants {

    public static final int clientMapHeight = 21;
    public static final int clientMapWidth = 61;
    public static final int clientSidebarWidth = 15;
    public static final int clientWidthTotal = clientMapWidth + clientSidebarWidth;

    public static final int mapHeight = 21;
    public static final int mapWidth = 61;

    // TODO this is awful, make this better after the demo :P
    public enum Direction {UP, LEFT, DOWN, RIGHT}

    public enum Action {MOVE, SHOOT}

}
