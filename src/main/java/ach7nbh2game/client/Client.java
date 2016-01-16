package ach7nbh2game.client;

import com.googlecode.blacken.colors.ColorNames;
import com.googlecode.blacken.colors.ColorPalette;
import com.googlecode.blacken.grid.Grid;
import com.googlecode.blacken.grid.Point;
import com.googlecode.blacken.swing.SwingTerminal;
import com.googlecode.blacken.terminal.*;

import java.util.EnumSet;

public class Client {

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

    }

    public void init () {

        TerminalInterface newTerminal = new SwingTerminal();
        newTerminal.init("Andrew Nick Game", 25, 25);
        terminal = new CursesLikeAPI(newTerminal);

        ColorPalette palette = new ColorPalette();
        palette.addAll(ColorNames.XTERM_256_COLORS, false);
        palette.putMapping(ColorNames.SVG_COLORS);
        terminal.setPalette(palette);

        makeMap();

    }

    private void makeMap() {

        grid.clear();

    }

    public void run () {

        //int ch = BlackenKeys.NO_KEY;
        //int mod;

        //updateStatus();
        //movePlayerBy(0,0);
        //this.message = "Welcome to Stumble!";

        terminal.move(-1, -1);

        while (true) {

            //placeIt(0x23);

            //if (dirtyStatus) {
            //    updateStatus();
            //}
            //updateMessage(false);

            showMap();

            terminal.setCursorLocation(player.getY() - upperLeft.getY() + MAP_START.getY(),
                    player.getX() - upperLeft.getX() + MAP_START.getX());

            //terminal.getPalette().rotate(0xee, 10, +1);

            terminal.refresh();

            //mod = BlackenKeys.NO_KEY;

            int ch = terminal.getch();
            if (ch == BlackenKeys.RESIZE_EVENT) {
                //this.refreshScreen();
                continue;
            }

            //else if (BlackenKeys.isModifier(ch)) {
            //    //mod = ch;
            //    //ch = terminal.getch();
            //}

            // LOGGER.debug("Processing key: {}", ch);
            //if (ch != BlackenKeys.NO_KEY) {
            //    this.message = null;
            //    doAction(mod, ch);
            //}

        }

    }

    private void showMap() {

        int ey = MAP_END.getY();
        int ex = MAP_END.getX();

        if (ey <= 0) {
            ey += terminal.getHeight();
        }
        if (ex <= 0) {
            ex += terminal.getWidth();
        }

        for (int y = MAP_START.getY(); y < ey; y++) {
            for (int x = MAP_START.getX(); x < ex; x++) {

                int y1 = y + upperLeft.getY() - MAP_START.getY();
                int x1 = x + upperLeft.getX() - MAP_START.getX();

                int what = ' ';
                if (y1 >= 0 && x1 >= 0 && y1 < grid.getHeight() && x1 < grid.getWidth()) {
                    what = grid.get(y1, x1);
                }

                int fclr = 7;
                int bclr = 0;

                EnumSet<CellWalls> walls = EnumSet.noneOf(CellWalls.class);
                terminal.set(y, x, new String(Character.toChars(what)),
                        fclr, bclr, EnumSet.noneOf(TerminalStyle.class), walls);

            }
        }

    }

}
