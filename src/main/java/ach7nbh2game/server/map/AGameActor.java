package ach7nbh2game.server.map;

import ach7nbh2game.server.Game;

public abstract class AGameActor implements IMapModifier {

    private GameMap map;
    private Game game;

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

}
