package ach7nbh2game.network.adapters;

import ach7nbh2game.main.Constants.*;
import ach7nbh2game.network.NetServer;
import ach7nbh2game.network.StatePacket;
import ach7nbh2game.server.GameServer;

public class ServerToClient {

    private NetServer nserver;
    private GameServer gserver;

    public ServerToClient(NetServer newnserver, GameServer newgserver) {
        nserver = newnserver;
        gserver = newgserver;
    }

    /**
     * notify a client that the game (for the lobby they are in) has started
     */
    public void enterGame(int gameIDIn) {
        //TODO
    }

    // TODO
    // sendGameState() or something like this?
    // so the server can send game state instead of just returning it

    public void newState(StatePacket pkt) {
        nserver.sendState(pkt);
    }

    public void move(Directions direction) {
        gserver.move(direction);
    }
}
