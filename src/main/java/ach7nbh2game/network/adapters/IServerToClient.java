package ach7nbh2game.network.adapters;

public interface IServerToClient {

    /**
     * notify a client that the game (for the lobby they are in) has started
     */
    void enterGame (int gameIDIn);

    // TODO
    // sendGameState() or something like this?
    // so the server can send game state instead of just returning it

}
