package ach7nbh2game.server.map;

import ach7nbh2game.server.Game;

public interface IGameActor {

    GameMap getMap ();
    void setMap (GameMap mapIn);
    boolean mapIsNull ();

    Game getGame ();
    void setGame (Game gameIn);
    boolean gameIsNull ();

}
