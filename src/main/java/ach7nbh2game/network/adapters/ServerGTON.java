package ach7nbh2game.network.adapters;

import ach7nbh2game.network.NetServer;

import java.util.ArrayList;
import java.util.Map;

public class ServerGTON implements IServerToClient {

    NetServer nserver;

    public ServerGTON(NetServer netserver) {
        nserver = netserver;
    }

    /**
     * notify a client that the game (for the lobby they are in) has started
     */
    public void enterGame (int clientID) {
        nserver.enterGame(clientID);
    }

    /**
     * send a client a new game state
     */
    public void updateGameState (int clientID, ArrayList<ArrayList<Integer>> frame) {
        nserver.updateGameState(clientID, frame);
    }

    /**
     * send all available lobbies
     */
    public void announceLobbies (int clientID, Map<Integer, String> lobbies) {
        nserver.announceLobbies(clientID, lobbies);
    }
}
