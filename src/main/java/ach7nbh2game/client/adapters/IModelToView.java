package ach7nbh2game.client.adapters;

import ach7nbh2game.client.ClientView;
import ach7nbh2game.network.packets.GameState;
import ach7nbh2game.network.packets.PlayerInfo;

public interface IModelToView {
    String askForUsername();
    String askForServerIP();
    String askForServerIPFailed();
    String askForThing(String label, String value);
    String askForThing(String label, String value, ClientView.VerticalAlignment valign,
                       ClientView.HorizontalAlignment halign);
	void updateThing(String newLabel);
    void updateState(GameState state);
    void endGame(PlayerInfo client);
    void clearMenus();
}
