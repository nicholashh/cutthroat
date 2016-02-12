package ach7nbh2game.server.map.components;

import ach7nbh2game.main.Constants.Direction;
import ach7nbh2game.server.CallbackRequest;
import ach7nbh2game.server.map.GameMap;

import java.util.Random;

public class Bullet extends AMapComponent {

    private Direction direction;
    private Client owner;

    private CallbackRequest callbackRequest;

    private int mapChar = 46;

    private Random rand = new Random();

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
            mapChar = 42;
            boolean which = rand.nextBoolean();
            switch (direction) {
                case UP:
                    direction = which ? Direction.LEFT : Direction.RIGHT;
                    break;
                case LEFT:
                    direction = which ? Direction.DOWN : Direction.UP;
                    break;
                case DOWN:
                    direction = which ? Direction.RIGHT : Direction.LEFT;
                    break;
                case RIGHT:
                    direction = which ? Direction.UP : Direction.DOWN;
                    break;
            }

        } else if (thing instanceof Wall || thing instanceof Bullet || thing == null) {

            removeFromMap();
            callbackRequest.cancel();

            if (thing instanceof Bullet) {

                Bullet other = (Bullet)thing;
                other.removeFromMap();
                other.callbackRequest.cancel();

            }

        }

        getGame().updateAllPlayers();

    }

    public int getMapChar () {
        return mapChar;
    }

}
