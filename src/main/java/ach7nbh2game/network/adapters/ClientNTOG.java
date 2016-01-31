package ach7nbh2game.network.adapters;

import ach7nbh2game.client.GameClient;
import ach7nbh2game.server.GameState;

import java.util.Map;
import java.util.Set;

public class ClientNTOG implements IServerToClient {

    private GameClient client;

    public ClientNTOG (GameClient clientIn) {
        client = clientIn;
    }

    public void enterGame (int clientID) {
        client.enterGame();
    }

    public void updateGameState (int clientID, GameState state) {
        client.updateState(state);
    }

    public void announceLobbies (int clientID,
            Map<Integer, String> lobbies,
            Map<Integer, String> players,
            Map<Integer, Set<Integer>> lobbyToPlayers) {
        client.updateLobbyList(lobbies, players, lobbyToPlayers);
    }

}
