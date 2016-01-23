package ach7nbh2game.network.adapters;

import ach7nbh2game.network.NetClient;

import java.util.ArrayList;

/**
 * Created by achuie on 1/23/16.
 */
public class ClientToServer implements IClientToServer {

    ArrayList<NetClient> clients = new ArrayList<NetClient>();

    /**
     * a client trying to create a new game lobby
     * how do you want to identify the lobby? a unique int? your choice
     */
    public void createNewLobby ();

    /**
     * get all lobbies available
     * again, how to represent?
     */
    public void getLobbies ();

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
    public boolean joinLobby (int clientID, int lobbyID);

    /**
     * turn a lobby into a game
     * again, how to identify lobby? your choice
     */
    public void startGame(final int lobbyID) {
        //TODO
    }

    public void newState(GameState gs) {
        //TODO
    }

    /**
     * get the view of the map from this player's perspective
     * i agree with what you said earlier about making this more general
     * perhaps a getGameState(clientID) method?
     * you can determine return type, but i strongly thingchatFrame it should change
     * i think the best choice would be an object with getters
     */
    ArrayList<ArrayList<Integer>> getMapView (int clientID, int gameID);

    /**
     * move up, down, left, right, pick up things, etc
     * should return the game state (same return type as previous function)
     * this should be highly easily extensible
     */
    public void moveUp(int clientID, int gameID) {
        clients.get(clientID).moveUp();
    }

    public void moveLeft(int clientID, int gameID) {
        clients.get(clientID).moveLeft();
    }

    public void moveDown(int clientID, int gameID) {
        clients.get(clientID).moveDown();
    }

    public void moveRight(int clientID, int gameID) {
        clients.get(clientID).moveRight();
    }
}
