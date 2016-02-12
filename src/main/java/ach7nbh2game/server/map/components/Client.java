package ach7nbh2game.server.map.components;

import ach7nbh2game.client.PlayerInfo;
import ach7nbh2game.main.Constants.*;
import ach7nbh2game.network.packets.ClientAction;
import ach7nbh2game.network.packets.GameState;
import ach7nbh2game.server.Game;
import ach7nbh2game.server.map.GameMap;
import ach7nbh2game.util.ClientID;
import ach7nbh2game.util.Logger;

public abstract class Client extends AMapComponent {

    private final ClientID id;
    private final PlayerInfo info;

    private Game game;

    // client objects know how to communicate with the clients they represent
    public abstract void enterGame ();
    public abstract void announceLobbies ();
    public abstract void sendGameState (GameState state);

    public Client (ClientID idIn, PlayerInfo infoIn) {
        super("Client(" + infoIn.getUsername() + ")");
        id = idIn;
        info = infoIn;
    }

    public String getName () {
        return info.getUsername();
    }

    public ClientID getID () {
        return id;
    }

    public Game getGame () {
        return game;
    }

    public boolean gameIsNull () {
        return game == null;
    }

    public void setGame (Game gameIn) {

        Logger.Singleton.log(this, 0, "setGame:");
        Logger.Singleton.log(this, 1, "gameIn = " + gameIn);

        if (mapIsNull()) {
            // if this client is already in a game
            if (!gameIsNull()) {
                // remove this client from that game
                game.removePlayer(this);
            }
            // record which game this client is now in
            game = gameIn;
            // alert that game that this client wants to join
            game.addPlayer(this);
        } else {
            // TODO: not allowed
        }

    }

    public void perform (ClientAction action) {

        Logger.Singleton.log(this, 0, "perform:");
        Logger.Singleton.log(this, 1, "action = " + action);

        boolean somethingMoved = true;
        boolean somethingChanged = true;

        // BEGIN ISOLATED {

        int x = getX();
        int y = getY();
        int newX = x;
        int newY = y;

        switch (action.direction) {
            case UP:
                newX = x;
                newY = y - 1;
                break;
            case DOWN:
                newX = x;
                newY = y + 1;
                break;
            case LEFT:
                newX = x - 1;
                newY = y;
                break;
            case RIGHT:
                newX = x + 1;
                newY = y;
                break;
        }

        GameMap map = getMap();
        IMapComponent thing = map.get(newY, newX);

        if (thing instanceof Ground) {

            Logger.Singleton.log(this, 0,
                    "moving from (" + x + "," + y + ") " +
                            "to (" + newX + "," + newY + ")");

            map.swap(this, thing);

        //} else if (thing instanceof Player) {
        //    // TODO don't use instanceof
        //
        //    if (!isGun) {
        //
        //        String myPlayerName = players.get(playerID).getPlayerInfo().getUsername();
        //        String otherPlayerName = players.get(((Player) thing).getID()).getPlayerInfo().getUsername();
        //
        //        String whoItIs = gameState.getWhoItIs();
        //        if (myPlayerName.equals(whoItIs) ||
        //                otherPlayerName.equals(whoItIs)) {
        //
        //            int curScore = gameState.getScores().get(whoItIs);
        //            gameState.updateScore(whoItIs, curScore + 1);
        //            // TODO this should use a teamID instead of a string
        //
        //            restartGame();
        //
        //        }
        //
        //    } // TODO if is gun (should be fixed automatically with dispatchers)

        } else {
            somethingMoved = false;
            somethingChanged = false;
        }

        if (somethingMoved) {
            game.updateAllPlayers();
        } else if (somethingChanged) {
            sendGameState();
        }

        // } END ISOLATED

    }

    public void sendGameState () {

        Logger.Singleton.log(this, 0, "sendGameState:");

        if (!mapIsNull()) {
            GameState gameState = new GameState();
            // TODO put in constructor instead of setter?
            gameState.setFrame(getMap().getPerspectiveFrom(getX(), getY()));
            sendGameState(gameState);
        } else {
            // TODO: not allowed
        }

    }

    public int getMapChar () {
        return info.getIcon();
    }

}
