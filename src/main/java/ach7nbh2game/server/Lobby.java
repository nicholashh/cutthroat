package ach7nbh2game.server;

import java.util.HashSet;

public class Lobby extends APlayerContainer {

    private int gameMapHeight;
    private int gameMapWidth;

    public Lobby () {

        playerIDs = new HashSet<Integer>();
        gameMapHeight = 20;
        gameMapWidth = 40;

    }

    public void join (int newID) {

        playerIDs.add(newID);

    }

    public Game startGame () {

        return new Game(playerIDs, gameMapHeight, gameMapWidth);

    }

}
