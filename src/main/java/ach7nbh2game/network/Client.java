package ach7nbh2game.network;

import ach7nbh2game.server.Server;
import com.googlecode.blacken.colors.ColorNames;
import com.googlecode.blacken.colors.ColorPalette;
import com.googlecode.blacken.grid.Grid;
import com.googlecode.blacken.grid.Point;
import com.googlecode.blacken.swing.SwingTerminal;
import com.googlecode.blacken.terminal.*;

import java.util.ArrayList;
import java.util.EnumSet;

class Client {

    private Server server;

    public static void main (String[] args) {

        Client client = new Client();
        client.init();
        client.run();

    }

    private Grid<Integer> grid;

    private CursesLikeAPI terminal;

    private final static int EMPTY_FLOOR = 0x2e;

    private Point player = new Point(-1, -1);
    private Point upperLeft = new Point(0, 0);
    private final static Point MAP_START = new Point(1, 0);
    private final static Point MAP_END = new Point(-1, 0);

    public Client () {

        grid = new Grid<Integer>(EMPTY_FLOOR, 20, 20);

        server = new Server();

    }

    public void init () {

        TerminalInterface newTerminal = new SwingTerminal();
        newTerminal.init("Andrew Nick Game", 25, 25);
        terminal = new CursesLikeAPI(newTerminal);

        ColorPalette palette = new ColorPalette();
        palette.addAll(ColorNames.XTERM_256_COLORS, false);
        palette.putMapping(ColorNames.SVG_COLORS);
        terminal.setPalette(palette);

    }

    public void run () {

        terminal.move(-1, -1);

        while (true) {

            showMap();

            terminal.setCursorLocation(player.getY() - upperLeft.getY() + MAP_START.getY(),
                    player.getX() - upperLeft.getX() + MAP_START.getX());

            terminal.refresh();

            int ch = terminal.getch();
            if (ch == BlackenKeys.RESIZE_EVENT) {
                //this.refreshScreen();
                continue;
            }

        }

    }

    private void showMap() {

        ArrayList<ArrayList<Integer>> mapView = server.getMapView(5, 5, 10, 10);

        for (int i = 0; i < mapView.size(); i++) {
            ArrayList<Integer> row = mapView.get(i);
            for (int j = 0; j < row.size(); j++) {
                terminal.set(i, j, new String(Character.toChars(row.get(j))), 7, 0,
                        EnumSet.noneOf(TerminalStyle.class), EnumSet.noneOf(CellWalls.class));
            }
        }

    }

}
