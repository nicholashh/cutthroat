package ach7nbh2game.network.adapters;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by achuie on 1/23/16.
 */
public class ClientNTOG {

    /**
     * notify a client that the game (for the lobby they are in) has started
     */
    void enterGame (int clientID);

    /**
     * send a client a new game state
     */
    void updateGameState (int clientID, ArrayList<ArrayList<Integer>> frame);

    /**
     * send all available lobbies
     */
    void announceLobbies (int clientID, Map<Integer, String> lobbies);
}
