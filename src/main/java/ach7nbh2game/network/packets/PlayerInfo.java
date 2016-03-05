package ach7nbh2game.network.packets;

import ach7nbh2game.main.Constants;

public class PlayerInfo {

    private String username;
    private int icon;
    private int id;
    private int health = Constants.clientHealth;
    private int score = 0;

    public void setUsername (String usernameIn) {
        username = usernameIn;
    }

    public String getUsername () {
        return username;
    }

    public void setIcon (int iconIn) {
        icon = iconIn;
    }

    public int getIcon () {
        return icon;
    }

    public void setID (int idIn) {
        id = idIn;
    }

    public int getID () {
        return id;
    }

    public void setHealth(int healthIn) {
        health = healthIn;
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

}
