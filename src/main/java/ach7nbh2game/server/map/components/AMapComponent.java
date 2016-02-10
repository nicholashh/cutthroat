package ach7nbh2game.server.map.components;

import ach7nbh2game.server.map.AMapModifier;
import ach7nbh2game.server.map.GameMap;

public abstract class AMapComponent extends AMapModifier implements IMapComponent {

    private int x;
    private int y;

    private final String componentName;

    public AMapComponent (String componentNameIn) {
        componentName = componentNameIn;
    }

    public int getX () {
        return x;
    }

    public void setX (int xIn) {
        x = xIn;
    }

    public int getY () {
        return y;
    }

    public void setY (int yIn) {
        y = yIn;
    }

    public void placeOnMap (GameMap map, int xIn, int yIn) {

        //Logger.Singleton.log(this, 0, "placeOnMap:");
        //Logger.Singleton.log(this, 1, "map = " + map);
        //Logger.Singleton.log(this, 1, "xIn = " + xIn);
        //Logger.Singleton.log(this, 1, "yIn = " + yIn);

        // TODO: temporarily allowing this for tag-restarting capabilities
        //if (mapIsNull()) {

            map.set(yIn, xIn, this);
            setMap(map);

        //} else {
            // TODO: not allowed
        //}

    }

    @Override
    public String toString () {
        return componentName + "(" + x + "," + y + ")";
    }

}
