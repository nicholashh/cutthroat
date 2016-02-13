package ach7nbh2game.client;

import ach7nbh2game.client.adapters.IViewToModel;
import ach7nbh2game.main.Constants;
import ach7nbh2game.main.Constants.*;
import ach7nbh2game.network.packets.ClientAction;
import ach7nbh2game.network.packets.GameState;
import ach7nbh2game.util.Logger;
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

    private final int moveUp = BlackenKeys.KEY_UP;
    private final int moveLeft = BlackenKeys.KEY_LEFT;
    private final int moveDown = BlackenKeys.KEY_DOWN;
    private final int moveRight = BlackenKeys.KEY_RIGHT;

    private final int gunUp = 'w';
    private final int gunLeft = 'a';
    private final int gunDown = 's';
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

        Logger.Singleton.log(this, 0, "setUpTerminal:");

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

    public void showMap (ArrayList<ArrayList<Integer>> map) {

        Logger.Singleton.log(this, 0, "showMap:");
        Logger.Singleton.log(this, 1, "map = " + map);

        showSomething(map,
                0, Constants.clientMapHeight, 0,
                0, Constants.clientMapWidth, 0);

        try {
            terminal.refresh();
        } catch (IndexOutOfBoundsException e) {
            // TODO why is this breaking?
            System.err.println("error in showMap(): " + e.toString());
        }

    }

    private ArrayList<Integer> stringToInts (String string) {

        ArrayList<Integer> ints = new ArrayList<>();
        for (char c : string.toCharArray()) ints.add((int)c);
        return ints;
    }

    private void showWelcomeMessage () {

        ArrayList<ArrayList<Integer>> message = new ArrayList<>();
        message.add(stringToInts(" "));
        message.add(stringToInts(" Welcome"));
        message.add(stringToInts(" to Cutthroat!"));

        showMessage(message, true);
    }

    public void showScores (GameState gameState) {

        Logger.Singleton.log(this, 0, "showScores:");
        Logger.Singleton.log(this, 1, "gameState = " + gameState);

        Map<String, Integer> scores = gameState.getScores();

        ArrayList<ArrayList<Integer>> message = new ArrayList<>();
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

        try {
            terminal.refresh();
        } catch (IndexOutOfBoundsException e) {
            // TODO why is this breaking?
            System.err.println("error in showScores(): " + e.toString());
        }

    }

    private void clearMessageArea () {

        ArrayList<Integer> row = new ArrayList<>();
        for (int i = 0; i < Constants.clientSidebarWidth; i++) {
            row.add((int)' ');
        }

        ArrayList<ArrayList<Integer>> message = new ArrayList<>();
        for (int i = 0; i < Constants.clientMapHeight; i++) {
            message.add(row);
        }

        showMessage(message, false);
    }

    private void showMessage (ArrayList<ArrayList<Integer>> message, boolean shouldClear) {

        Logger.Singleton.log(this, 0, "showMessage:");
        Logger.Singleton.log(this, 1, "message = " + message);
        Logger.Singleton.log(this, 1, "shouldClear = " + shouldClear);

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

        Logger.Singleton.log(this, 0, "beginAcceptingCharacterInput:");

        (new Thread() { public void run () {

            ClientAction action = new ClientAction();

            while (true) {

                int input = terminal.getch(); // BLOCKING

                switch (input) {

                    case moveUp:
                        action.setAction(Action.MOVE);
                        action.setDirection(Direction.UP);
                        model.action(action);
                        break;
                    case moveLeft:
                        action.setAction(Action.MOVE);
                        action.setDirection(Direction.LEFT);
                        model.action(action);
                        break;
                    case moveDown:
                        action.setAction(Action.MOVE);
                        action.setDirection(Direction.DOWN);
                        model.action(action);
                        break;
                    case moveRight:
                        action.setAction(Action.MOVE);
                        action.setDirection(Direction.RIGHT);
                        model.action(action);
                        break;

                    case gunUp:
                        action.setAction(Action.SHOOT);
                        action.setDirection(Direction.UP);
                        model.action(action);
                        break;
                    case gunLeft:
                        action.setAction(Action.SHOOT);
                        action.setDirection(Direction.LEFT);
                        model.action(action);
                        break;
                    case gunDown:
                        action.setAction(Action.SHOOT);
                        action.setDirection(Direction.DOWN);
                        model.action(action);
                        break;
                    case gunRight:
                        action.setAction(Action.SHOOT);
                        action.setDirection(Direction.RIGHT);
                        model.action(action);
                        break;
                }
            }
        }}).start();
    }
}
