package ach7nbh2game.server.map.components;

import ach7nbh2game.main.Constants;

public class CavernWall extends Wall {

    private int maxHealth = Constants.wall1;
    private int health = maxHealth;

    private Cavern cavern;
    private Constants.Item item = null;
    private boolean visible = false;

    public CavernWall(Cavern cavernIn) {
        super("CavernWall");
        cavern = cavernIn;
    }

    @Override
    public int getMapChar() {
        if (!visible) {
            if (health > maxHealth*0.75) {
                return "\u2588".codePointAt(0);
            } else if (health > maxHealth*0.5) {
                return "\u2593".codePointAt(0);
            } else if (health > maxHealth*0.25) {
                return "\u2592".codePointAt(0);
            } else {
                return "\u2591".codePointAt(0);
            }
        } else {
            if (item != null) {
                return "\u25CF".codePointAt(0);
            } else {
                removeFromMap();
                return ' ';
            }
        }
    }

    @Override
    public void decHealth(Client client, int healthDiff) {
        health -= healthDiff;
        if (health <= 0) {
            visible = true;
            cavern.discovered();
            if (item == null) {
                removeFromMap();
            }

        }
    }

    @Override
    public void removeFromMap () {

        Ground newGround = new Ground();
        newGround.placeOnMap(getMap(), getX(), getY());

    }

    public void setItem(Constants.Item itemIn) {
        item = itemIn;
    }

    public Constants.Item getItem() {
        return item;
    }

    public void setVisible() {
        visible = true;
    }

    public boolean getVisible() {
        return visible;
    }
}
