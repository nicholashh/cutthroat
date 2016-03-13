package ach7nbh2game.network.packets;

import ach7nbh2game.main.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameState {

    private ArrayList<ArrayList<Integer>> frame;

    public void setFrame (ArrayList<ArrayList<Integer>> newFrame) {
        frame = newFrame;
    }

    public ArrayList<ArrayList<Integer>> getFrame () {
        return frame;
    }

    private Map<String,PlayerObservableState> otherPlayerStates = new HashMap<>();

    public void setOtherPlayerState (String player, PlayerObservableState state) {
        otherPlayerStates.put(player, state);
    }

    public Map<String,PlayerObservableState> getOtherPlayerStates () {
        return otherPlayerStates;
    }

    private PlayerState playerState;

    public void setPlayerState (PlayerState playerState) {
        this.playerState = playerState;
    }

    public PlayerState getPlayerState () {
        return playerState;
    }

    private ArrayList<Constants.ServerToClientSound> sounds;

    public void setSounds (ArrayList<Constants.ServerToClientSound> sounds) {
        this.sounds = sounds;
    }

    public ArrayList<Constants.ServerToClientSound> getSounds () {
        return sounds;
    }

}
