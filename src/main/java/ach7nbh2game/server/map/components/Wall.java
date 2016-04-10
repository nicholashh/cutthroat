package ach7nbh2game.server.map.components;

import ach7nbh2game.main.Constants;
import ach7nbh2game.main.Constants.Item;
import ach7nbh2game.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Wall extends AMapComponent {

    private int maxHealth = Constants.wall1;
    private int health = maxHealth;
    private List<Item> items = new ArrayList<>();

    public Wall () {
        this("Wall");
    }

    public Wall (String name) {

        super(name);

        Random rand = new Random();
        boolean dropAtAll = rand.nextDouble() < 0.5;
        double whichItem = rand.nextDouble();

        if (dropAtAll) {
            if (whichItem <= Constants.bulletDropFreq) {
                items.add(Item.BULLET1);
            } else if (whichItem <= Constants.bulletDropFreq+Constants.rocketDropFreq) {
                items.add(Item.ROCKET);
            } else if (whichItem <= Constants.bulletDropFreq+Constants.rocketDropFreq+Constants.healthDropFreq) {
                items.add(Item.HEALTH);
            }
        }

    }

    public Wall (List<Item> itemsIn) {

        this();

        if (itemsIn.size() > 0) {

            Logger.Singleton.log(this, 0, "constructed with items = " + items);

            items.clear();
            items.addAll(itemsIn);

            health = 0;

        }

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
            if (!items.isEmpty()) {
                switch (items.get(0)) {
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

    public List<Item> getItems () {
        return items;
    }

    public void applyDamage (int healthDiff, Client client) {
        health -= healthDiff;
        if (health <= 0) {
            if (items.isEmpty()) {
                removeFromMap();
            }
        }
    }

}
