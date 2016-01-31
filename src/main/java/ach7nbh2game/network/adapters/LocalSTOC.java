package ach7nbh2game.network.adapters;

import ach7nbh2game.client.GameClient;
import ach7nbh2game.server.GameState;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LocalSTOC implements IServerToClient {

    private Map<Integer, GameClient> clients;

    public LocalSTOC () {
        clients = new HashMap<Integer, GameClient>();
    }

    public void addClient (int clientID, GameClient client) {
        clients.put(clientID, client);
    }

    public void enterGame (int clientID) {
        clients.get(clientID).enterGame();
    }

    public void updateGameState (int clientID, GameState state) {
        clients.get(clientID).updateState(state);
    }

    public void announceLobbies (int clientID,
            Map<Integer, String> lobbies,
            Map<Integer, String> players,
            Map<Integer, Set<Integer>> lobbyToPlayers) {
        clients.get(clientID).updateLobbyList(lobbies, players, lobbyToPlayers);
    }

}
