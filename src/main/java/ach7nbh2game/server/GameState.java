package ach7nbh2game.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameState {

    private ArrayList<ArrayList<Integer>> frame;
    private Map<String, Integer> scores;
    private int timeRemaining;
    private String whoItIs;

    public GameState () {
        scores = new HashMap<String, Integer>();
    }

    public void setFrame (ArrayList<ArrayList<Integer>> newFrame) {
        frame = newFrame;
    }

    public ArrayList<ArrayList<Integer>> getFrame () {
        return frame;
    }

    public Map<String, Integer> getScores() {
        return scores;
    }

    public void updateScore (String player, int score) {
        scores.put(player, score);
    }

    public String getWhoItIs () {
        return whoItIs;
    }

    public void setWhoItIs (String whoItIsIn) {
        whoItIs = whoItIsIn;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining (int timeRemainingIn) {
        timeRemaining = timeRemainingIn;
    }
}
