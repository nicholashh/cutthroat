package ach7nbh2game.server.map.components;

import ach7nbh2game.main.Constants;
import ach7nbh2game.network.packets.PlayerInfo;
import ach7nbh2game.main.Constants.*;
import ach7nbh2game.network.packets.ClientAction;
import ach7nbh2game.network.packets.GameState;
import ach7nbh2game.server.Callback;
import ach7nbh2game.server.Game;
import ach7nbh2game.server.map.GameMap;
import ach7nbh2game.util.id.ClientID;
import ach7nbh2game.util.id.Coordinate;
import ach7nbh2game.util.Logger;

import ach7nbh2game.util.Utility;

import java.io.IOException;
import java.util.Random;

public abstract class Client extends AMapComponent {

    private final ClientID id;
    private final PlayerInfo info;
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
        return info.getHealth();
    }

    public void incHealth(int healthDiff) {
        info.setHealth(info.getHealth()+healthDiff);
    }

    public void decHealth(int healthDiff, Bullet bullet) {
        info.setHealth(info.getHealth()-healthDiff);
        if (info.getHealth() <= 0) {
            removeFromMap();
            bullet.getOwner().incScore(1);

            (new Thread() { public void run() {
                try {Thread.sleep(5000);} catch (InterruptedException e) {}

                info.setHealth(Constants.clientHealth);
                Coordinate newloc = getMap().getRandomLocationWithA(Ground.class);
                placeOnMap(getMap(), newloc);
            }}).start();
        }
    }

    public PlayerInfo getInfo() {
        return  info;
    }

    public int getScore() {
        return info.getScore();
    }

    public void incScore(int scoreDiff) {
        info.setScore(info.getScore()+scoreDiff);
    }

    public void decScore(int scoreDiff) {
        info.setScore(info.getScore()-scoreDiff);
    }

    public int getAmmo() {
        return info.getAmmo();
    }

    public void incAmmo(int ammoDiff) {
        info.setAmmo(info.getAmmo()+ammoDiff);
    }

    public void decAmmo(int ammoDiff) {
        info.setAmmo(info.getAmmo()-ammoDiff);
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

                    if (info.getAmmo() > 0) {
                        Bullet newBullet = new Bullet(direction, this, info.getBulletDmg());
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
                    wall.decHealth(info.getPickaxeDmg());
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
