package ach7nbh2game.server;

import ach7nbh2game.main.Constants;

public class PlayerState {

    private int health = Constants.clientHealth;
    private int score = 0;

    private int gunDmg = Constants.gun1;
    private int pickaxeDmg = Constants.pickaxe1;
    private int bulletDmg = Constants.bullet1;
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

    public void setScore(int scoreIn) {
        score = scoreIn;
    }

    public int getScore() {
        return score;
    }

    // "Inventory"

    public int getGunDmg() {
        return gunDmg;
    }

    public void setGunDmg(int dmg) {
        gunDmg = dmg;
    }

    public int getPickaxeDmg() {
        return pickaxeDmg;
    }

    public void setPickaxeDmg(int dmg) {
        pickaxeDmg = dmg;
    }

    public int getBulletDmg() {
        return bulletDmg;
    }

    public void setBulletDmg(int dmg) {
        bulletDmg = dmg;
    }

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int newammo) {
        ammo = newammo;
    }
}
