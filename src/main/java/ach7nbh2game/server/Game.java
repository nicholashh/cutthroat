package ach7nbh2game.server;

import ach7nbh2game.main.Constants;
import ach7nbh2game.server.map.GameMap;
import ach7nbh2game.server.map.components.Client;
import ach7nbh2game.server.map.components.Wall;
import ach7nbh2game.util.ClientID;
import ach7nbh2game.util.Coordinate;
import ach7nbh2game.util.GameID;
import ach7nbh2game.util.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// TODO: move map to AGameModifier
// AGameModifier (has a map) -> AMapComponent
// Game -> AGameModifier
public class Game {

    private final GameID id;
    private String name;

    private boolean gameHasStarted = false;

    // TODO
    private GameMap map;

    private Map<ClientID, Client> players = new HashMap<ClientID, Client>();

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

        gameHasStarted = true;

        map = new GameMap(Constants.mapHeight, Constants.mapWidth);

        for (Client client : players.values()) {
            Coordinate coord = map.getRandomLocationWithA(Wall.class);
            client.placeOnMap(map, coord.x, coord.y);
        }

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

    // TODO: game timer ticking

    //private int tick;
    //private void incTick () {
    //
    //    tick++;
    //
    //    // or a for through registrations
    //    if (tick % 5) {
    //        ...
    //    }
    //
    //}
    //async {
    //    while (true) {
    //        Thread.sleep();
    //        incTick();
    //    }
    //}

}
