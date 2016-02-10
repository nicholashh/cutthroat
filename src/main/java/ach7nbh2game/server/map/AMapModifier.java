package ach7nbh2game.server.map;

public abstract class AMapModifier implements IMapModifier {

    private GameMap map;

    public GameMap getMap () {
        return map;
    }

    public void setMap (GameMap mapIn) {
        map = mapIn;
    }

    public boolean mapIsNull () {
        return map == null;
    }

}
