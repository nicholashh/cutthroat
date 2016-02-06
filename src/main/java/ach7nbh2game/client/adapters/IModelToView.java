package ach7nbh2game.client.adapters;

import ach7nbh2game.server.GameState;

import java.util.ArrayList;

public interface IModelToView {
    String askForUsername();
    String askForServerIP();
    String askForThing(String label, String value);
    void showMap(ArrayList<ArrayList<Integer>> frame);
    void showScores(GameState state);
}
