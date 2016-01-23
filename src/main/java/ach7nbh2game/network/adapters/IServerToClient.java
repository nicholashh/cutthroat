package ach7nbh2game.network.adapters;

import ach7nbh2game.main.Constants.*;
import ach7nbh2game.network.StatePacket;

public interface IServerToClient {

    /**
     * notify a client that the game (for the lobby they are in) has started
     */
    void enterGame (int gameIDIn);

    // TODO
    // sendGameState() or something like this?
    // so the server can send game state instead of just returning it

    void newState (StatePacket pkt);

    void move (Directions direction);
}
