package ach7nbh2game.server;

import ach7nbh2game.client.PlayerInfo;
import ach7nbh2game.main.Constants.Directions;
import ach7nbh2game.server.map.GameMap;

import java.util.Map;

public class Game extends APlayerContainer {

    private GameServer server;
    private GameMap map;

    public Game (GameServer serverIn, Map<Integer, PlayerInfo> playerIDsIn, int height, int width) {

        server = serverIn;
        playerInfo = playerIDsIn;

        map = new GameMap(this, height, width);

        for (int playerID : playerInfo.keySet()) {
            map.addNewPlayer(playerID, playerInfo.get(playerID));
        }

        map.startLevel();

    }

    public GameState getGameState (int clientID) {

        GameState gameState = map.getGameState();
        gameState.setFrame(map.getMapView(clientID));
        return gameState;

    }

    public void move (int clientID, Directions direction) {
        map.move(clientID, direction);
    }

    public void broadcastState() {
        server.sendGameState(this);
    }

}
