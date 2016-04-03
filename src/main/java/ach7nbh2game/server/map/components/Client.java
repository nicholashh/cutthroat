package ach7nbh2game.server.map.components;

import ach7nbh2game.main.Constants;
import ach7nbh2game.main.Constants.Direction;
import ach7nbh2game.main.Constants.Item;
import ach7nbh2game.main.Constants.ServerToClientSound;
import ach7nbh2game.network.packets.ClientAction;
import ach7nbh2game.network.packets.GameState;
import ach7nbh2game.network.packets.PlayerInfo;
import ach7nbh2game.network.packets.PlayerState;
import ach7nbh2game.server.Callback;
import ach7nbh2game.server.Game;
import ach7nbh2game.server.map.GameMap;
import ach7nbh2game.util.Logger;
import ach7nbh2game.util.Utility;
import ach7nbh2game.util.id.ClientID;
import ach7nbh2game.util.id.Coordinate;

import java.util.ArrayList;
import java.util.List;

public abstract class Client extends AMapComponent {

    private final ClientID id;
    private final PlayerInfo info;
    private PlayerState state = new PlayerState();

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

    public PlayerInfo getInfo () {
        return info;
    }

    public int getHealth () {
        return state.getHealth();
    }

    public void applyDamage (int damage, Client killer) {

        int health = state.getHealth() - damage;
        if (health < 0) {
            state.setHealth(0);
        } else if (health > Constants.clientHealth) {
            state.setHealth(Constants.clientHealth);
        } else {
            state.setHealth(health);
        }

        if (state.getHealth() <= 0) {

            List<Item> items = new ArrayList<>();
            if (state.getGunDmg() >= Constants.gun2)
                items.add(Item.GUN2);
            if (state.getPickaxeDmg() >= Constants.pickaxe3)
                items.add(Item.PICK3);
            else if (state.getPickaxeDmg() >= Constants.pickaxe2)
                items.add(Item.PICK2);

            if (items.isEmpty()) removeFromMap();
            else removeFromMap(new Wall(items));

            killer.incScore(1 + this.getScore() * Constants.bountyPercent);
            getGame().addSound(ServerToClientSound.PLAYER_DIES);

            (new Thread() { public void run() {

                try {Thread.sleep(5000);} catch (InterruptedException e) { /* TODO */ }

                state.respawn();
                Coordinate newloc = getMap().getRandomLocationWithA(Ground.class);
                placeOnMap(getMap(), newloc);
                getGame().addSound(ServerToClientSound.PLAYER_SPAWNS);

            }}).start();

        }

    }

    public PlayerState getState() {
        return state;
    }

    public double getScore() {
        return state.getScore();
    }

    public void incScore(double scoreDiff) {
        state.setScore(state.getScore()+scoreDiff);
        if (state.getScore() >= getGame().getKillsToWin()) {
            getGame().iJustWon(this);
        }
    }

    //public void decScore(int scoreDiff) {
    //    state.setScore(state.getScore()-scoreDiff);
    //}

    //public int getAmmo() {
    //    return state.getAmmo();
    //}

    public void incAmmo(int ammoDiff) {
        state.setAmmo(state.getAmmo()+ammoDiff);
    }

    public void decAmmo(int ammoDiff) {
        state.setAmmo(state.getAmmo() - ammoDiff);
    }

    public void incRocketAmmo(int ammoDiff) {
        state.setRocketAmmo(state.getRocketAmmo() + ammoDiff);
    }

    public void decRocketAmmo(int ammoDiff) {
        state.setRocketAmmo(state.getRocketAmmo() - ammoDiff);
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
        if (!isDead()) {

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

                    case SHOOT_GUN: {

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

                    case SHOOT_ROCKET: {

                        Logger.Singleton.log(this, 0, "firing rocket " + direction);

                        if (state.hasRocket() && state.getRocketAmmo() > 0) {
                            Rocket newRocket = new Rocket(direction, this, state.getRocketDmg());
                            newRocket.placeOnMap(map, newX, newY);
                            newRocket.setGame(game);
                            newRocket.start();
                            decRocketAmmo(1);
                            game.addSound(ServerToClientSound.ROCKET_LAUNCH);
                        } else {
                            game.addSound(ServerToClientSound.GUN_WHIFF);
                        }

                        break;
                    }

                    case DIG: {
                        game.addSound(ServerToClientSound.PICKAXE_WHIFF);
                        break;
                    }

                }

            } else if (thing instanceof CavernWall) {
                CavernWall cwall = (CavernWall) thing;
                switch (action.action) {
                    case MOVE:
                        if (cwall.getVisible()) {
                            map.swap(this, cwall);
                            cwall.removeFromMap();
                            upgradeItem(cwall.getItem());
                            game.addSound(ServerToClientSound.PICKUP_ITEM);
                        }
                        break;
                    case DIG:
                        if (!(cwall.getVisible())) {
                            cwall.applyDamage(state.getPickaxeDmg(), this);
                            game.addSound(ServerToClientSound.PICKAXE_HIT_WALL);
                        }
                        break;
                }

            } else if (thing instanceof Wall) {
                Wall wall = (Wall) thing;
                switch (action.action) {
                    case MOVE:
                        if (wall.isDead()) {
                            map.swap(this, wall);
                            wall.removeFromMap();
                            for (Item item : wall.getItems())
                                switch (item) {
                                    case BULLET1:
                                        incAmmo(Constants.bulletBatchSize);
                                        break;
                                    case ROCKET:
                                        incRocketAmmo(Constants.rocketBatchSize);
                                        break;
                                    case HEALTH:
                                        applyDamage(-1 * Constants.healthPack, this);
                                        break;
                                    default:
                                        upgradeItem(item);
                                        break;
                                }
                            game.addSound(ServerToClientSound.PICKUP_ITEM);
                        }
                        break;
                    case DIG:
                        Logger.Singleton.log(this, 0, "digging " + direction);

                        if (!(wall.isDead())) {
                            wall.applyDamage(state.getPickaxeDmg(), this);
                            game.addSound(ServerToClientSound.PICKAXE_HIT_WALL);
                        }
                        break;
                }
            } else if (thing instanceof Client) {
                switch(action.action) {
                    case DIG:
                        Client other = (Client) thing;
                        other.applyDamage(state.getPickaxeDmg(), this);
                        game.addSound(ServerToClientSound.PICKAXE_HIT_PLAYER);
                }
            } else if (thing instanceof Projectile) {
                switch(action.action) {
                    case DIG: ((Projectile)thing).interactionWithKillable(thing);
                }
            }
        }
    }

    private void upgradeItem(Item item) {
        switch (item) {
            case GUN2:
                state.upgradeGun(Constants.gun2);
                break;
            case PICK2:
                state.upgradePickaxe(Constants.pickaxe2);
                break;
            case PICK3:
                state.upgradePickaxe(Constants.pickaxe3);
                break;
            case BULLET1:
                incAmmo(Constants.bulletBatchSize);
                break;
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
        sendEndGame(winner.getInfo());
    }

    public void gameHasEnded () {
        state = new PlayerState();
        setMap(null);
    }
    public boolean canDie () {return true;}

}
