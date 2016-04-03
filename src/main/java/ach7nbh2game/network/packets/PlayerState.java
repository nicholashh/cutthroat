package ach7nbh2game.network.packets;

import ach7nbh2game.main.Constants;

public class PlayerState {

    private int health = Constants.clientHealth;
    private double score = 0;

    private int gunDmg = Constants.gun1;
    private int pickaxeDmg = Constants.pickaxe1;
    private int bulletDmg = Constants.bullet1;
    //TODO start with rocket0 and no ammo
    private Constants.Item rocket = Constants.Item.RL1;
    private int rocketAmmo = 3;
    private int ammo = Constants.initAmmo;

    public void setHealth(int healthIn) {
        if (healthIn < 0) {
            health = 0;
        } else {
            health = healthIn;
        }
    }

    public int getHealth() {
        return health;
    }

    public void setScore(double scoreIn) {
        score = scoreIn;
    }

    public double getScore() {
        return score;
    }

    // "Inventory"

    public int getGunDmg() {
        return gunDmg;
    }

    public void setGunDmg(int dmg) {
        gunDmg = dmg;
    }

    public void upgradeGun( int dmg) {
        if (dmg > gunDmg) {
            gunDmg = dmg;
        }
    }

    public int getPickaxeDmg() {
        return pickaxeDmg;
    }

    public void setPickaxeDmg(int dmg) {
        pickaxeDmg = dmg;
    }

    public void upgradePickaxe(int dmg) {
        if (dmg > pickaxeDmg) {
            pickaxeDmg = dmg;
        }
    }

    public int getBulletDmg() {
        return bulletDmg;
    }

    public void setBulletDmg(int dmg) {
        bulletDmg = dmg;
    }

    public void upgradeBullet(int dmg) {
        if (dmg > bulletDmg) {
            bulletDmg = dmg;
        }
    }

    public boolean hasRocket() {
        return rocket == Constants.Item.RL1;
    }

    public void pickupRocket() {
        rocket = Constants.Item.RL1;
        rocketAmmo = 3;
    }

    public int getRocketDmg() {
        if (hasRocket()) {
            return Constants.rocket1;
        } else {
            return 0;
        }
    }

    public int getRocketAmmo() {
        return rocketAmmo;
    }

    public void setRocketAmmo(int newammo) {
        rocketAmmo = newammo;
    }

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int newammo) {
        ammo = newammo;
    }

    public void respawn() {
        health = Constants.clientHealth;

        gunDmg = Constants.gun1;
        pickaxeDmg = Constants.pickaxe1;
        bulletDmg = Constants.bullet1;
        //TODO remove
        rocketAmmo = 3;
        ammo = Constants.initAmmo;
    }
}
