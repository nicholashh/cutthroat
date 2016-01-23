package ach7nbh2game.server;

import ach7nbh2game.main.Constants;

import java.util.HashSet;

public class Lobby extends APlayerContainer {

    private int gameMapHeight;
    private int gameMapWidth;

    public Lobby () {

        playerIDs = new HashSet<Integer>();
        gameMapHeight = Constants.mapHeight;
        gameMapWidth = Constants.mapWidth;

    }

    public void join (int newID) {

        playerIDs.add(newID);

    }

    public Game startGame () {

        return new Game(playerIDs, gameMapHeight, gameMapWidth);

    }

}
