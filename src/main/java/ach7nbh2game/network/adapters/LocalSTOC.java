package ach7nbh2game.network.adapters;

import ach7nbh2game.client.GameClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    public void updateGameState (int clientID, ArrayList<ArrayList<Integer>> frame) {
        clients.get(clientID).updateState(frame);
    }

    public void announceLobbies (int clientID, Map<Integer, String> lobbies) {
        clients.get(clientID).updateLobbyList(lobbies);
    }

}
