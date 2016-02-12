package ach7nbh2game.server.map.components;

import ach7nbh2game.main.Constants.Direction;
import ach7nbh2game.server.CallbackRequest;
import ach7nbh2game.server.map.GameMap;

public class Bullet extends AMapComponent {

    private Direction direction;
    private Client owner;

    private CallbackRequest callbackRequest;

    private int mapChar = 46;

    public Bullet (Direction directionIn, Client ownerIn) {
        super("Bullet");
        direction = directionIn;
        owner = ownerIn;
    }

    public void start () {
        callbackRequest = new CallbackRequest(1, -1, () -> move());
        getGame().requestCallback(callbackRequest);
    }

    private void move () {

        GameMap map = getMap();
        IMapComponent thing = map.get(nextLocation(direction));

        if (thing instanceof Ground) {

            map.swap(this, thing);

        } else if (thing instanceof Client) {

            // TODO

            // just for now...
            mapChar = 167;
            switch (direction) {
                case UP:
                    direction = Direction.LEFT;
                    break;
                case LEFT:
                    direction = Direction.DOWN;
                    break;
                case DOWN:
                    direction = Direction.RIGHT;
                    break;
                case RIGHT:
                    direction = Direction.UP;
                    break;
            }

        } else if (thing instanceof Wall || thing instanceof Bullet || thing == null) {

            removeFromMap();
            callbackRequest.cancel();

        }

        getGame().updateAllPlayers();

    }

    public int getMapChar () {
        return mapChar;
    }

}
