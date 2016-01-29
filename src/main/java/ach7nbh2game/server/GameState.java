package ach7nbh2game.server;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GameState {
    private ArrayList<ArrayList<Integer>> frame;
    //TODO

    public GameState() {
    }

    public void setFrame(ArrayList<ArrayList<Integer>> newframe) {
        frame = newframe;
    }

    public ArrayList<ArrayList<Integer>> getFrame() {
        return frame;
    }
}
