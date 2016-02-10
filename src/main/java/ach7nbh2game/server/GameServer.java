package ach7nbh2game.server;

import ach7nbh2game.client.PlayerInfo;
import ach7nbh2game.main.Constants;
import ach7nbh2game.network.NetServer;
import ach7nbh2game.network.adapters.IClientToServer;
import ach7nbh2game.network.adapters.IServerToClient;
import ach7nbh2game.network.packets.ClientAction;
import ach7nbh2game.network.packets.GameState;
import ach7nbh2game.util.ClientID;
import ach7nbh2game.util.GameID;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class GameServer {

    private NetServer network;
    private ServerModel model;

    public GameServer () throws IOException {

        System.out.println("making new GameServer...");

        network = new NetServer(new IClientToServer () {

            public void createNewLobby(int clientID, String name) {
                model.createNewGameLobby(name);
            }

            public void requestLobbies(int clientID) {
                model.requestLobbies(new ClientID(clientID));
            }

            public void joinLobby(int clientID, int lobbyID, PlayerInfo info) {
                model.joinLobby(new ClientID(clientID), new GameID(lobbyID), info);
            }

            public void startGame(int clientID) {
                model.startGame(new ClientID(clientID));
            }

            public void move(int clientID, Constants.Directions direction) {
                model.respondToClientAction(new ClientID(clientID), new ClientAction(direction));
            }

            public boolean isConnected() {
                // this method is only needed client-side
                return false;
            }

            public void connectTo(String host, PlayerInfo info) throws IOException {
                // this method is only needed client-side
            }

        });

        model = new ServerModel(new IServerToClient() {

            public void enterGame(int clientID) {
                network.enterGame(clientID);
            }

            public void updateGameState(int clientID, GameState state) {
                network.updateGameState(clientID, state);
            }

            public void announceLobbies(int clientID, Map<Integer, String> lobbies,
                    Map<Integer, String> players, Map<Integer, Set<Integer>> lobbyToPlayers) {
                network.announceLobbies(clientID, lobbies, players, lobbyToPlayers);
            }

        });

    }

    public void start () throws IOException {

        System.out.println("starting the NetServer!");

        network.start();
        model.start();

    }

}
