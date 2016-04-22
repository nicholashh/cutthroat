package ach7nbh2game.client;

import ach7nbh2game.client.adapters.IModelToView;
import ach7nbh2game.main.Constants;
import ach7nbh2game.network.adapters.IClientToServer;
import ach7nbh2game.network.packets.ClientAction;
import ach7nbh2game.network.packets.GameState;
import ach7nbh2game.network.packets.PlayerInfo;
import ach7nbh2game.util.Logger;
import ach7nbh2game.util.Utility;
import ach7nbh2game.util.id.Pair;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClientModel {

    private IClientToServer server;

    private IModelToView view;

    private PlayerInfo playerInfo = new PlayerInfo();

    private boolean inGame = false; // TODO state design pattern

    private Set<Thread> infoRequestThreads = new HashSet<>();

    private Map<Integer, String> mlobbies = new HashMap<>();
    private Map<Integer, Pair<String, Boolean>> mplayers = new HashMap<>();
    private Map<Integer, Set<Integer>> mlobbyToPlayers = new HashMap<>();
    private Object[] pInL = new Object[]{};

    private int numLobbies = 0;
    private int numPlayers = 0;

    public ClientModel (IClientToServer serverIn, IModelToView viewIn) {

        System.out.println("making new ClientModel...");

        server = serverIn;
        view = viewIn;

    }

    public void start () {

        System.out.println("starting the ClientModel!");

        String username = "";
        while (username.trim() == "")
        	username = view.askForUsername();
        playerInfo.setUsername(username);
        playerInfo.setIcon(username.toUpperCase().toCharArray()[0]);

        boolean failedOnce = false;
        while (!server.isConnected()) {
            try {
                if (!failedOnce) {
                    server.connectTo(0, view.askForServerIP(), playerInfo);
                } else {
                    server.connectTo(0, view.askForServerIPFailed(), playerInfo);
                }
            } catch (IOException e) {
                System.out.println("failed to connect to server");
                e.printStackTrace();
                failedOnce = true;
            }
        }

        server.requestLobbies(playerInfo.getID());

    }

    public void updateState (GameState state) {

        //Logger.Singleton.log(this, 0, "updateState:");
        //Logger.Singleton.log(this, 1, "state = " + state);

        view.updateState(state);

    }
    
    private boolean waitingForInput = false;
    private Integer myLobby = null;
//    public enum Column {LOBBIES, PLAYERS}
//    private Column focus = LOBBIES;
    private int selected = 0;
//    private int pselected = 0;

    public void selectUp() {
        Logger.Singleton.log(this, 0, "moved selection up");
        if (selected > 0) {
            if (mplayers.get(playerInfo.getID()).second) {
                server.playerReady(playerInfo.getID(), false);
            }
            selected--;
            myLobby = (int)mlobbies.keySet().toArray()[selected];
            server.joinLobby(playerInfo.getID(), (int)mlobbies.keySet().toArray()[selected]);
            updateLobbyMenu();
        }
//        switch(focus) {
//            case LOBBIES:
//                if (selected > 0) {
//                    selected--;
//                    updateLobbyMenu();
//                }
//                break;
//            case PLAYERS:
//                if (pselected > 0) {
//                    pselected--;
//                    updateLobbyMenu();
//                }
//                break;
//        }
    }

    public void selectDown() {
        Logger.Singleton.log(this, 0, "moved selection down");
        if (selected < numLobbies-1) {
            if (mplayers.get(playerInfo.getID()).second) {
                server.playerReady(playerInfo.getID(), false);
            }
            selected++;
            myLobby = (int)mlobbies.keySet().toArray()[selected];
            server.joinLobby(playerInfo.getID(), (int)mlobbies.keySet().toArray()[selected]);
            updateLobbyMenu();
        }
//        switch(focus) {
//            case LOBBIES:
//                if (selected < numLobbies-1) {
//                    selected++;
//                    updateLobbyMenu();
//                }
//                break;
//            case PLAYERS:
//                if (pselected < numPlayers-1) {
//                    pselected++;
//                    updateLobbyMenu();
//                }
//                break;
//        }
    }

//    public void selectLeft() {
//        focus = LOBBIES;
//        updateLobbyMenu();
//    }
//
//    public void selectRight() {
//        focus = PLAYERS;
//        updateLobbyMenu();
//    }

    final String PIPE = "\u2502";

    private void updateLobbyMenu() {
        Object[] lobbyIDs = mlobbies.keySet().toArray();
        numLobbies = lobbyIDs.length;

        String instructions;
        if (mplayers.get(playerInfo.getID()).second) {
            instructions = "\u2191/\u2193: Select  Enter: UNREADY";
            view.showPrompt(instructions, Window.Component.BottomPanel, ClientView.VerticalAlignment.CENTER,
                    ClientView.HorizontalAlignment.LEFT);
        } else {
            instructions = "\u2191/\u2193: Select  Enter: READY";
            view.showPrompt(instructions, Window.Component.BottomPanel, ClientView.VerticalAlignment.CENTER,
                    ClientView.HorizontalAlignment.LEFT);
        }

        String prompt = "";
        prompt += "Lobbies";
        for (int i = 0; i < (Constants.clientMapWidth/2)-"lobbies".length()-1; i++) {
            prompt += " ";
        }
        prompt += PIPE;
        prompt += "Players\n";

        //final Set<Integer> myLobbies = new HashSet<Integer>();
        final Map<Integer, Integer> smallIntToLobbyID = new HashMap<Integer, Integer>();

        if (mlobbies.isEmpty()) {

            for (int i = 0; i < Constants.clientMapHeight-2; i++) {
                for (int j = 0; j < Constants.clientMapWidth/2-1; j++) {
                    prompt += " ";
                }
                prompt += PIPE + "\n";
            }

        } else {
            if (myLobby == null) {
                myLobby = (int)mlobbies.keySet().toArray()[selected];
                server.joinLobby(playerInfo.getID(), (int)mlobbies.keySet().toArray()[selected]);
            }

            for (int i = 0; i < Constants.clientMapHeight - 2; i++) {
                if (selected == i) { // if (focus == LOBBIES && selected == i) {
                        prompt += "*";
                } else {
                    prompt += " ";
                }
                if (i < lobbyIDs.length) {
                    char[] lname = mlobbies.get(lobbyIDs[i]).toCharArray();
                    for (int c = 0; c < Constants.clientMapWidth / 2 - 2; c++) {
                        if (c < lname.length) {
                            prompt += lname[c];
                        } else {
                            prompt += " ";
                        }
                    }
                } else {
                    for (int c = 0; c < Constants.clientMapWidth / 2 - 2; c++) {
                        prompt += " ";
                    }
                }
                prompt += PIPE;
                pInL = mlobbyToPlayers.get(lobbyIDs[selected]).toArray();
                numPlayers = pInL.length;
                if (pInL.length > 0 && i < pInL.length) {
//                    if (focus == PLAYERS && pselected == i) {
//                        prompt += "*";
//                    } else {
//                        prompt += " ";
//                    }
                    prompt += " ";
                    for (int p = 0; p < Constants.clientMapWidth / 2-9; p++) {
                        if (p < mplayers.get(pInL[i]).first.length()) {
                            prompt += mplayers.get(pInL[i]).first.toCharArray()[p];
                        } else {
                            prompt += " ";
                        }
                    }
                    if (mplayers.get(pInL[i]).second) {
                        prompt += "    READY";
                    } else {
                        prompt += "NOT READY";
                    }
                }
                prompt += "\n";
            }
        }
        prompt += "CREATE LOBBY: ";

        view.updateThing(prompt, ClientView.VerticalAlignment.CENTER, ClientView.HorizontalAlignment.LEFT);
    }

    public void updateLobbyList (
            final Map<Integer, String> lobbies,
            final Map<Integer, Pair<String, Boolean>> players,
            final Map<Integer, Set<Integer>> lobbyToPlayers) {

        Logger.Singleton.log(this, 0, "updateLobbyList:");
        Logger.Singleton.log(this, 1, "lobbies = " + lobbies);
        Logger.Singleton.log(this, 1, "players = " + players);
        Logger.Singleton.log(this, 1, "lobbyToPlayers = " + lobbyToPlayers);

        if (!inGame) {
            mlobbies = lobbies;
            mplayers = players;
            mlobbyToPlayers = lobbyToPlayers;

            String instructions;
            if (players.get(playerInfo.getID()).second) {
                instructions = "\u2191/\u2193: Select  Enter: UNREADY";
                view.showPrompt(instructions, Window.Component.BottomPanel, ClientView.VerticalAlignment.CENTER,
                        ClientView.HorizontalAlignment.LEFT);
            } else {
                instructions = "\u2191/\u2193: Select  Enter: READY";
                view.showPrompt(instructions, Window.Component.BottomPanel, ClientView.VerticalAlignment.CENTER,
                        ClientView.HorizontalAlignment.LEFT);
            }

            // NORMAL BEHAVIOR
            Object[] lobbyIDs = lobbies.keySet().toArray();
            numLobbies = lobbyIDs.length;

            String prompt = "";
            prompt += "Lobbies";
            for (int i = 0; i < (Constants.clientMapWidth/2)-"lobbies".length()-1; i++) {
                prompt += " ";
            }
            prompt += PIPE;
            prompt += "Players\n";

            //final Set<Integer> myLobbies = new HashSet<Integer>();
            final Map<Integer, Integer> smallIntToLobbyID = new HashMap<Integer, Integer>();

            if (lobbies.isEmpty()) {

                for (int i = 0; i < Constants.clientMapHeight-2; i++) {
                    for (int j = 0; j < Constants.clientMapWidth/2-1; j++) {
                        prompt += " ";
                    }
                    prompt += PIPE + "\n";
                }

            } else {
                if (myLobby == null) {
                    myLobby = (int)mlobbies.keySet().toArray()[selected];
                    server.joinLobby(playerInfo.getID(), (int)mlobbies.keySet().toArray()[selected]);
                }

                for (int i = 0; i < Constants.clientMapHeight-2; i++) {
                    if (selected == i) { // if (focus == LOBBIES && selected == i) {
                        prompt += "*";
                    } else {
                        prompt += " ";
                    }
                    if (i < lobbyIDs.length) {
                        char[] lname = lobbies.get(lobbyIDs[i]).toCharArray();
                        for (int c = 0; c < Constants.clientMapWidth / 2 - 2; c++) {
                            if (c < lname.length) {
                                prompt += lname[c];
                            } else {
                                prompt += " ";
                            }
                        }
                    } else {
                        for (int c = 0; c < Constants.clientMapWidth / 2 - 2; c++) {
                            prompt += " ";
                        }
                    }
                    prompt += PIPE;
                    pInL = lobbyToPlayers.get(lobbyIDs[selected]).toArray();
                    numPlayers = pInL.length;
                    if (pInL.length > 0 && i < pInL.length) {
//                        if (focus == PLAYERS && pselected == i) {
//                            prompt += "*";
//                        } else {
//                            prompt += " ";
//                        }
                        prompt += " ";
                        for (int p = 0; p < Constants.clientMapWidth/2-9; p++) {
                            if (p < players.get(pInL[i]).first.length()) {
                                prompt += players.get(pInL[i]).first.toCharArray()[p];
                            } else {
                                prompt += " ";
                            }
                        }
                        if (players.get(pInL[i]).second) {
                            prompt += "    READY";
                        } else {
                            prompt += "NOT READY";
                        }
                    }
                    prompt += "\n";
                }

//                int i = 0;
//                for (int lobbyID : lobbies.keySet()) {
//
//                    smallIntToLobbyID.put(i, lobbyID);
//                    prompt += i + ": " + lobbies.get(lobbyID) + "\n";
//                    i++;
//
//                    for (int playerID : lobbyToPlayers.get(lobbyID)) {
//                        if (playerID == playerInfo.getID()) {
//                            myLobby = lobbyID;
//                            prompt += "me! (" + playerInfo.getUsername() + ")\n";
//                        } else {
//                            String username = players.get(playerID);
//                            prompt += "player: " + username + "\n";
//                        }
//                    }
//
//                }

            }

            prompt += "CREATE LOBBY: ";

            final String promptFinal = prompt;

            if (waitingForInput) {
            	
            	view.updateThing(promptFinal, ClientView.VerticalAlignment.CENTER, ClientView.HorizontalAlignment.LEFT);
            	
            } else {

                Thread newThread = new Thread() {
                    public void run() {
                        try {

                            waitingForInput = true;

                            String action = view.askForThing(promptFinal, "", ClientView.VerticalAlignment.CENTER,
                                    ClientView.HorizontalAlignment.LEFT);

                            if (action.equals("")) {

//                            Logger.Singleton.log(ClientModel.this, 0, "updateLobbyList: requesting lobbies...");
//
//                            server.requestLobbies(playerInfo.getID());

//                            switch(focus) {
//                                case LOBBIES:
//                                    if (!lobbies.isEmpty()) {
//                                        if (myLobby == (int)lobbyIDs[selected]) {
//                                            //server.startGame(myLobby);
//                                        } else {
//                                            server.joinLobby(playerInfo.getID(), (int)lobbyIDs[selected]);
//                                            myLobby = (int)lobbyIDs[selected];
//                                        }
//                                    } else {
//                                        server.requestLobbies(playerInfo.getID());
//                                    }
//                                    break;
//                                case PLAYERS:
//                                    if (pInL.length != 0) {
//                                        if (players.get(pInL[pselected]).first.contentEquals(
//                                                playerInfo.getUsername())) {
//                                            if (!players.get(pInL[pselected]).second) {
//                                                server.playerReady(playerInfo.getID(), true);
//                                            } else {
//                                                server.playerReady(playerInfo.getID(), false);
//                                            }
//                                        }
//                                    }
//                            }
                                if (!players.get(playerInfo.getID()).second) {
                                    server.playerReady(playerInfo.getID(), true);
                                } else {
                                    server.playerReady(playerInfo.getID(), false);
                                }

                            } else {

                                if (Utility.isAlphanumeric(action)) {

                                    Logger.Singleton.log(ClientModel.this, 0, "updateLobbyList: creating lobby " + action + "...");

                                    server.createNewLobby(playerInfo.getID(), action);
                                }

                            }
                            Logger.Singleton.log(ClientModel.this, 0, "updateLobbyList: invalid input. trying again...");

                            updateLobbyList(lobbies, players, lobbyToPlayers);

                            server.requestLobbies(playerInfo.getID());

                            waitingForInput = false;
                        } catch (Exception e) {
                            Thread.currentThread().interrupt();
                        }

                    }
                };

                infoRequestThreads.add(newThread);
                newThread.start();

            }

//                }
//            };
//
//            infoRequestThreads.add(newThread);
//            newThread.start();

        }

    }

    public void enterGame () {

        Logger.Singleton.log(this, 0, "enterGame:");

        if (!inGame) {

            for (Thread thread : infoRequestThreads) {
                thread.interrupt();
            }

            inGame = true;
            waitingForInput = false;

        } else {
            // TODO what if in multiple lobbies and a second one starts?
        }

    }

    public void endGame(PlayerInfo client) {
        inGame = false;
        waitingForInput = false;
        view.endGame(client);
        try {Thread.sleep(5000);} catch (InterruptedException e) {}
        view.clearMenus();
        myLobby = null;
        server.requestLobbies(playerInfo.getID());
        selectDown();
        selectUp();
        SoundEffect.MENU_BGM.loop();
    }

    //public void move(Direction direction) {
    //
    //    Logger.Singleton.log(this, 0, "move:");
    //    Logger.Singleton.log(this, 1, "direction = " + direction);
    //
    //    if (inGame) {
    //        server.move(playerInfo.getID(), direction);
    //    }
    //
    //}

    public void action(ClientAction actionIn) {
        if (inGame) {
            server.performAction(playerInfo.getID(), actionIn);
        }
    }

}
