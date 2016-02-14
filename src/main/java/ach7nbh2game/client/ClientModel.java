package ach7nbh2game.client;

import ach7nbh2game.client.adapters.IModelToView;
import ach7nbh2game.main.Constants.*;
import ach7nbh2game.network.packets.ClientAction;
import ach7nbh2game.network.packets.PlayerInfo;
import ach7nbh2game.util.Logger;
import ach7nbh2game.util.Utility;
import ach7nbh2game.network.adapters.IClientToServer;
import ach7nbh2game.network.packets.GameState;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClientModel {

    private IClientToServer server;

    private IModelToView view;

    private PlayerInfo playerInfo = new PlayerInfo();

    private boolean inGame = false; // TODO state design pattern

    public ClientModel (IClientToServer serverIn, IModelToView viewIn) {

        System.out.println("making new ClientModel...");

        server = serverIn;
        view = viewIn;

    }

    public void start () {

        System.out.println("starting the ClientModel!");

        String username = view.askForUsername();
        playerInfo.setUsername(username);
        playerInfo.setIcon(username.toUpperCase().toCharArray()[0]);

        while (!server.isConnected()) {
            try {
                server.connectTo(view.askForServerIP(), playerInfo);
            } catch (IOException e) {
                // no-op
            }
        }

        server.requestLobbies(playerInfo.getID());

    }

    public void updateState (GameState state) {

        Logger.Singleton.log(this, 0, "updateState:");
        Logger.Singleton.log(this, 1, "state = " + state);

        view.showMap(state.getFrame());
        view.showScores(state);

    }

    public void updateLobbyList (
            final Map<Integer, String> lobbies,
            final Map<Integer, String> players,
            final Map<Integer, Set<Integer>> lobbyToPlayers) {

        Logger.Singleton.log(this, 0, "updateLobbyList:");
        Logger.Singleton.log(this, 1, "lobbies = " + lobbies);
        Logger.Singleton.log(this, 1, "players = " + players);
        Logger.Singleton.log(this, 1, "lobbyToPlayers = " + lobbyToPlayers);

        if (!inGame) {

            // NORMAL BEHAVIOR

            String prompt = "";
            prompt += "Lobbies available for you to join:\n";

            final Set<Integer> myLobbies = new HashSet<Integer>();
            final Map<Integer, Integer> smallIntToLobbyID = new HashMap<Integer, Integer>();

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

            final String promptFinal = prompt;

            (new Thread() {
                public void run() {

                    String action = view.askForThing(promptFinal, "");

                    if (action.equals("")) {

                        Logger.Singleton.log(ClientModel.this, 0, "updateLobbyList: requesting lobbies...");

                    } else {

                        if (Utility.isInteger(action)) {

                            int smallInt = Integer.parseInt(action);
                            if (smallIntToLobbyID.containsKey(smallInt)) {
                                int lobbyID = smallIntToLobbyID.get(smallInt);

                                if (myLobbies.contains(lobbyID)) {

                                    Logger.Singleton.log(ClientModel.this, 0, "updateLobbyList: starting game " + lobbyID + "...");

                                    server.startGame(lobbyID);

                                    return; // do not request lobbies

                                } else {

                                    Logger.Singleton.log(ClientModel.this, 0, "updateLobbyList: joining lobby " + lobbyID + "...");

                                    server.joinLobby(playerInfo.getID(), lobbyID, playerInfo);

                                }

                            }

                        } else if (Utility.isAlphanumeric(action)) {

                            Logger.Singleton.log(ClientModel.this, 0, "updateLobbyList: creating lobby " + action + "...");

                            server.createNewLobby(playerInfo.getID(), action);

                        } else {

                            Logger.Singleton.log(ClientModel.this, 0, "updateLobbyList: invalid input. trying again...");

                            updateLobbyList(lobbies, players, lobbyToPlayers);

                        }

                    }

                    server.requestLobbies(playerInfo.getID());

                }
            }).start();

        }

    }

    public void enterGame () {

        Logger.Singleton.log(this, 0, "enterGame:");

        if (!inGame) {
            inGame = true;
        } else {
            // TODO what if in multiple lobbies and a second one starts?
        }

    }

    public void move(Direction direction) {

        Logger.Singleton.log(this, 0, "move:");
        Logger.Singleton.log(this, 1, "direction = " + direction);

        if (inGame) {
            server.move(playerInfo.getID(), direction);
        }

    }

    public void action(ClientAction actionIn) {
        if (inGame) {
            server.action(playerInfo.getID(), actionIn);
        }
    }

}
