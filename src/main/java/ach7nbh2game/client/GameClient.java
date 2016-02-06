package ach7nbh2game.client;

import ach7nbh2game.client.adapters.IModelToView;
import ach7nbh2game.client.adapters.IViewToModel;
import ach7nbh2game.main.Constants;
import ach7nbh2game.network.NetClient;
import ach7nbh2game.network.adapters.IClientToServer;
import ach7nbh2game.network.adapters.IServerToClient;
import ach7nbh2game.server.GameState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class GameClient {

    NetClient network;
    ClientModel model;
    ClientView view;

    public GameClient () {

        System.out.println("making new GameClient...");

        network = new NetClient(new IServerToClient () {

            public void enterGame(int clientID) {
                model.enterGame();
            }

            public void updateGameState(int clientID, GameState state) {
                model.updateState(state);
            }

            public void announceLobbies(int clientID, Map<Integer, String> lobbies,
                    Map<Integer, String> players, Map<Integer, Set<Integer>> lobbyToPlayers) {
                model.updateLobbyList(lobbies, players, lobbyToPlayers);
            }

        });

        model = new ClientModel(new IClientToServer () {

            public void createNewLobby(int clientID, String name) {
                network.createLobby(name);
            }

            public void requestLobbies(int clientID) {
                network.reqLobbies();
            }

            public void joinLobby(int clientID, int lobbyID, PlayerInfo info) {
                network.joinLobby(lobbyID, info);
            }

            public void startGame(int lobbyID) {
                network.startGame(lobbyID);
            }

            public void move(int clientID, Constants.Directions direction) {
                network.move(direction);
            }

            public boolean isConnected() {
                return network.isConnected();
            }

            public void connectTo(String host, PlayerInfo info) throws IOException {
                network.connectTo(host, info);
            }

        }, new IModelToView () {

            public String askForUsername() {
                return PopupController.askForUsername();
            }

            public String askForServerIP() {
                return PopupController.askForServerIP();
            }

            public String askForThing(String label, String value) {
                return PopupController.askForThing(label, value);
            }

            public void showMap(ArrayList<ArrayList<Integer>> frame) {
                view.showMap(frame);
            }

            public void showScores(GameState state) {
                view.showScores(state);
            }

        });

        view = new ClientView(new IViewToModel () {

            public void move(Constants.Directions direction) {
                model.move(direction);
            }

        });

    }

    public void start () {

        System.out.println("starting the GameClient!");

        network.start();
        model.start();
        view.start();

    }

}
