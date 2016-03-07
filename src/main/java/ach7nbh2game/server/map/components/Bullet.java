package ach7nbh2game.server.map.components;

import ach7nbh2game.main.Constants.Direction;
import ach7nbh2game.server.Callback;
import ach7nbh2game.server.map.GameMap;
import ach7nbh2game.util.Logger;

import java.util.Random;

public class Bullet extends AMapComponent {

    private Direction direction;
    private Client owner;
    private int damage;

    private int mapChar = 46;

    private Random rand = new Random();

    public Bullet (Direction directionIn, Client ownerIn, int damageIn) {
        super("Bullet");
        direction = directionIn;
        owner = ownerIn;
        damage = damageIn;
    }

    public Client getOwner() {
        return owner;
    }

    public void start () {
        setCallback(getGame().requestCallback(new Callback(2, -1, () -> move())));
    }

    private void move () {
        move(4);
    }

    private void move (int numTries) {

        GameMap map = getMap();
        IMapComponent thing = map.get(nextLocation(direction));

        if (thing instanceof Ground) {

            Logger.Singleton.log(this, 0, "moving " + direction);

            map.swap(this, thing);

        } else if (thing instanceof Client) {

            // TODO
            removeFromMap();
            getCallback().cancel();

            Client player = (Client) thing;
            player.decHealth(damage*owner.getState().getGunDmg(), this);

            // just for now...
//            mapChar = 42;
//            boolean which = rand.nextBoolean();
//            switch (direction) {
//                case UP:
//                    direction = which ? Direction.LEFT : Direction.RIGHT;
//                    break;
//                case LEFT:
//                    direction = which ? Direction.DOWN : Direction.UP;
//                    break;
//                case DOWN:
//                    direction = which ? Direction.RIGHT : Direction.LEFT;
//                    break;
//                case RIGHT:
//                    direction = which ? Direction.UP : Direction.DOWN;
//                    break;
//            }

//            if (numTries > 0) {
//                move(numTries - 1);
//            }

        } else if (thing instanceof Wall || thing instanceof Bullet || thing == null) {

            Logger.Singleton.log(this, 0, "killing self");

            removeFromMap();
            getCallback().cancel();

            if (thing instanceof Bullet) {

                Logger.Singleton.log(this, 1, "killing other bullet, too");

                Bullet other = (Bullet)thing;
                other.getCallback().cancel();
                other.removeFromMap();

            }

        }

    }

    public int getMapChar () {
        return mapChar;
    }

}
