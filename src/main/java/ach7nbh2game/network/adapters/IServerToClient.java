package ach7nbh2game.network.adapters;

import ach7nbh2game.server.GameState;

import java.util.Map;

public interface IServerToClient {

    /**
     * notify a client that the game (for the lobby they are in) has started
     */
    void enterGame (int clientID);

    /**
     * send a client a new game state
     */
    void updateGameState (int clientID, GameState state);

    /**
     * send all available lobbies
     */
    void announceLobbies (int clientID, Map<Integer, String> lobbies);

}
