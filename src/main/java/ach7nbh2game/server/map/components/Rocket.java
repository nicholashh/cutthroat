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
        return 1;
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

        getGame().addSound(Constants.ServerToClientSound.ROCKET_LAUNCH);

        if (other instanceof Projectile) {
            Logger.Singleton.log(this, 1, "killing other projectile, too");
            ((Projectile)other).kill();
        }

        int x = other.getX();
        int y = other.getY();
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                int newX = x + i;
                int newY = y + j;

                IMapComponent thing = getMap().get(newX, newY);

                if (thing == null) break;

                int level = 0;
                boolean left = false, right = false, top = false, bottom = false, corner = false;
                int damageVal = Constants.rocket1;
                if (Math.abs(i) == 2 || Math.abs(j) == 2) {
                    damageVal *= 0.25;
                    level = 2;
                    if (i == -2) {left = true;}
                    if (i == 2) {right = true;}
                    if (j == -2) {top = true;}
                    if (j == 2) {bottom = true;}
                } else if (Math.abs(i) == 1 || Math.abs(j) == 1) {
                    damageVal *= 0.5;
                    level = 1;
                    if (i == -1) {left = true;}
                    if (i == 1) {right = true;}
                    if (j == -1) {top = true;}
                    if (j == 1) {bottom = true;}
                }
                if ((left || right) && (top || bottom)) {
                    corner = true;
                }

                final int levelFinal = level;
                final boolean topFinal = top;
                final boolean bottomFinal = bottom;
                final boolean leftFinal = left;
                final boolean rightFinal = right;
                final boolean cornerFinal = corner;

                thing.applyDamage(damageVal, getOwner());

                if (thing.isDead() || !thing.canDie()) {

                    Ground newGround = new Ground () {

                        private int mapChar = ' ';

                        private final int delayOffset = 6;

                        {

                            int delay = 1 + delayOffset * levelFinal;

                            setCallback(getGame().requestCallback(new Callback(delay, 1, () -> {

                                if (cornerFinal) {
                                    mapChar = '.';
                                } else if (leftFinal || rightFinal) {
                                    mapChar = '|';
                                } else if (topFinal || bottomFinal) {
                                    mapChar = '-';
                                } else {
                                    // TODO
                                }

                                setCallback(getGame().requestCallback(new Callback(delayOffset, 1, () -> {
                                    mapChar = ' ';
                                })));

                            })));

                        }

                        public int getMapChar () {
                            return mapChar;
                        }

                    };

                    newGround.placeOnMap(getMap(), newX, newY);

                }

            }
        }

    }

}
