package ach7nbh2game.network.adapters;

import ach7nbh2game.network.NetServer;
import ach7nbh2game.server.GameState;

import java.util.Map;
import java.util.Set;

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
    public void updateGameState (int clientID, GameState state) {
        nserver.updateGameState(clientID, state);
    }

    /**
     * send all available lobbies
     */
    public void announceLobbies (int clientID,
            Map<Integer, String> lobbies,
            Map<Integer, String> players,
            Map<Integer, Set<Integer>> lobbyToPlayers) {
        nserver.announceLobbies(clientID, lobbies, players, lobbyToPlayers);
    }

}
