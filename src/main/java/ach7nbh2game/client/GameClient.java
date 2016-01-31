package ach7nbh2game.client;

import ach7nbh2game.main.Constants;
import ach7nbh2game.network.NetClient;
import ach7nbh2game.network.adapters.ClientGTON;
import ach7nbh2game.network.adapters.ClientNTOG;
import ach7nbh2game.network.adapters.IClientToServer;
import ach7nbh2game.network.adapters.IServerToClient;
import ach7nbh2game.server.GameState;
import com.googlecode.blacken.colors.ColorNames;
import com.googlecode.blacken.colors.ColorPalette;
import com.googlecode.blacken.swing.SwingTerminal;
import com.googlecode.blacken.terminal.*;

import javax.swing.*;
import java.io.IOException;
import java.util.*;

public class GameClient {

    private String name;
    private int clientID;
    private PlayerInfo playerInfo;

    private IClientToServer server;

    private CursesLikeAPI terminal;

    private boolean inGame = false; // TODO state design pattern

    private final int keyUp = BlackenKeys.KEY_UP;
    private final int keyDown = BlackenKeys.KEY_DOWN;
    private final int keyLeft = BlackenKeys.KEY_LEFT;
    private final int keyRight = BlackenKeys.KEY_RIGHT;

    public GameClient (String nameIn, boolean localGame, IClientToServer serverIn) throws IOException {

        System.out.println("GameClient");
        System.out.println("  nameIn = " + nameIn);
        System.out.println("  localGame = " + localGame);

        name = nameIn;

        Random rand = new Random();
        clientID = rand.nextInt();

        playerInfo = new PlayerInfo();
        String username = askForUsername();
        playerInfo.setUsername(username);
        playerInfo.setIcon(username.toUpperCase().toCharArray()[0]);

        if (localGame) {

            server = serverIn;

        } else {

            NetClient netClient = null;

            while (netClient == null) {
                try { netClient = new NetClient(askForServerIP(), playerInfo); }
                catch (Exception e) { System.err.println(e.toString()); }
            }

            IServerToClient adapterNTOG = new ClientNTOG(this);
            IClientToServer adapterGTON = new ClientGTON(netClient);
            netClient.installAdapter(adapterNTOG);
            server = adapterGTON;

        }

        setUpTerminal();
        showWelcomeMessage();
        beingAcceptingCharacterInput();

        server.requestLobbies(clientID);

    }

    private String askForServerIP () {

        String prompt = "";
        prompt += "Connect to a game server!\n";
        prompt += "What is the server's IP address?";

        return askForThing(prompt , "localhost");

    }

    private boolean isAlphanumeric (String string) {

        boolean isAlphanumeric = true;

        for (char c : string.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                isAlphanumeric = false;
            }
        }

