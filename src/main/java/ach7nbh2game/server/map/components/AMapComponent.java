package ach7nbh2game.server.map.components;

import ach7nbh2game.main.Constants.Direction;
import ach7nbh2game.server.CallbackRegistration;
import ach7nbh2game.server.map.AGameActor;
import ach7nbh2game.server.map.GameMap;
import ach7nbh2game.util.id.Coordinate;

public abstract class AMapComponent extends AGameActor implements IMapComponent {

    private int x;
    private int y;
    private CallbackRegistration callback;

    public AMapComponent (String name) {
        super(name);
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

    public CallbackRegistration getCallback () {
        return callback;
    }

    public void setCallback (CallbackRegistration callbackIn) {
        callback = callbackIn;
    }

    public boolean callbackIsNull () {
        return callback == null;
    }

    public void placeOnMap (GameMap map, int xIn, int yIn) {

        if (mapIsNull()) {

            map.set(yIn, xIn, this);
            setMap(map);

        } else {
            // TODO: not allowed
        }

    }

    boolean isDead = false;

    public void placeOnMap(GameMap map, Coordinate loc) {
        map.set(loc.y, loc.x, this);
        setMap(map);
        isDead = false;
    }

    public void removeFromMap () {
        removeFromMap(new Ground());
    }

    public void removeFromMap (AMapComponent replaceWith) {
        replaceWith.placeOnMap(getMap(), x, y);
        isDead = true;
    }

    public boolean isDead () {
        return isDead;
    }

    public Coordinate nextLocation (Direction direction) {

        switch (direction) {
            case UP:
                return new Coordinate(y - 1, x);
            case DOWN:
                return new Coordinate(y + 1, x);
            case LEFT:
                return new Coordinate(y, x - 1);
            case RIGHT:
                return new Coordinate(y, x + 1);
        }

        // TODO
        return null;

    }

    @Override
    public String toString () {
        return super.toString() + "(" + x + "," + y + ")";
    }

}
