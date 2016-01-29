package ach7nbh2game.network.adapters;

import ach7nbh2game.client.PlayerInfo;
import ach7nbh2game.main.Constants.*;
import ach7nbh2game.network.NetClient;

public class ClientGTON implements IClientToServer {

    NetClient nclient;

    public ClientGTON(NetClient netclient) {
        nclient = netclient;
    }

    public void createNewLobby (int clientID, String name) {
        nclient.createLobby(name);
    }

    public void requestLobbies (int clientID) {
        nclient.reqLobbies();
    }

    public void joinLobby (int clientID, int lobbyID, PlayerInfo info) {
        nclient.joinLobby(lobbyID, info);
    }

    public void startGame (int lobbyID) {
        nclient.startGame(lobbyID);
    }

    public void move (int clientID, Directions direction) {
        nclient.move(direction);
    }

}
