package ach7nbh2game.server.map.components;

import ach7nbh2game.main.Constants;
import ach7nbh2game.main.Constants.Direction;
import ach7nbh2game.main.Constants.ServerToClientSound;
import ach7nbh2game.network.packets.ClientAction;
import ach7nbh2game.network.packets.GameState;
import ach7nbh2game.network.packets.PlayerInfo;
import ach7nbh2game.server.Callback;
import ach7nbh2game.server.Game;
import ach7nbh2game.network.packets.PlayerState;
import ach7nbh2game.server.map.GameMap;
import ach7nbh2game.util.Logger;
import ach7nbh2game.util.Utility;
import ach7nbh2game.util.id.ClientID;
import ach7nbh2game.util.id.Coordinate;

import java.util.Random;

public abstract class Client extends AMapComponent {

    private final ClientID id;
    private final PlayerInfo info;
    private PlayerState state = new PlayerState();
    private boolean dead = false;

    private Random rand = new Random();

    // client objects know how to communicate with the clients they represent
    public abstract void enterGame ();
    public abstract void announceLobbies ();
    public abstract void sendGameState (GameState state);
    public abstract void sendEndGame (PlayerInfo winner);

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

    public PlayerInfo getInfo() {
        return info;
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
            dead = true;
            removeFromMap();
            bullet.getOwner().incScore(1);
            getGame().addSound(ServerToClientSound.PLAYER_DIES);

            (new Thread() { public void run() {
                try {Thread.sleep(5000);} catch (InterruptedException e) {}

                state.respawn();
                Coordinate newloc = getMap().getRandomLocationWithA(Ground.class);
                placeOnMap(getMap(), newloc);
                dead = false;
            }}).start();
        }
    }

    public void decHealth(int healthDiff, Client killer) {
        state.setHealth(state.getHealth()-healthDiff);
        if (state.getHealth() <= 0) {
            dead = true;
            removeFromMap();
            killer.incScore(1);
            getGame().addSound(ServerToClientSound.PLAYER_DIES);

            (new Thread() { public void run() {
                try {Thread.sleep(5000);} catch (InterruptedException e) {}

                state.respawn();
                Coordinate newloc = getMap().getRandomLocationWithA(Ground.class);
                placeOnMap(getMap(), newloc);
                dead = false;
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
        if (state.getScore() >= getGame().getKillsToWin()) {
            getGame().iJustWon(this);
        }
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
        state.setAmmo(state.getAmmo() - ammoDiff);
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
    public void setGame (Game newGame) {

        Logger.Singleton.log(this, 0, "setGame()");
        Logger.Singleton.log(this, 1, "newGame = " + newGame);

        if (mapIsNull()) {
            boolean needToUpdate = true;
            // if this client is already in a game
            if (!gameIsNull()) {
                final Game curGame = getGame();
                if (curGame.getID() != newGame.getID()) {
                    // remove this client from that game
                    getGame().removePlayer(this);
                } else {
                    needToUpdate = false;
                }
            }
            if (needToUpdate) {
                // record which game this client is now in
                super.setGame(newGame);
                // alert that game that this client wants to join
                newGame.addPlayer(this);
            } else {
                announceLobbies();
            }
        } else {
            // TODO: not allowed
        }

    }

    public void perform (ClientAction action) {
        if (!dead) {

            Logger.Singleton.log(this, 0, "perform(action = " + action + ")");
            Logger.Singleton.log(this, 1, "before: " + getMap().getPerspectiveFrom(getX(), getY(), 3, 3));

            Direction direction = action.direction;
            Coordinate nextLocation = nextLocation(direction);
            int newX = nextLocation.x;
            int newY = nextLocation.y;

            Game game = getGame();
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
                            newBullet.setGame(game);
                            newBullet.start();
                            decAmmo(1);
                            game.addSound(ServerToClientSound.GUN_FIRE);
                        } else {
                            game.addSound(ServerToClientSound.GUN_WHIFF);
                        }

                        break;
                    }

                    case DIG:
                        game.addSound(ServerToClientSound.PICKAXE_WHIFF);

                }

            } else if (thing instanceof CavernWall) {
                CavernWall cwall = (CavernWall) thing;
                switch (action.action) {
                    case MOVE:
                        if (cwall.getVisible()) {
                            map.swap(this, cwall);
                            cwall.removeFromMap();
                            switch (cwall.getItem()) {
                                case GUN2:
                                    state.upgradeGun(Constants.gun2);
                                    break;
                                case PICK3:
                                    state.upgradePickaxe(Constants.pickaxe3);
                                    break;
                                case BULLET1:
                                    incAmmo(3);
                            }
                            game.addSound(ServerToClientSound.PICKUP_ITEM);
                        }
                        break;
                    case DIG:
                        if (!(cwall.getVisible())) {
                            cwall.decHealth(this, state.getPickaxeDmg());
                            game.addSound(ServerToClientSound.PICKAXE_HIT_WALL);
                        }
                        break;
                }

            } else if (thing instanceof Wall) {
                switch (action.action) {
                    case DIG:
                        Logger.Singleton.log(this, 0, "digging " + direction);

                        Wall wall = (Wall) thing;
                        wall.decHealth(this, state.getPickaxeDmg());
                        game.addSound(ServerToClientSound.PICKAXE_HIT_WALL);
                }
            } else if (thing instanceof Client) {
                switch(action.action) {
                    case DIG:
                        Client other = (Client) thing;
                        other.decHealth(state.getPickaxeDmg(), this);
                        game.addSound(ServerToClientSound.PICKAXE_HIT_PLAYER);
                }
            }
        }
    }

    public void sendGameState () {

        //Logger.Singleton.log(this, 0, "sendGameState:");

        if (!mapIsNull()) {
            GameState gameState = getGame().fillGameStateInfo();
            gameState.setPlayerState(state);
            gameState.setFrame(Utility.componentToInteger(getMap().getPerspectiveFrom(getX(),getY())));
            sendGameState(gameState);
        } else {
            // TODO: not allowed
        }

    }

    public int getMapChar () {
        return info.getIcon();
    }

    public void endGame (Client winner) {
        state = new PlayerState();
        sendEndGame(winner.getInfo());
    }

}
