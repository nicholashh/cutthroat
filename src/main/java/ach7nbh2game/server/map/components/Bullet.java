package ach7nbh2game.server.map.components;

import ach7nbh2game.main.Constants;
import ach7nbh2game.main.Constants.Direction;
import ach7nbh2game.util.Logger;

public class Bullet extends Projectile {

    public Bullet (Direction direction, Client owner, int damage) {
        super("Bullet", direction, owner, damage);
    }

    protected int getSpeed () {
        return 2;
    }

    public int getMapChar () {
        return 46;
    }

    protected void interactionWithKillable (IMapComponent other) {

        Logger.Singleton.log(this, 0, "killing self");
        this.kill();

        other.applyDamage(getDamage() * getOwner().getState().getGunDmg(), getOwner());

        if (other instanceof Projectile) {

            Logger.Singleton.log(this, 1, "killing other projectile, too");
            ((Projectile)other).kill();

            getGame().addSound(Constants.ServerToClientSound.BULLET_HIT_BULLET);
        } else {
            getGame().addSound(Constants.ServerToClientSound.BULLET_HIT_WALL);
        }

    }

}
