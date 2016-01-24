package ach7nbh2game.network.adapters;

import ach7nbh2game.main.Constants.Directions;
import ach7nbh2game.server.GameServer;

public class ServerNTOG implements IClientToServer {

    private GameServer server;

    public ServerNTOG (GameServer serverIn) {
        server = serverIn;
    }

    public void createNewLobby (int clientID, String name) {
        server.createNewLobby(clientID, name);
    }

    public void requestLobbies (int clientID) {
        server.requestLobbies(clientID);
    }

    public void joinLobby (int clientID, int lobbyID) {
        server.joinLobby(clientID, lobbyID);
    }

    public void startGame (int lobbyID) {
        server.startGame(lobbyID);
    }

    public void move (int clientID, Directions direction) {
        server.move(clientID, direction);
    }

}
