package ach7nbh2game.client;

import ach7nbh2game.main.Constants;
import ach7nbh2game.network.NetClient;
import ach7nbh2game.network.adapters.ClientGTON;
import ach7nbh2game.network.adapters.ClientNTOG;
import ach7nbh2game.network.adapters.IClientToServer;
import ach7nbh2game.network.adapters.IServerToClient;
import com.googlecode.blacken.colors.ColorNames;
import com.googlecode.blacken.colors.ColorPalette;
import com.googlecode.blacken.swing.SwingTerminal;
import com.googlecode.blacken.terminal.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Map;
import java.util.Random;

public class GameClient {

    private String name;
    private int clientID;

    private IClientToServer server;

    private CursesLikeAPI terminal;

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

        if (localGame) {

            server = serverIn;

        } else {

            NetClient netClient = new NetClient();
            IServerToClient adapterNTOG = new ClientNTOG(this);
            IClientToServer adapterGTON = new ClientGTON(netClient);
            netClient.installAdapter(adapterNTOG);
            server = adapterGTON;

        }

    }

    public void runTest () {

        // FOR TESTING ONLY
        if (name.equals("Client A")) {
            System.out.println("STEP 1");
            server.createNewLobby(clientID, "Test Lobby");
        } else if (name.equals("Client B")) {
            System.out.println("STEP 2");
            server.requestLobbies(clientID);
        }

    }

    public int getClientID () {

        return clientID;

    }

    public void updateLobbyList (Map<Integer, String> lobbies) {

        System.out.println("in " + name + ", updateLobbyList()");
        System.out.println("  lobbies = " + lobbies);

        // FOR TESTING ONLY
        if (name.equals("Client B")) {
            System.out.println("STEP 3");
            int lobbyID = lobbies.keySet().toArray(new Integer[lobbies.size()])[0];
            server.joinLobby(clientID, lobbyID);
            server.startGame(lobbyID);
        }

    }

    public void enterGame () {

        System.out.println("in " + name + ", enterGame()");

        TerminalInterface newTerminal = new SwingTerminal();
        newTerminal.init("Andrew Nick Game",
                Constants.clientHeight + 1, Constants.clientWidth + 1);
        terminal = new CursesLikeAPI(newTerminal);

        ColorPalette palette = new ColorPalette();
        palette.addAll(ColorNames.XTERM_256_COLORS, false);
        palette.putMapping(ColorNames.SVG_COLORS);
        terminal.setPalette(palette);

        terminal.move(-1, -1);

        (new Thread() {
            public void run() {

                while (true) {

                    int input = terminal.getch(); // BLOCKING

                    System.out.println("in " + name + " loop");
                    System.out.println("  input = " + input);

                    switch (input) {
                        case keyUp:
                            server.move(clientID, Constants.Directions.UP);
                            break;
                        case keyDown:
                            server.move(clientID, Constants.Directions.DOWN);
                            break;
                        case keyLeft:
                            server.move(clientID, Constants.Directions.LEFT);
                            break;
                        case keyRight:
                            server.move(clientID, Constants.Directions.RIGHT);
                            break;
                    }

                }

            }
        }).start();

    }

    public void updateState (ArrayList<ArrayList<Integer>> mapView) {

        //System.out.println("in " + name + ", updateState()");
        //System.out.println("  height = " + Constants.clientHeight);
        //System.out.println("  width = " + Constants.clientWidth);
        //System.out.println("  mapView.size() = " + mapView.size());
        //System.out.println("  mapView.get(0).size() = " + mapView.get(0).size());

        showMap(mapView);

        terminal.refresh();

    }

    private void showMap (ArrayList<ArrayList<Integer>> mapView) {

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
