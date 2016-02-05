package ach7nbh2game.server.map.components;

import ach7nbh2game.main.Constants.Directions;

public class Bullet extends AMapComponent {

    private int y, x; // TODO players also have this; factor out to abstract class
    private Directions direction;
    private Player player;

    public Bullet(int yIn, int xIn, Directions directionIn, Player playerIn) {
        y = yIn;
        x = xIn;
        direction = directionIn;
        player = playerIn;
    }

    public int getY () {
        return y;
    }

    public int getX () {
        return x;
    }

    public void setY (int yIn) {
        y = yIn;
    }

    public void setX (int xIn) {
        x = xIn;
    }

    public Directions getDirection () {
        return direction;
    }

    public Player getOwner () {
        return player;
    }

    public int getMapChar () {
        return 46;
    }

}
