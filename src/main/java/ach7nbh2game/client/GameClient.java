package ach7nbh2game.client;

import ach7nbh2game.client.adapters.IModelToView;
import ach7nbh2game.client.adapters.IViewToModel;
import ach7nbh2game.network.NetClient;
import ach7nbh2game.network.adapters.IClientToServer;
import ach7nbh2game.network.adapters.IServerToClient;
import ach7nbh2game.network.packets.ClientAction;
import ach7nbh2game.network.packets.GameState;
import ach7nbh2game.network.packets.PlayerInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class GameClient {

    private NetClient network;
    private ClientModel model;
    private ClientView view;

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

        view = new ClientView(new IViewToModel () {

            public void performAction(ClientAction actionIn) {
                model.action(actionIn);
            }

        });

        model = new ClientModel(new IClientToServer () {

            public void createNewLobby(int clientID, String name) {
                network.createLobby(name);
            }

            public void requestLobbies(int clientID) {
                network.reqLobbies();
            }

            public void joinLobby(int clientID, int lobbyID) {
                network.joinLobby(lobbyID);
            }

            public void startGame(int clientID) {
                network.startGame(clientID);
            }

            public void performAction(int clientID, ClientAction actionIn) {
                network.action(actionIn);
            }

            public boolean isConnected() {
                return network.isConnected();
            }

            public void connectTo(int clientID, String host, PlayerInfo info) throws IOException {
                network.connectTo(host, info);
            }

        }, new IModelToView () {

            public String askForUsername() {
                return view.askForUsername();
            }

            public String askForServerIP() {
                return view.askForServerIP();
            }

            public String askForThing(String label, String value) {
                return view.askForThing(label, value);
            }

			public void updateThing(String newLabel) {
				view.updateThing(newLabel);
			}

            public void showMap(ArrayList<ArrayList<Integer>> frame) {
                view.showMap(frame);
            }

            public void showScores(GameState state) {
                view.showScores(state);
            }

        });

    }

    public void start () {

        System.out.println("starting the GameClient!");

        network.start();
        view.start();
        model.start();

    }

}
