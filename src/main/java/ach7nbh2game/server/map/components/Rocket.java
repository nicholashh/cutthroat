package ach7nbh2game.server.map.components;

import ach7nbh2game.main.Constants;
import ach7nbh2game.main.Constants.Direction;
import ach7nbh2game.server.Callback;
import ach7nbh2game.util.Logger;

public class Rocket extends Projectile {

    public Rocket (Direction direction, Client owner, int damage) {
        super("Rocket", direction, owner, damage);
    }

    protected int getSpeed () {
        return 2;
    }

    public int getMapChar () {
        switch (getDirection()) {
            case UP:    return '^';
            case LEFT:  return '<';
            case DOWN:  return 'v';
            case RIGHT: return '>';
            default: throw new RuntimeException("wtf");
        }
    }

    protected void interactionWithKillable (IMapComponent other) {

        Logger.Singleton.log(this, 0, "killing self");
        this.kill();

        int x = other.getX();
        int y = other.getY();

        if (other instanceof Projectile) {
            Logger.Singleton.log(this, 1, "killing other projectile, too");
            ((Projectile)other).kill();
        }

        getGame().addSound(Constants.ServerToClientSound.ROCKET_EXPLODE);

        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                int newX = x + i;
                int newY = y + j;

                IMapComponent thing = getMap().get(newY, newX);
                Logger.Singleton.log(this, 1, "i = " + i + ", j = " + j +
                        ", x = " + newX + ", y = " + newY + ", thing = " + thing);

                if (thing == null) continue;

                int level = 0;
                boolean leftRightEdge = false;
                boolean topBottomEdge = false;
                int damageVal = Constants.rocket1;

                /*
                 *  (-2, 2)(-1, 2)( 0, 2)( 1, 2)( 2, 2)
                 *
                 *  (-2, 1)(-1, 1)( 0, 1)( 1, 1)( 2, 1)
                 *
                 *  (-2, 0)(-1, 0)       ( 1, 0)( 2, 0)
                 *
                 *  (-2,-1)(-1,-1)( 0,-1)( 1,-1)( 2,-1)
                 *
                 *  (-2,-2)(-1,-2)( 0,-2)( 1,-2)( 2,-2)
                 */
                if (Math.abs(i) == 2 || Math.abs(j) == 2) {
                    if (Math.abs(i) == 2) {leftRightEdge = true;}
                    if (Math.abs(j) == 2) {topBottomEdge = true;}
                    damageVal *= 0.25;
                    level = 2;
                } else if (Math.abs(i) == 1 || Math.abs(j) == 1) {
                    if (Math.abs(i) == 1) {leftRightEdge = true;}
                    if (Math.abs(j) == 1) {topBottomEdge = true;}
                    damageVal *= 0.5;
                    level = 1;
                }

                final int levelFinal = level, ii = i, jj = j;
                final boolean leftRightFinal = leftRightEdge;
                final boolean topBottomFinal = topBottomEdge;
                final boolean cornerFinal = leftRightFinal && topBottomFinal;

                thing.applyDamage(damageVal, getOwner());

                if ((thing.isDead() || !thing.canDie()) && !(thing instanceof Client)) {

                    Logger.Singleton.log(this, 2, "making new ground");

                    Ground newGround = new Ground () {

                        private int mapChar = ' ';

                        private final int delayOffset = 3;

                        {

                            int delay = 1 + delayOffset * levelFinal;

                            setCallback(Rocket.this.getGame().requestCallback(new Callback(delay, 1, () -> {

                                Logger.Singleton.log(Rocket.this, 0, "ground callback 1 of 2");
                                Logger.Singleton.log(this, 1, "i = " + ii + ", j = " + jj +
                                        ", x = " + newX + ", y = " + newY + ", thing = " + thing);

                                if (cornerFinal) {
                                    mapChar = '.';
                                } else if (leftRightFinal) {
                                    mapChar = '|';
                                } else if (topBottomFinal) {
                                    mapChar = '-';
                                } else {
                                    // TODO
                                }

                                setCallback(Rocket.this.getGame().requestCallback(new Callback(delayOffset, 1, () -> {

                                    Logger.Singleton.log(Rocket.this, 0, "ground callback 2 of 2");
                                    Logger.Singleton.log(this, 1, "i = " + ii + ", j = " + jj +
                                            ", x = " + newX + ", y = " + newY + ", thing = " + thing);

                                    mapChar = ' ';

                                })));

                            })));

                        }

                        public int getMapChar () {
                            return mapChar;
                        }

                    };

                    newGround.placeOnMap(getMap(), newX, newY);

                } else {
                    Logger.Singleton.log(this, 2, "NOT making new ground");
                }

            }
        }

    }

}
