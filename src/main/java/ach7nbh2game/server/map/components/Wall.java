package ach7nbh2game.server.map.components;

import ach7nbh2game.main.Constants;

import java.util.Random;

public class Wall extends AMapComponent {

    private int maxHealth = Constants.wall1;
    private int health = maxHealth;
    private Constants.Item item = null;

    private Random rand = new Random();

    public Wall () {
        super("Wall");

        boolean dropAtAll = rand.nextDouble() < 0.5;
        double whichItem = rand.nextDouble();

        if (dropAtAll) {
            if (whichItem <= Constants.bulletDropFreq) {
                item = Constants.Item.BULLET1;
            } else if (whichItem <= Constants.bulletDropFreq+Constants.rocketDropFreq) {
                item = Constants.Item.ROCKET;
            } else if (whichItem <= Constants.bulletDropFreq+Constants.rocketDropFreq+Constants.healthDropFreq) {
                item = Constants.Item.HEALTH;
            }
        }
    }

    public Wall(String name) {
        super(name);
    }

    public int getMapChar () {

        if (health > maxHealth*0.75) {
            return "\u2588".codePointAt(0);
        } else if (health > maxHealth*0.5) {
            return "\u2593".codePointAt(0);
        } else if (health > maxHealth*0.25) {
            return "\u2592".codePointAt(0);
        } else if (health > 0){
            return "\u2591".codePointAt(0);
        } else {
            isDead = true;
            if (item != null) {
                switch (item) {
                    case HEALTH: return "\u002B".codePointAt(0);
                    case ROCKET: return "\u2622".codePointAt(0);
                    default: return "\u25CF".codePointAt(0);
                }
            } else {
                removeFromMap();
                return ' ';
            }
        }
    }

    public boolean canDie () {return true;}

    public int getHealth () {
        return health;
    }

    public Constants.Item getItem() {
        return item;
    }

    public void applyDamage (int healthDiff, Client client) {
        health -= healthDiff;
        if (health <= 0) {
            if (item == null) {
                removeFromMap();
            }
        }
    }

}
