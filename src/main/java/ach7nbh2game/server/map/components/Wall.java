package ach7nbh2game.server.map.components;

import ach7nbh2game.main.Constants;

public class Wall extends AMapComponent {

    private int health = Constants.wall1;

    public Wall () {
        super("Wall");
    }

    public int getMapChar () {

        return "\u2592".codePointAt(0);

    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int newhealth) {
        health = newhealth;
    }

    public void decHealth(Client client, int healthDiff) {
        health -= healthDiff;
        if (health <= 0) {
            removeFromMap();

            client.getState();
        }
    }

}
