package ach7nbh2game.network.adapters;

import ach7nbh2game.network.packets.GameState;
import ach7nbh2game.network.packets.PlayerInfo;

import java.util.Map;
import java.util.Set;

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
    void announceLobbies (int clientID,
            Map<Integer, String> lobbies,
            Map<Integer, String> players,
            Map<Integer, Set<Integer>> lobbyToPlayers);

    void theWinnerIs(PlayerInfo client);
}
