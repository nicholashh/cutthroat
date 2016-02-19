package ach7nbh2game.server.map;

import ach7nbh2game.server.Game;

public abstract class AGameActor implements IGameActor {

    private GameMap map;
    private Game game;

    private final String name;

    public AGameActor (String nameIn) {
        name = nameIn;
    }

    public GameMap getMap () {
        return map;
    }

    public void setMap (GameMap mapIn) {
        map = mapIn;
    }

    public boolean mapIsNull () {
        return map == null;
    }

    public Game getGame () {
        return game;
    }

    public void setGame (Game gameIn) {
        game = gameIn;
    }

    public boolean gameIsNull () {
        return game == null;
    }

    public String getName () {
        return name;
    }

    @Override
    public String toString () {
        return name;
    }

}
