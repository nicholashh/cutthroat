package ach7nbh2game.client;

import ach7nbh2game.client.adapters.IViewToModel;
import ach7nbh2game.main.Constants;
import ach7nbh2game.server.GameState;
import com.googlecode.blacken.colors.ColorNames;
import com.googlecode.blacken.colors.ColorPalette;
import com.googlecode.blacken.swing.SwingTerminal;
import com.googlecode.blacken.terminal.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Map;

public class ClientView {

    private IViewToModel model;

    private CursesLikeAPI terminal;

    private final int keyUp = BlackenKeys.KEY_UP;
    private final int keyDown = BlackenKeys.KEY_DOWN;
    private final int keyLeft = BlackenKeys.KEY_LEFT;
    private final int keyRight = BlackenKeys.KEY_RIGHT;

    private final int gunUp = 'w';
    private final int gunDown = 's';
    private final int gunLeft = 'a';
    private final int gunRight = 'd';

    public ClientView (IViewToModel modelIn) {

        System.out.println("making new ClientView...");

        model = modelIn;

    }

    public void start () {

        System.out.println("starting the ClientView!");

        setUpTerminal();
        showWelcomeMessage();
        beginAcceptingCharacterInput();

    }

    private void setUpTerminal () {

        System.out.println("setUpTerminal()");

        TerminalInterface newTerminal = new SwingTerminal();
        newTerminal.init("Andrew Nick Game",
                Constants.clientMapHeight + 1,
                Constants.clientWidthTotal + 1);
        terminal = new CursesLikeAPI(newTerminal);

        ColorPalette palette = new ColorPalette();
        palette.addAll(ColorNames.XTERM_256_COLORS, false);
        palette.putMapping(ColorNames.SVG_COLORS);
        terminal.setPalette(palette);

        terminal.move(-1, -1);

    }

    public void showMap(ArrayList<ArrayList<Integer>> map) {

        showSomething(map,
                0, Constants.clientMapHeight, 0,
                0, Constants.clientMapWidth, 0);

        terminal.refresh();

    }

    private ArrayList<Integer> stringToInts (String string) {

        ArrayList<Integer> ints = new ArrayList<Integer>();
        for (char c : string.toCharArray()) ints.add((int)c);
        return ints;

    }

    private void showWelcomeMessage () {

        ArrayList<ArrayList<Integer>> message = new ArrayList<ArrayList<Integer>>();
        message.add(stringToInts(" "));
        message.add(stringToInts(" Welcome"));
        message.add(stringToInts(" to Cutthroat!"));

        showMessage(message, true);

    }

    public void showScores(GameState gameState) {

        Map<String, Integer> scores = gameState.getScores();

        ArrayList<ArrayList<Integer>> message = new ArrayList<ArrayList<Integer>>();
        message.add(stringToInts(" "));
        message.add(stringToInts(" Scores:"));
        message.add(stringToInts(" "));

        for (String player : scores.keySet()) {
            message.add(stringToInts(" " + player + ": " + scores.get(player)));
        }

        message.add(stringToInts(" "));
        message.add(stringToInts(" Who's \"it\"?"));
        message.add(stringToInts(" " + gameState.getWhoItIs()));

        message.add(stringToInts(" "));
        message.add(stringToInts(" Time Left: " + gameState.getTimeRemaining()));

        showMessage(message, true);

        terminal.refresh();

    }

    private void clearMessageArea () {

        ArrayList<Integer> row = new ArrayList<Integer>();
        for (int i = 0; i < Constants.clientSidebarWidth; i++) {
            row.add((int)' ');
        }

        ArrayList<ArrayList<Integer>> message = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < Constants.clientMapHeight; i++) {
            message.add(row);
        }

        showMessage(message, false);

    }

    private void showMessage (ArrayList<ArrayList<Integer>> message, boolean shouldClear) {

        if (shouldClear) {
            clearMessageArea();
        }

        showSomething(message,
                0, Constants.clientMapHeight, 0,
                0, Constants.clientSidebarWidth, Constants.clientMapWidth);

    }

    private void showSomething (ArrayList<ArrayList<Integer>> thing,
            int yLow, int yHigh, int yOffset, int xLow, int xHigh, int xOffset) {

        for (int y = yLow; y < yHigh; y++) {
            if (y < thing.size()) {
                ArrayList<Integer> row = thing.get(y);
                for (int x = xLow; x < xHigh; x++) {
                    if (x < row.size()) {

                        setTerminal(x + xOffset, y + yOffset, row.get(x));

                    }
                }
            }
        }

    }

    private void setTerminal (int x, int y, int character) {

        try { if (y < terminal.getHeight() && x < terminal.getWidth()) {

            terminal.set(y, x, new String(Character.toChars(character)), 7, 0,
                    EnumSet.noneOf(TerminalStyle.class), EnumSet.noneOf(CellWalls.class));

        } } catch (IndexOutOfBoundsException e) {
            // TODO this is not ideal, but it almost never happens, sooooo...
            System.err.println("error in setTerminal(): " + e.toString());
        }

    }

    private void beginAcceptingCharacterInput() {

        System.out.println("beginAcceptingCharacterInput()");

        (new Thread() { public void run () {

            while (true) {

                int input = terminal.getch(); // BLOCKING

                switch (input) {

                    case keyUp:    model.move(Constants.Directions.UP);    break;
                    case keyDown:  model.move(Constants.Directions.DOWN);  break;
                    case keyLeft:  model.move(Constants.Directions.LEFT);  break;
                    case keyRight: model.move(Constants.Directions.RIGHT); break;

                    case gunUp:    model.move(Constants.Directions.GUN_UP);    break;
                    case gunDown:  model.move(Constants.Directions.GUN_DOWN);  break;
                    case gunLeft:  model.move(Constants.Directions.GUN_LEFT);  break;
                    case gunRight: model.move(Constants.Directions.GUN_RIGHT); break;

                }

            }

        }}).start();

    }

}
