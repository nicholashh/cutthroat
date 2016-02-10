package ach7nbh2game.server.map;

public interface IMapModifier {

    GameMap getMap ();
    void setMap (GameMap mapIn);
    boolean mapIsNull ();

}
