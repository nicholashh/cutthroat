package ach7nbh2game.server.map.components;

import ach7nbh2game.main.Constants;
import ach7nbh2game.main.Constants.Direction;
import ach7nbh2game.server.Callback;
import ach7nbh2game.server.map.GameMap;
import ach7nbh2game.util.Logger;

public abstract class Projectile extends AMapComponent {

    private Direction direction;
    private Client owner;
    private int damage;

    public Projectile (String name, Direction directionIn, Client ownerIn, int damageIn) {
        super(name);
        direction = directionIn;
        owner = ownerIn;
        damage = damageIn;
    }

    public Direction getDirection () {
        return direction;
    }

    public Client getOwner () {
        return owner;
    }

    public int getDamage () {
        return damage;
    }

    public void start () {
        setCallback(getGame().requestCallback(new Callback(getSpeed(), -1, () -> move())));
    }

    protected abstract int getSpeed ();

    private void move () {

        GameMap map = getMap();
        IMapComponent thing = map.get(nextLocation(getDirection()));

        if (thing instanceof Ground) {
            interactionWithGround((Ground)thing);
        } else if (thing instanceof Client) {
            interactionWithClient((Client) thing);
        } else if (thing instanceof Wall || thing instanceof Projectile || thing == null) {
            interactionWithKillable(thing);
        } else { /* TODO */ }

    }

    private void interactionWithGround (Ground other) {
        Logger.Singleton.log(this, 0, "moving");
        getMap().swap(this, other);
    }

    private void interactionWithClient (Client other) {
        removeFromMap();
        getCallback().cancel();
        other.applyDamage(getDamage() * getOwner().getState().getGunDmg(), getOwner());
        getGame().addSound(Constants.ServerToClientSound.BULLET_HIT_PLAYER);
    }

    protected abstract void interactionWithKillable (IMapComponent other);

    public void kill () {
        removeFromMap();
        getCallback().cancel();
    }

    // this map component can't really be damaged
    public int getHealth () { return 0; }
    public void applyDamage (int damage, Client killer) {}
    public boolean canDie () {return false;}

}
