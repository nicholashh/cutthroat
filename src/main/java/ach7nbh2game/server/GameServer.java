package ach7nbh2game.server;

import ach7nbh2game.client.PlayerInfo;
import ach7nbh2game.main.Constants;
import ach7nbh2game.network.NetServer;
import ach7nbh2game.network.adapters.IClientToServer;
import ach7nbh2game.network.adapters.IServerToClient;

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
                model.createNewLobby(clientID, name);
            }

            public void requestLobbies(int clientID) {
                model.requestLobbies(clientID);
            }

            public void joinLobby(int clientID, int lobbyID, PlayerInfo info) {
                model.joinLobby(clientID, lobbyID, info);
            }

            public void startGame(int lobbyID) {
                model.startGame(lobbyID);
            }

            public void move(int clientID, Constants.Directions direction) {
                model.move(clientID, direction);
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
