package ach7nbh2game.server.map.components;

import ach7nbh2game.main.Constants;
import ach7nbh2game.network.packets.PlayerInfo;
import ach7nbh2game.main.Constants.*;
import ach7nbh2game.network.packets.ClientAction;
import ach7nbh2game.network.packets.GameState;
import ach7nbh2game.server.Callback;
import ach7nbh2game.server.Game;
import ach7nbh2game.server.PlayerState;
import ach7nbh2game.server.map.GameMap;
import ach7nbh2game.util.id.ClientID;
import ach7nbh2game.util.id.Coordinate;
import ach7nbh2game.util.Logger;

import ach7nbh2game.util.Utility;

import java.util.Random;

public abstract class Client extends AMapComponent {

    private final ClientID id;
    private final PlayerInfo info;
    private PlayerState state = new PlayerState();

    private Random rand = new Random();

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

    public int getHealth() {
        return state.getHealth();
    }

    public void incHealth(int healthDiff) {
        state.setHealth(state.getHealth()+healthDiff);
    }

    public void decHealth(int healthDiff, Bullet bullet) {
        state.setHealth(state.getHealth()-healthDiff);
        if (state.getHealth() <= 0) {
            removeFromMap();
            bullet.getOwner().incScore(1);

            (new Thread() { public void run() {
                try {Thread.sleep(5000);} catch (InterruptedException e) {}

                state.setHealth(Constants.clientHealth);
                Coordinate newloc = getMap().getRandomLocationWithA(Ground.class);
                placeOnMap(getMap(), newloc);
            }}).start();
        }
    }

    public PlayerState getState() {
        return state;
    }

    public int getScore() {
        return state.getScore();
    }

    public void incScore(int scoreDiff) {
        state.setScore(state.getScore()+scoreDiff);
    }

    public void decScore(int scoreDiff) {
        state.setScore(state.getScore()-scoreDiff);
    }

    public int getAmmo() {
        return state.getAmmo();
    }

    public void incAmmo(int ammoDiff) {
        state.setAmmo(state.getAmmo()+ammoDiff);
    }

    public void decAmmo(int ammoDiff) {
        state.setAmmo(state.getAmmo()-ammoDiff);
    }

    // essentially a setter for the callback
    public void queueAction (Callback newCallback) {

        // cancel the old callback
        if (!callbackIsNull()) {
            getCallback().cancel();
        }

        // register the new callback
        setCallback(getGame().requestCallback(newCallback));

    }

    @Override
    public void setGame (Game game) {

        Logger.Singleton.log(this, 0, "setGame()");
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

        Logger.Singleton.log(this, 0, "perform(action = " + action + ")");
        Logger.Singleton.log(this, 1, "before: " + getMap().getPerspectiveFrom(getX(), getY(), 3, 3));

        Direction direction = action.direction;
        Coordinate nextLocation = nextLocation(direction);
        int newX = nextLocation.x;
        int newY = nextLocation.y;

        GameMap map = getMap();
        IMapComponent thing = map.get(newY, newX);

        if (thing instanceof Ground) {

            switch (action.action) {

                case MOVE: {

                    Logger.Singleton.log(this, 1, "moving " + direction);

                    map.swap(this, thing);

                    break;

                }

                case SHOOT: {

                    Logger.Singleton.log(this, 0, "firing bullet " + direction);

                    if (state.getAmmo() > 0) {
                        Bullet newBullet = new Bullet(direction, this, state.getBulletDmg());
                        newBullet.placeOnMap(map, newX, newY);
                        newBullet.setGame(getGame());
                        newBullet.start();
                        decAmmo(1);
                    }

                    break;

                }

            }

        } else if (thing instanceof Wall) {
            switch(action.action) {
                case DIG:
                    Logger.Singleton.log(this, 0, "digging "+direction);

                    Wall wall = (Wall) thing;
                    wall.decHealth(this, state.getPickaxeDmg());
            }
        }

    }

    public void sendGameState () {

        //Logger.Singleton.log(this, 0, "sendGameState:");

        if (!mapIsNull()) {
            GameState gameState = new GameState();
            // TODO put in constructor instead of setter?
            gameState.setFrame(Utility.componentToInteger(
                    getMap().getPerspectiveFrom(getX(),getY())));
            sendGameState(gameState);
        } else {
            // TODO: not allowed
        }

    }

    public int getMapChar () {
        return info.getIcon();
    }

}
