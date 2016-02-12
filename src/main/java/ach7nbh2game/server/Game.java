package ach7nbh2game.server;

import ach7nbh2game.main.Constants;
import ach7nbh2game.server.map.AGameActor;
import ach7nbh2game.server.map.GameMap;
import ach7nbh2game.server.map.components.Client;
import ach7nbh2game.server.map.components.Ground;
import ach7nbh2game.util.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Game extends AGameActor {

    private final GameID id;
    private String name;

    private boolean gameHasStarted = false;

    private int tick = 0;
    private Thread timerThread = null;
    private ConcurrentMap<CallbackRegistration,Object>
            callbackRegistrations = new ConcurrentHashMap<>();

    private Map<ClientID,Client> players = new HashMap<>();

    public Game (GameID idIn, String nameIn) {

        id = idIn;
        name = nameIn;

        startGameTimer();

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

        updateAllPlayers();

    }

    public void updateAllPlayers () {

        Logger.Singleton.log(this, 0, "updateAllPlayers:");
        Logger.Singleton.log(this, 1, "gameHasStarted = " + gameHasStarted);

        if (gameHasStarted) {
            // send the state of the game
            for (Client client : players.values()) {
                client.sendGameState();
            }
        } else {
            // send the state of the lobby
            for (Client client : players.values()) {
                client.announceLobbies();
            }
        }

    }

    // SERVER CLOCK CODE (tick... tick... tick...)

    public void requestCallback (CallbackRequest request) {

        Logger.Singleton.log(this, 0, "requestCallback:");
        Logger.Singleton.log(this, 1, "request = " + request);

        callbackRegistrations.put(new CallbackRegistration(tick, request), new Object());

    }

    private void incTick () {

        Logger.Singleton.log(this, 0, "tick! " + tick + "->" + (tick + 1));

        tick += 1;

        for (CallbackRegistration registration : callbackRegistrations.keySet()) {
            if ((tick - registration.startTime) % registration.frequency == 0) {
                if (registration.run()) {
                    callbackRegistrations.remove(registration);
                }
            }
        }

    }

    private void startGameTimer () {

        Logger.Singleton.log(this, 0, "startGameTimer");

        if (timerThread == null) {

            Logger.Singleton.log(this, 1, "making thread...");

            timerThread = new Thread () { public void run () {

                try {

                    while (true) {

                        Thread.sleep(100);
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
