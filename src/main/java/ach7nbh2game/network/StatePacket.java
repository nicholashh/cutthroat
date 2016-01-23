package ach7nbh2game.network;

import java.util.ArrayList;

public class StatePacket {
    public ArrayList<ArrayList<Integer>> frame;
    //TODO

    public StatePacket(ArrayList<ArrayList<Integer>> newframe) {
        frame = newframe;
    }
}
