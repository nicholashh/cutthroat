package ach7nbh2game.server;

import ach7nbh2game.client.PlayerInfo;
import ach7nbh2game.main.Constants;

import java.util.HashMap;

public class Lobby extends APlayerContainer {

    // TODO state design pattern for lobbies -> games
    private GameServer server;

    private String name;

    private int gameMapHeight;
    private int gameMapWidth;

    public Lobby (GameServer serverIn, String nameIn) {

        server = serverIn;

        name = nameIn;
        playerInfo = new HashMap<Integer, PlayerInfo>();
        gameMapHeight = Constants.mapHeight;
        gameMapWidth = Constants.mapWidth;

    }

    public void join (int newID, PlayerInfo info) {

        playerInfo.put(newID, info);

    }

    public Game startGame () {

        return new Game(server, playerInfo, gameMapHeight, gameMapWidth);

    }

    public String getName () {

        return name;

    }

}
