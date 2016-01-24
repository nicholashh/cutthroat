package ach7nbh2game.network.adapters;

import ach7nbh2game.client.GameClient;
import java.util.ArrayList;
import java.util.Map;

public class ClientNTOG implements IServerToClient {

    private GameClient client;

    public ClientNTOG (GameClient clientIn) {
        client = clientIn;
    }

    public void enterGame (int clientID) {
        client.enterGame();
    }

    public void updateGameState (int clientID, ArrayList<ArrayList<Integer>> frame) {
        client.updateState(frame);
    }

    public void announceLobbies (int clientID, Map<Integer, String> lobbies) {
        client.updateLobbyList(lobbies);
    }

}
