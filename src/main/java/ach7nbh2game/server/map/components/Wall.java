package ach7nbh2game.server.map.components;

import ach7nbh2game.main.Constants;
import ach7nbh2game.server.PlayerState;

import java.util.Random;

public class Wall extends AMapComponent {

    private int maxHealth = Constants.wall1;
    private int health = maxHealth;

    private Random rand = new Random();

    public Wall () {
        super("Wall");
    }

    public Wall(String name) {
        super(name);
    }

    public int getMapChar () {

        // return 35;
        if (health > maxHealth*0.75) {
            return "\u2588".codePointAt(0);
        } else if (health > maxHealth*0.5) {
            return "\u2593".codePointAt(0);
        } else if (health > maxHealth*0.25) {
            return "\u2592".codePointAt(0);
        } else {
            return "\u2591".codePointAt(0);
        }

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
            PlayerState state = client.getState();

            boolean dropGun2 = rand.nextDouble() < 0.05;
            boolean dropBullet1 = rand.nextDouble() < 0.5;
            boolean dropPick2 = rand.nextDouble() < 0.05;

            if (dropGun2) {
                state.upgradeGun(Constants.gun2);
            }
            if (dropBullet1) {
                client.incAmmo(3);
            }
            if (dropPick2) {
                state.upgradePickaxe(Constants.pickaxe2);
            }
        }
    }

}
