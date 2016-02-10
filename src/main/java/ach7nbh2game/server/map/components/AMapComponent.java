package ach7nbh2game.server.map.components;

import ach7nbh2game.server.map.GameMap;
import ach7nbh2game.util.Logger;

abstract class AMapComponent implements IMapComponent {

    private GameMap map;
    private int x;
    private int y;

    protected GameMap getMap () {
        return map;
    }

    public boolean mapIsNull () {
        return map == null;
    }

    public void setMap (GameMap mapIn) {
        map = mapIn;
    }

    protected int getX () {
        return x;
    }

    public void setX (int xIn) {
        x = xIn;
    }

    protected int getY () {
        return y;
    }

    public void setY (int yIn) {
        y = yIn;
    }

    public void placeOnMap (GameMap mapIn, int xIn, int yIn) {

        Logger.Singleton.log(this, 0, "placeOnMap:");
        Logger.Singleton.log(this, 1, "mapIn = " + mapIn);
        Logger.Singleton.log(this, 1, "xIn = " + xIn);
        Logger.Singleton.log(this, 1, "yIn = " + yIn);

        // TODO: temporarily allowing this for tag-restarting capabilities
        //if (mapIsNull()) {
            map = mapIn;
            x = xIn;
            y = yIn;
        //} else {
            // TODO: not allowed
        //}

    }

}
