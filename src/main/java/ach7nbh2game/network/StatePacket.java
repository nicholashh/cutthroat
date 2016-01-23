package ach7nbh2game.network;

import java.util.ArrayList;

/**
 * Created by achuie on 1/23/16.
 */
public class StatePacket {
    public ArrayList<ArrayList<Integer>> frame;
    //TODO

    public StatePacket(ArrayList<ArrayList<Integer>> newframe) {
        frame = newframe;
    }
}
