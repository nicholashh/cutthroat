package ach7nbh2game.server;

import ach7nbh2game.main.Constants;
import ach7nbh2game.main.Constants.Item;
import ach7nbh2game.main.Constants.ServerToClientSound;
import ach7nbh2game.network.packets.GameState;
import ach7nbh2game.network.packets.PlayerObservableState;
import ach7nbh2game.server.map.AGameActor;
import ach7nbh2game.server.map.GameMap;
import ach7nbh2game.server.map.components.Client;
import ach7nbh2game.server.map.components.Ground;
import ach7nbh2game.server.map.components.Wall;
import ach7nbh2game.util.Logger;
import ach7nbh2game.util.id.ClientID;
import ach7nbh2game.util.id.Coordinate;
import ach7nbh2game.util.id.GameID;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public abstract class Game extends AGameActor {

    private final GameID id;

    private boolean gameHasStarted = false;

    private int tick = 0;
    private Thread timerThread = null;
    private Deque<CallbackRegistration> callbackRegistrations = new ConcurrentLinkedDeque<>();

    private Map<ClientID,Client> players = new HashMap<>();

    private ArrayList<ServerToClientSound> sounds = new ArrayList<>();

    private ServerModel host;

    public abstract void announceLobbies () ;

    public Game (GameID idIn, String name, ServerModel server) {
        super(name);
        id = idIn;
        host = server;
    }

    public GameID getID () {
        return id;
    }

    public void addPlayer (Client client) {

        Logger.Singleton.log(this, 0, "addPlayer:");
        Logger.Singleton.log(this, 1, "client = " + client);

        if (!gameHasStarted) {
            players.put(client.getID(), client);
            updateAllPlayers();
        } else {
            // TODO: can't add player if game has started
        }

    }

    public void removePlayer (Client client) {

        Logger.Singleton.log(this, 0, "removePlayer:");
        Logger.Singleton.log(this, 1, "client = " + client);

        if (!gameHasStarted) {
            players.remove(client.getID());
            updateAllPlayers();
        } else {
            // TODO: special action required if game has started
        }

    }

    public Collection<Client> getPlayers () {
        return players.values();
    }

    public void start() {

        Logger.Singleton.log(this, 0, "start:");
        Logger.Singleton.log(this, 1, "serverTicksPerSecond = " + Constants.serverTicksPerSecond);
        Logger.Singleton.log(this, 1, "clientUpdatesPerSecond = " + Constants.clientUpdatesPerSecond);
        Logger.Singleton.log(this, 1, "clientUpdatesFrequency = " + Constants.clientUpdatesFrequency);

        gameHasStarted = true;

        GameMap map = new GameMap(Constants.mapHeight, Constants.mapWidth);

        for (Client client : players.values()) {
            Coordinate coord = map.getRandomLocationWithA(Ground.class);
            client.placeOnMap(map, coord.x, coord.y);
        }

        setMap(map);

        for (Client client : players.values()) {
            client.enterGame();
        }

        startGameTimer();

        // 30 * 60 = 1 minute
        requestCallback(new Callback(30 * 60, -1, () -> {
            for (int i = 0; i < numCavernSpawns; i++)
                makeRandomCavern();
            numCavernSpawns++;
        }));

    }

    private int numCavernSpawns = 1;

    private void updateAllPlayers () {

        //Logger.Singleton.log(this, 0, "updateAllPlayers:");
        //Logger.Singleton.log(this, 1, "gameHasStarted = " + gameHasStarted);

        if (gameHasStarted) {
            // send the state of the game
            for (Client client : players.values()) {
                client.sendGameState();
            }
        } else {
            // update everyone on the state of the lobby
            announceLobbies();
        }

    }

    public int getKillsToWin() {
        return Constants.killsToWin;
    }

    public void iJustWon (Client winner) {
        gameHasStarted = false;
        for (Client client : players.values())
            client.endGame(winner);
        for (Client client : players.values())
            client.gameHasEnded();
        host.endGame(this);
    }

    public GameState fillGameStateInfo() {
        GameState state = new GameState();
        state.setSounds(sounds);
        for (Client client : players.values()) {
            PlayerObservableState obsState = new PlayerObservableState();
            obsState.setHealth(client.getHealth());
            obsState.setScore(client.getScore());
            state.setOtherPlayerState(client.getName(), obsState);
        }
        return state;
    }

    // SERVER CLOCK CODE (tick... tick... tick...)

    public CallbackRegistration requestCallback (Callback request) {

        //Logger.Singleton.log(this, 0, "requestCallback:");
        //Logger.Singleton.log(this, 1, "request = " + request);

        CallbackRegistration registration = new CallbackRegistration(tick - 1, request);
        callbackRegistrations.add(registration);
        return registration;

    }

    private long timeSpentUpdatingState = 0;
    private long timeSpentSendingStateToPlayers = 0;
    private long timeSpentSleeping = 0;

    private void incTick () {

        tick += 1;

        long starTime, endTime;
        starTime = System.currentTimeMillis();

        boolean first = true;
        for (CallbackRegistration registration : callbackRegistrations) {
            if ((tick - registration.startTime) % registration.frequency == 0) {

                if (first) {
                    first = false;
                    Logger.Singleton.log(this, 0, "tick! " + (tick - 1) + "->" + tick);
                    Logger.Singleton.log(this, 1, "callbackRegistrations = " + callbackRegistrations);
                    Logger.Singleton.log(this, 1, "timeSpentUpdatingState = " + timeSpentUpdatingState);
                    Logger.Singleton.log(this, 1, "timeSpentSendingStateToPlayers = " + timeSpentSendingStateToPlayers);
                    Logger.Singleton.log(this, 1, "timeSpentSleeping = " + timeSpentSleeping);
                }

                // run the callback
                if (registration.run()) {
                    Logger.Singleton.log(this, 0, "removing " + registration);
                    callbackRegistrations.remove(registration);
                    Logger.Singleton.log(this, 1, "callbackRegistrations = " + callbackRegistrations);
                }

            }
        }

        endTime = System.currentTimeMillis();
        long timeSpentUpdatingStateThisTime = endTime - starTime;
        timeSpentUpdatingState += timeSpentUpdatingStateThisTime;
        starTime = endTime;

        if (tick % Constants.clientUpdatesFrequency == 0) {
            updateAllPlayers();
            resetTempState();
        }

        endTime = System.currentTimeMillis();
        long timeSpentSendingStateToPlayersThisTime = endTime - starTime;
        timeSpentSendingStateToPlayers += timeSpentSendingStateToPlayersThisTime;

        Logger.Singleton.log(this, 0, "ending tick " + tick + "...");
        Logger.Singleton.log(this, 1, "timeSpentUpdatingStateThisTime = " + timeSpentUpdatingStateThisTime);
        Logger.Singleton.log(this, 1, "timeSpentSendingStateToPlayersThisTime = " + timeSpentSendingStateToPlayersThisTime);

    }

    public void addSound (ServerToClientSound sound) {
        sounds.add(sound);
    }

    private void resetTempState () {
        sounds.clear();
    }

    private void startGameTimer () {

        Logger.Singleton.log(this, 0, "startGameTimer");

        if (timerThread == null) {

            Logger.Singleton.log(this, 1, "making thread...");

            timerThread = new Thread () { public void run () {

                try {

                    while (true) {

                        long starTime = System.currentTimeMillis();
                        incTick();
                        long endTime = System.currentTimeMillis();

                        long timeToSleep = 1000 - (endTime - starTime);
                        timeSpentSleeping += timeToSleep;

                        Logger.Singleton.log(Game.this, 0, "timer thread: sleeping for " + timeToSleep);

                        if (timeToSleep > 0) {
                            Thread.sleep(timeToSleep / Constants.serverTicksPerSecond);
                        }

                    }

                } catch (Exception e) {
                    // TODO
                }

            }};

            Logger.Singleton.log(this, 1, "starting thread...");

            timerThread.start();

        } else {
            // TODO
        }

    }

    private void makeRandomCavern () {

        Logger.Singleton.log(this, 0, "makeRandomCavern()");

        GameMap map = getMap();
        int numTries = 0;
        Coordinate place;
        while (true) {
            place = map.getRandomLocationWithA(Ground.class);
            boolean allGround = true;
            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    if (!(map.get(place.y + i, place.x + j) instanceof Ground)) {
                        allGround = false;
                    }
                }
            }
            if (allGround) {
                break;
            } else if (++numTries > 100) {
                return;
            }
        }
        Random rand = new Random();
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                double randVal = rand.nextDouble();
                List<Item> items = new ArrayList<>();
                if (Math.abs(i) == 2 || Math.abs(j) == 2) {
                    // add nothing
                } else if (i == 0 && j == 0) {
                    if (randVal < 0.33) {
                        items.add(Item.GUN2);
                    } else if (randVal < 0.66) {
                        items.add(Item.PICK2);
                    } else {
                        items.add(Item.PICK3);
                    }
                } else {
                    if (randVal < 0.33) {
                        items.add(Item.BULLET1);
                    } else if (randVal < 0.66) {
                        items.add(Item.HEALTH);
                    } else {
                        items.add(Item.ROCKET);
                    }
                }
                Wall newWall = new Wall(items);
                newWall.placeOnMap(map, place.x + j, place.y + i);
            }
        }
    }

    @Override
    public String toString () {
        return "Game(" + super.toString() + ")";
    }

}
