package ach7nbh2game.network.adapters;

import ach7nbh2game.main.Constants.*;

/**
 * Created by achuie on 1/23/16.
 */
public class ServerNTOG implements IClientToServer {

    /**
     * a client trying to create a new game lobby
     * how do you want to identify the lobby? a unique int? your choice
     */
    public void createNewLobby (String name);

    /**
     * get all lobbies available
     * again, how to represent?
     */
    void requestLobbies ();

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
    void joinLobby (int lobbyID);

    /**
     * turn a lobby into a game
     * again, how to identify lobby? your choice
     */
    void startGame (int lobbyID);

    /**
     * move up, down, left, right, pick up things, etc
     * should return the game state (same return type as previous function)
     * this should be highly easily extensible
     */
    void move (Directions direction);
}
