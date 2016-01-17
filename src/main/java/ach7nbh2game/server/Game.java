package ach7nbh2game.server;

import ach7nbh2game.server.map.Map;

import java.util.ArrayList;
import java.util.Set;

class Game extends APlayerContainer {

    private Map map;

    public Game (Set<Integer> playerIDsIn, int height, int width) {

        playerIDs = playerIDsIn;

        map = new Map(height, width);

        for (int playerID : playerIDs) {
            map.addNewPlayer(playerID);
        }

    }

    public ArrayList<ArrayList<Integer>> getMapView (int clientID) {

        return map.getMapView(0, 0, 20, 40);

    }

}
