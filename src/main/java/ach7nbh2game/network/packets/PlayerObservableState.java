package ach7nbh2game.network.packets;

public class PlayerObservableState {

    private int score;
    private int health;

    public int getScore () {
        return score;
    }

    public void setScore (int score) {
        this.score = score;
    }

    public int getHealth () {
        return health;
    }

    public void setHealth (int health) {
        this.health = health;
    }

}
