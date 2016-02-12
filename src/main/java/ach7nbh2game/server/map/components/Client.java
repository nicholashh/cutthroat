package ach7nbh2game.server.map.components;

import ach7nbh2game.client.PlayerInfo;
import ach7nbh2game.main.Constants.*;
import ach7nbh2game.network.packets.ClientAction;
import ach7nbh2game.network.packets.GameState;
import ach7nbh2game.server.Game;
import ach7nbh2game.server.map.GameMap;
import ach7nbh2game.util.ClientID;
import ach7nbh2game.util.Coordinate;
import ach7nbh2game.util.Logger;

public abstract class Client extends AMapComponent {

    private final ClientID id;
    private final PlayerInfo info;

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

    @Override
    public void setGame (Game game) {

        Logger.Singleton.log(this, 0, "setGame:");
        Logger.Singleton.log(this, 1, "game = " + game);

        if (mapIsNull()) {
            // if this client is already in a game
            if (!gameIsNull()) {
                // remove this client from that game
                getGame().removePlayer(this);
            }
            // record which game this client is now in
            super.setGame(game);
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

        int x = getX();
        int y = getY();

        Direction direction = action.direction;
        Coordinate nextLocation = nextLocation(direction);
        int newX = nextLocation.x;
        int newY = nextLocation.y;

        GameMap map = getMap();
        IMapComponent thing = map.get(newY, newX);

        if (thing instanceof Ground) {

            switch (action.action) {

                case MOVE: {

                    Logger.Singleton.log(this, 0,
                            "moving from (" + x + "," + y + ") " +
                                    "to (" + newX + "," + newY + ")");

                    map.swap(this, thing);

                    break;

                }

                case SHOOT: {

                    Bullet newBullet = new Bullet(direction,this);
                    newBullet.placeOnMap(map, newX, newY);
                    newBullet.setGame(getGame());
                    newBullet.start();

                    break;

                }

            }

        } else {
            somethingMoved = false;
            somethingChanged = false;
        }

        if (somethingMoved) {
            getGame().updateAllPlayers();
        } else if (somethingChanged) {
            sendGameState();
        }

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
