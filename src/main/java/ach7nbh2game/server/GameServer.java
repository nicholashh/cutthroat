package ach7nbh2game.server;

import ach7nbh2game.network.NetServer;
import ach7nbh2game.network.adapters.IClientToServer;
import ach7nbh2game.network.adapters.IServerToClient;
import ach7nbh2game.network.packets.ClientAction;
import ach7nbh2game.network.packets.GameState;
import ach7nbh2game.network.packets.PlayerInfo;
import ach7nbh2game.util.id.ClientID;
import ach7nbh2game.util.id.GameID;
import ach7nbh2game.util.id.Pair;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.Map;
import java.util.Set;

public class GameServer {

    private NetServer network;
    private ServerModel model;

    public GameServer () throws IOException {

        System.out.println("making new GameServer...");
        System.out.println("running on " + Inet4Address.getLocalHost().getHostAddress());

        network = new NetServer(new IClientToServer () {

            public void createNewLobby(int clientID, String name) {
                model.createNewGameLobby(new ClientID(clientID), name);
            }

            public void requestLobbies(int clientID) {
                model.requestLobbies(new ClientID(clientID));
            }

            public void joinLobby(int clientID, int lobbyID) {
                model.joinLobby(new ClientID(clientID), new GameID(lobbyID));
            }

            public void startGame(int clientID) {
                model.startGame(new ClientID(clientID));
            }

            public void performAction(int clientID, ClientAction actionIn) {
                model.respondToClientAction(new ClientID(clientID), actionIn);
            }

            public void connectTo (int clientID, String host, PlayerInfo info) throws IOException {
                model.registerNewClient(new ClientID(clientID), info);
            }

            public boolean isConnected () {
                // this method is only needed client-side
                return true;
            }

            public void playerReady(int clientID, boolean value) {
                model.playerReady(new ClientID(clientID), value);
            }

        });

        model = new ServerModel(new IServerToClient() {

            public void enterGame(int clientID) {
                network.enterGame(clientID);
            }

            public void endGame (int clientID, PlayerInfo client) {
                network.endGame(clientID, client);
            }

            public void updateGameState(int clientID, GameState state) {
                network.updateGameState(clientID, state);
            }

            public void announceLobbies(int clientID, Map<Integer, String> lobbies,
                    Map<Integer, Pair<String, Boolean>> players, Map<Integer, Set<Integer>> lobbyToPlayers) {
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
