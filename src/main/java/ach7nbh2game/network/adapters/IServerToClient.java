package ach7nbh2game.network.adapters;

import ach7nbh2game.network.packets.GameState;
import ach7nbh2game.network.packets.PlayerInfo;
import ach7nbh2game.util.id.Pair;

import java.util.Map;
import java.util.Set;

public interface IServerToClient {

    /**
     * notify a client that the game (for the lobby they are in) has started/ended
     */
    void enterGame (int clientID);
    void endGame (int clientID, PlayerInfo winner);

    /**
     * send a client a new game state
     */
    void updateGameState (int clientID, GameState state);

    /**
     * send all available lobbies
     */
    void announceLobbies (int clientID,
            Map<Integer, String> lobbies,
            Map<Integer, Pair<String, Boolean>> players,
            Map<Integer, Set<Integer>> lobbyToPlayers);

}
