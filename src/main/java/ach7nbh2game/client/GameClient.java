package ach7nbh2game.client;

import ach7nbh2game.main.Constants;
import ach7nbh2game.server.GameServer;
import com.googlecode.blacken.colors.ColorNames;
import com.googlecode.blacken.colors.ColorPalette;
import com.googlecode.blacken.grid.Point;
import com.googlecode.blacken.swing.SwingTerminal;
import com.googlecode.blacken.terminal.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

public class GameClient {

    private String name;
    private GameServer server;
    private int clientID;
    private int gameID;

    private CursesLikeAPI terminal;

    private Point player = new Point(-1, -1);
    private Point upperLeft = new Point(0, 0);
    private final static Point MAP_START = new Point(1, 0);
    private final static Point MAP_END = new Point(-1, 0);

    private final int keyUp = BlackenKeys.KEY_UP;
    private final int keyDown = BlackenKeys.KEY_DOWN;
    private final int keyLeft = BlackenKeys.KEY_LEFT;
    private final int keyRight = BlackenKeys.KEY_RIGHT;

    public GameClient (String nameIn, GameServer serverIn) {

        name = nameIn;

        server = serverIn;
        clientID = server.registerNewClient(this);

        // FOR TESTING ONLY
        if (name.equals("Client A")) {
            int lobbyID = server.createNewLobby();
            server.joinLobby(clientID, lobbyID);
        } else if (name.equals("Client B")) {
            Set<Integer> lobbyIDSet = server.getLobbies();
            int lobbyID = lobbyIDSet.toArray(new Integer[lobbyIDSet.size()])[0];
            server.joinLobby(clientID, lobbyID);
            server.startGame(lobbyID);
        }

        // FOR TESTING ONLY
        //if (name.equals("Client A")) {
        //    int lobbyID = server.createNewLobby();
        //    server.joinLobby(clientID, lobbyID);
        //    server.startGame(lobbyID);
        //}

    }

    public void enterGame (int gameIDIn) {

        gameID = gameIDIn;

        TerminalInterface newTerminal = new SwingTerminal();
        newTerminal.init("Andrew Nick Game", Constants.clientHeight + 1, Constants.clientWidth + 1);
        terminal = new CursesLikeAPI(newTerminal);

        ColorPalette palette = new ColorPalette();
        palette.addAll(ColorNames.XTERM_256_COLORS, false);
        palette.putMapping(ColorNames.SVG_COLORS);
        terminal.setPalette(palette);

        terminal.move(-1, -1);

        while (true) {

            //System.out.println(name + " in loop");

            showMap();

            //terminal.setCursorLocation(player.getY() - upperLeft.getY() + MAP_START.getY(),
            //        player.getX() - upperLeft.getX() + MAP_START.getX());

            terminal.refresh();

            int input = terminal.getch(); // BLOCKING

            switch (input) {
                case keyUp:
                    server.moveUp(clientID, gameID);
                    break;
                //case keyDown:
                //    server.moveDown(clientID, gameID);
                //    break;
                //case keyLeft:
                //    server.moveLeft(clientID, gameID);
                //    break;
                //case keyRight:
                //    server.moveRight(clientID, gameID);
                //    break;
            }

        }

    }

    private void showMap () {

        ArrayList<ArrayList<Integer>> mapView = server.getMapView(clientID, gameID);

        //System.out.println("mapView");
        //for (int i = 0; i < mapView.size(); i++) {
        //    System.out.println(mapView.get(i));
        //}

        //System.out.println("terminal");
        //for (int i = 0; i < terminal.getHeight(); i++) {
        //    System.out.print("[");
        //    for (int j = 0; j < terminal.getWidth(); j++) {
        //        System.out.print(" " + terminal.get(i, j).getBackground());
        //    }
        //    System.out.println(" ]");
        //}

        for (int y = 0; y < Constants.clientHeight; y++) {
            if (y < mapView.size() && y < terminal.getHeight()) {
                ArrayList<Integer> row = mapView.get(y);
                for (int x = 0; x < Constants.clientWidth; x++) {
                    if (x < row.size() && x < terminal.getWidth()) {

                        //System.out.println("Constants.clientHeight = " + Constants.clientHeight);
                        //System.out.println("Constants.clientWidth = " + Constants.clientWidth);
                        //System.out.println("terminal.getHeight() = " + terminal.getHeight());
                        //System.out.println("terminal.getWidth() = " + terminal.getWidth());
                        //System.out.println("mapView.size() = " + mapView.size());
                        //System.out.println("row.size() = " + row.size());

                        setTerminal(x, y, row.get(x));

                    }
                }
            }
        }

    }

    private void setTerminal (int x, int y, int character) {

        //System.out.println("x = " + x + ", y = " + y);

        terminal.set(y, x, new String(Character.toChars(character)), 7, 0,
                EnumSet.noneOf(TerminalStyle.class), EnumSet.noneOf(CellWalls.class));

    }

}
