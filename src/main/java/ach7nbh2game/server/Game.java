package ach7nbh2game.server;

import ach7nbh2game.main.Constants;
import ach7nbh2game.server.map.AGameActor;
import ach7nbh2game.server.map.GameMap;
import ach7nbh2game.server.map.components.Client;
import ach7nbh2game.server.map.components.Ground;
import ach7nbh2game.util.ClientID;
import ach7nbh2game.util.Coordinate;
import ach7nbh2game.util.GameID;
import ach7nbh2game.util.Logger;

import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

public abstract class Game extends AGameActor {

    private final GameID id;
    private String name;

    private boolean gameHasStarted = false;

    private int tick = 0;
    private Thread timerThread = null;
    private Deque<CallbackRegistration> callbackRegistrations = new ConcurrentLinkedDeque<>();

    private Map<ClientID,Client> players = new HashMap<>();

    public abstract void announceLobbies () ;

    public Game (GameID idIn, String nameIn) {

        id = idIn;
        name = nameIn;

    }

    public GameID getID () {
        return id;
    }

    public String getName () {
        return name;
    }

    public void setName (String nameIn) {
        name = nameIn;
    }

    public void addPlayer (Client client) {

        Logger.Singleton.log(this, 0, "addPlayer:");
        Logger.Singleton.log(this, 1, "client = " + client);

        players.put(client.getID(), client);
        updateAllPlayers();

    }

    public void removePlayer (Client client) {

        Logger.Singleton.log(this, 0, "removePlayer:");
        Logger.Singleton.log(this, 1, "client = " + client);

        players.remove(client.getID());
        updateAllPlayers();

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

    }

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

    // SERVER CLOCK CODE (tick... tick... tick...)

    public CallbackRegistration requestCallback (Callback request) {

        //Logger.Singleton.log(this, 0, "requestCallback:");
        //Logger.Singleton.log(this, 1, "request = " + request);

        CallbackRegistration registration = new CallbackRegistration(tick, request);
        callbackRegistrations.add(registration);
        return registration;

    }

    private void incTick () {

        tick += 1;

        boolean first = true;
        for (CallbackRegistration registration : callbackRegistrations) {
            if ((tick - registration.startTime) % registration.frequency == 0) {

                if (first) {
                    Logger.Singleton.log(this, 0, "tick! " + (tick - 1) + "->" + tick);
                    Logger.Singleton.log(this, 1, "callbackRegistrations = " + callbackRegistrations);
                    first = false;
                }

                if (registration.run()) {
                    Logger.Singleton.log(this, 0, "removing " + registration);
                    callbackRegistrations.remove(registration);
                    Logger.Singleton.log(this, 1, "callbackRegistrations = " + callbackRegistrations);
                }

            }
        }

        if (tick % Constants.clientUpdatesFrequency == 0) {
            updateAllPlayers();
        }

    }

    private void startGameTimer () {

        Logger.Singleton.log(this, 0, "startGameTimer");

        if (timerThread == null) {

            Logger.Singleton.log(this, 1, "making thread...");

            timerThread = new Thread () { public void run () {

                try {

                    while (true) {

                        Thread.sleep(1000 / Constants.serverTicksPerSecond);
                        incTick();

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

}
