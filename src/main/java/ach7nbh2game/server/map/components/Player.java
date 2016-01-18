package ach7nbh2game.server.map.components;

public class Player extends AMapComponent {

    private int id;
    private int y;
    private int x;

    public Player (int idIn, int yIn, int xIn) {

        id = idIn;
        y = yIn;
        x = xIn;

    }

    public int getY () {
        return y;
    }

    public int getX () {
        return x;
    }

    public void setY (int newY) {
        y = newY;
    }

    public void setX (int newX) {
        x = newX;
    }

    public int getMapChar () {
        return 42;
    }

}
