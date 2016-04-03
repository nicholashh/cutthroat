package ach7nbh2game.network.packets;

public class PlayerObservableState {

    private double score;
    private int health;

    public double getScore () {
        return score;
    }

    public void setScore (double score) {
        this.score = score;
    }

    public int getHealth () {
        return health;
    }

    public void setHealth (int health) {
        this.health = health;
    }

}
