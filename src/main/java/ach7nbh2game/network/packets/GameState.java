package ach7nbh2game.network.packets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameState {

    private ArrayList<ArrayList<Integer>> frame;
    private Map<String,PlayerObservableState> otherPlayerStates = new HashMap<>();
    private PlayerState playerState;

    public void setFrame (ArrayList<ArrayList<Integer>> newFrame) {
        frame = newFrame;
    }

    public ArrayList<ArrayList<Integer>> getFrame () {
        return frame;
    }

    public void setOtherPlayerState (String player, PlayerObservableState state) {
        otherPlayerStates.put(player, state);
    }

    public Map<String,PlayerObservableState> getOtherPlayerStates () {
        return otherPlayerStates;
    }

    public void setPlayerState (PlayerState playerState) {
        this.playerState = playerState;
    }

    public PlayerState getPlayerState () {
        return playerState;
    }

}
