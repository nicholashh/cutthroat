package ach7nbh2game.client.adapters;

import ach7nbh2game.network.packets.GameState;

public interface IModelToView {
    String askForUsername();
    String askForServerIP();
    String askForThing(String label, String value);
	void updateThing(String newLabel);
    void updateState(GameState state);
}
