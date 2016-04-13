package ach7nbh2game.network.adapters;

import ach7nbh2game.network.packets.ClientAction;
import ach7nbh2game.network.packets.PlayerInfo;

import java.io.IOException;

public interface IClientToServer {

    /**
     * a client trying to create a new game lobby
     * how do you want to identify the lobby? a unique int? your choice
     */
     void createNewLobby (int clientID, String name);

    /**
     * get all lobbies available
     * again, how to represent?
     */
     void requestLobbies (int clientID);

    /**
     * request to join a lobby
     * bool says if you were allowed to join or not?
     * i think clientID could be abstracted to IP
     * but my server needs a way to identify each player
     * so you can come up with a thing you can pass to me
     * an int? similarly, you can design this
     * also, do we want to have private lobbies?
     * we should make this easily extensible to that later
     */
     void joinLobby (int clientID, int lobbyID);

    /**
     * turn a lobby into a game
     * again, how to identify lobby? your choice
     */
    void startGame (int clientID);

    void performAction (int clientID, ClientAction actionIn);

    boolean isConnected();

    void connectTo(int clientID, String host, PlayerInfo info) throws IOException;

    void playerReady(int clientID, boolean value);

}