        return isAlphanumeric;

    }

    private boolean isInteger (String string) {

        try { Integer.parseInt(string); return true; }
        catch (NumberFormatException nfe) { return false; }

    }

    private String askForUsername () {

        String prompt = "";
        prompt += "Pick a username!\n";
        prompt += "(Must be alphanumeric.)\n";
        prompt += "(Must be 1-10 characters long.)";

        String name = askForThing(prompt, "");

        int length = name.length();
        if (!isAlphanumeric(name) || length < 1 || length > 10) {
            System.err.println("invalid username chosen; trying again...");
            return askForUsername();
        } else {
            return name;
        }

    }

    private String askForThing (String label, String value) {

        String input = (String) JOptionPane.showInputDialog(
                null, label, null, JOptionPane.QUESTION_MESSAGE, null, null, value);
        if (input == null || input.trim().length() == 0) return "";
        else return input.trim();

    }

    public void runTest () {

        // FOR TESTING ONLY
        if (name.equals("Client A")) {
            System.out.println("STEP A1");
            server.createNewLobby(clientID, "Test Lobby");
            try {Thread.sleep(250);} catch (InterruptedException e) {e.printStackTrace();}
            server.requestLobbies(clientID);
        } else if (name.equals("Client B") || name.equals("Client C")) {
            System.out.println("STEP B1");
            server.requestLobbies(clientID);
        }

    }

    public int getClientID () {
        return clientID;
    }

    // FOR TESTING ONLY
    public void setClientID (int newClientID) {
        clientID = newClientID;
    }

    public void updateLobbyList (
            Map<Integer, String> lobbies,
            Map<Integer, String> players,
            Map<Integer, Set<Integer>> lobbyToPlayers) {

        System.out.println("in " + name + ", updateLobbyList()");
        System.out.println("  lobbies = " + lobbies);
        System.out.println("  players = " + players);
        System.out.println("  lobbyToPlayers = " + lobbyToPlayers);

        // FOR TESTING ONLY
        if (name.equals("Client A") || name.equals("Client C")) {
            System.out.println("STEP A2");
            int lobbyID = lobbies.keySet().toArray(new Integer[lobbies.size()])[0];
            server.joinLobby(clientID, lobbyID, playerInfo);
        } else if (name.equals("Client B")) {
            System.out.println("STEP B2");
            int lobbyID = lobbies.keySet().toArray(new Integer[lobbies.size()])[0];
            server.joinLobby(clientID, lobbyID, playerInfo);
            try {Thread.sleep(250);} catch (InterruptedException e) {e.printStackTrace();}
            server.startGame(lobbyID);
        } else {

            // NORMAL BEHAVIOR

            String prompt = "";
            prompt += "Lobbies available for you to join:\n";

            Set<Integer> myLobbies = new HashSet<Integer>();
            Map<Integer, Integer> smallIntToLobbyID = new HashMap<Integer, Integer>();

            if (lobbies.isEmpty()) {

                prompt += "    There are no lobbies for you to join.\n";

            } else {

                int i = 0;
                for (int lobbyID : lobbies.keySet()) {

                    smallIntToLobbyID.put(i, lobbyID);
                    prompt += "    " + i + ": " + lobbies.get(lobbyID) + "\n";
                    i++;

                    for (int playerID : lobbyToPlayers.get(lobbyID)) {
                        if (playerID == playerInfo.getID()) {
                            myLobbies.add(lobbyID);
                            prompt += "        me! (" + playerInfo.getUsername() + ")\n";
                        } else {
                            String username = players.get(playerID);
                            prompt += "        player: " + username + "\n";
                        }
                    }

                }

            }

            prompt += "To update this list of lobbies, click \"cancel.\"\n";
            prompt += "To create a new lobby, enter it's alphanumeric name.\n";
            prompt += "To join an existing lobby, enter it's numeric ID.\n";
            prompt += "To start your game, re-enter your lobby's numeric ID.";

            String action = askForThing(prompt, "");

            if (action.equals("")) {

                System.out.println("  requesting lobbies...");

            } else {

                if (isInteger(action)) {

                    int smallInt = Integer.parseInt(action);
                    if (smallIntToLobbyID.containsKey(smallInt)) {
                        int lobbyID = smallIntToLobbyID.get(smallInt);

                        if (myLobbies.contains(lobbyID)) {

                            System.out.println("  starting game " + lobbyID + "...");
                            server.startGame(lobbyID);

                            return; // do not request lobbies

                        } else {

                            System.out.println("  joining lobby " + lobbyID + "...");
                            server.joinLobby(clientID, lobbyID, playerInfo);

                        }

                    }

                } else if (isAlphanumeric(action)) {

                    System.out.println("  creating lobby " + action + "...");
                    server.createNewLobby(clientID, action);

                } else {

                    System.out.println("  invalid input. trying again...");
                    updateLobbyList(lobbies, players, lobbyToPlayers);

                }

            }

            server.requestLobbies(clientID);

        }

    }

    private void setUpTerminal () {

        System.out.println("in " + name + ", setUpTerminal()");

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

    public void enterGame () {

        System.out.println("in " + name + ", enterGame()");

        if (!inGame) {
            inGame = true;
        } else {
            // TODO what if in multiple lobbies and a second one starts?
        }

    }

    private void beingAcceptingCharacterInput () {

        System.out.println("in " + name + ", beingAcceptingCharacterInput()");

        (new Thread() { public void run () {

            while (true) {

                int input = terminal.getch(); // BLOCKING

                if (inGame) switch (input) {
                    case keyUp:    server.move(clientID, Constants.Directions.UP);    break;
                    case keyDown:  server.move(clientID, Constants.Directions.DOWN);  break;
                    case keyLeft:  server.move(clientID, Constants.Directions.LEFT);  break;
                    case keyRight: server.move(clientID, Constants.Directions.RIGHT); break;
                }

            }

        }}).start();

    }

    public void updateState (GameState state) {

        showMap(state.getFrame());
        showScores(state);

        terminal.refresh();

    }

    private void showMap (ArrayList<ArrayList<Integer>> map) {

        showSomething(map,
                0, Constants.clientMapHeight, 0,
                0, Constants.clientMapWidth, 0);

        //for (int y = 0; y < Constants.clientMapHeight; y++) {
        //    if (y < mapView.size() && y < terminal.getHeight()) {
        //        ArrayList<Integer> row = mapView.get(y);
        //        for (int x = 0; x < Constants.clientMapWidth; x++) {
        //            if (x < row.size() && x < terminal.getWidth()) {
        //
        //                setTerminal(x, y, row.get(x));
        //
        //            }
        //        }
        //    }
        //}

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

    private void showScores (GameState gameState) {

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

        //for (int y = 0; y < Constants.clientMapHeight; y++) {
        //    if (y < message.size() && y < terminal.getHeight()) {
        //        ArrayList<Integer> row = message.get(y);
        //        for (int x = 0; x < Constants.clientSidebarWidth; x++) {
        //            if (x < row.size() && x < terminal.getWidth()) {
        //
        //                setTerminal(x + Constants.clientMapWidth, y, row.get(x));
        //
        //            }
        //        }
        //    }
        //}

    }

    private void showSomething (ArrayList<ArrayList<Integer>> thing,
            int yLow, int yHigh, int yOffset, int xLow, int xHigh, int xOffset) {

        for (int y = yLow; y < yHigh; y++) {
            if (y < thing.size() && y < terminal.getHeight()) {
                ArrayList<Integer> row = thing.get(y);
                for (int x = xLow; x < xHigh; x++) {
                    if (x < row.size() && x < terminal.getWidth()) {

                        setTerminal(x + xOffset, y + yOffset, row.get(x));

                    }
                }
            }
        }

    }

    private void setTerminal (int x, int y, int character) {

        terminal.set(y, x, new String(Character.toChars(character)), 7, 0,
                EnumSet.noneOf(TerminalStyle.class), EnumSet.noneOf(CellWalls.class));

    }

}
