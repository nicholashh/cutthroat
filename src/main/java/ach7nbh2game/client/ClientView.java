package ach7nbh2game.client;

import ach7nbh2game.client.Window.Component;
import ach7nbh2game.client.adapters.IViewToModel;
import ach7nbh2game.main.Constants.Action;
import ach7nbh2game.main.Constants.Direction;
import ach7nbh2game.network.packets.ClientAction;
import ach7nbh2game.network.packets.GameState;
import com.googlecode.blacken.terminal.BlackenKeys;

import java.util.ArrayList;

public class ClientView {

    private IViewToModel model;

    private Window window = new Window();

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

    public Window getWindow() {
        return window;
    }

    public void start () {

        System.out.println("starting the ClientView!");

        window.start();
        beginAcceptingCharacterInput();

    }

    public void showMap (ArrayList<ArrayList<Integer>> map) {

        window.fill(Component.CenterPanel, map);

        // re-paint the window
        window.repaint();

    }

    //private ArrayList<Integer> stringToInts (String string) {
    //
    //    ArrayList<Integer> ints = new ArrayList<>();
    //    for (char c : string.toCharArray()) ints.add((int)c);
    //    return ints;
    //}

    //private void showWelcomeMessage () {
    //
    //    ArrayList<ArrayList<Integer>> message = new ArrayList<>();
    //    message.add(stringToInts(" "));
    //    message.add(stringToInts(" Welcome"));
    //    message.add(stringToInts(" to Cutthroat!"));
    //
    //    showMessage(message, true);
    //
    //}

    public void showScores (GameState gameState) {

        //Logger.Singleton.log(this, 0, "showScores:");
        //Logger.Singleton.log(this, 1, "gameState = " + gameState);
        //
        //Map<String, Integer> scores = gameState.getScores();
        //
        //ArrayList<ArrayList<Integer>> message = new ArrayList<>();
        //message.add(stringToInts(" "));
        //message.add(stringToInts(" Scores:"));
        //message.add(stringToInts(" "));
        //
        //for (String player : scores.keySet()) {
        //    message.add(stringToInts(" " + player + ": " + scores.get(player)));
        //}
        //
        //message.add(stringToInts(" "));
        //message.add(stringToInts(" Who's \"it\"?"));
        //message.add(stringToInts(" " + gameState.getWhoItIs()));
        //
        //message.add(stringToInts(" "));
        //message.add(stringToInts(" Time Left: " + gameState.getTimeRemaining()));
        //
        //showMessage(message, true);
        //
        //try {
        //    terminal.refresh();
        //} catch (IndexOutOfBoundsException e) {
        //    // TODO why is this breaking?
        //    System.err.println("error in showScores(): " + e.toString());
        //}

    }

    //private void clearMessageArea () {
    //
    //    ArrayList<Integer> row = new ArrayList<>();
    //    for (int i = 0; i < Constants.clientSidebarWidth; i++) {
    //        row.add((int)' ');
    //    }
    //
    //    ArrayList<ArrayList<Integer>> message = new ArrayList<>();
    //    for (int i = 0; i < Constants.clientMapHeight; i++) {
    //        message.add(row);
    //    }
    //
    //    showMessage(message, false);
    //
    //}

    //private void showMessage (ArrayList<ArrayList<Integer>> message, boolean shouldClear) {
    //
    //    Logger.Singleton.log(this, 0, "showMessage:");
    //    Logger.Singleton.log(this, 1, "message = " + message);
    //    Logger.Singleton.log(this, 1, "shouldClear = " + shouldClear);
    //
    //    if (shouldClear) {
    //        clearMessageArea();
    //    }
    //
    //    showSomething(message,
    //            0, Constants.clientMapHeight, 0,
    //            0, Constants.clientSidebarWidth, Constants.clientMapWidth);
    //
    //}

    //private void showSomething (ArrayList<ArrayList<Integer>> thing,
    //        int yLow, int yHigh, int yOffset, int xLow, int xHigh, int xOffset) {
    //    for (int y = yLow; y < yHigh; y++) {
    //        if (y < thing.size()) {
    //            ArrayList<Integer> row = thing.get(y);
    //            for (int x = xLow; x < xHigh; x++) {
    //                if (x < row.size()) {
    //
    //                    setTerminal(x + xOffset, y + yOffset, row.get(x));
    //                }
    //            }
    //        }
    //    }
    //}

    private void beginAcceptingCharacterInput () {

        (new Thread () { public void run () {

            ClientAction action = new ClientAction();

            while (true) {

                int input = window.waitForUserInput();

                switch (input) {

                    // moving

                    case moveUp:
                        action.setAction(Action.MOVE);
                        action.setDirection(Direction.UP);
                        model.performAction(action);
                        break;
                    case moveLeft:
                        action.setAction(Action.MOVE);
                        action.setDirection(Direction.LEFT);
                        model.performAction(action);
                        break;
                    case moveDown:
                        action.setAction(Action.MOVE);
                        action.setDirection(Direction.DOWN);
                        model.performAction(action);
                        break;
                    case moveRight:
                        action.setAction(Action.MOVE);
                        action.setDirection(Direction.RIGHT);
                        model.performAction(action);
                        break;

                    // firing the gun

                    case gunUp:
                        action.setAction(Action.SHOOT);
                        action.setDirection(Direction.UP);
                        model.performAction(action);
                        break;
                    case gunLeft:
                        action.setAction(Action.SHOOT);
                        action.setDirection(Direction.LEFT);
                        model.performAction(action);
                        break;
                    case gunDown:
                        action.setAction(Action.SHOOT);
                        action.setDirection(Direction.DOWN);
                        model.performAction(action);
                        break;
                    case gunRight:
                        action.setAction(Action.SHOOT);
                        action.setDirection(Direction.RIGHT);
                        model.performAction(action);
                        break;

                    // miscellaneous

                    case BlackenKeys.RESIZE_EVENT:
                        window.handleResize();
                        break;

                }

            }

        }}).start();

    }

}
