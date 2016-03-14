package ach7nbh2game.server;

import ach7nbh2game.network.packets.PlayerInfo;
import ach7nbh2game.network.adapters.IServerToClient;
import ach7nbh2game.network.packets.ClientAction;
import ach7nbh2game.network.packets.GameState;
import ach7nbh2game.server.map.components.Client;
import ach7nbh2game.util.id.ClientID;
import ach7nbh2game.util.id.GameID;
import ach7nbh2game.util.Logger;

import java.util.*;

public class ServerModel {

    private IServerToClient network;

    private Map<ClientID, Client> clients = new HashMap<ClientID, Client>();
    private Map<GameID, Game> games = new HashMap<GameID, Game>();
    private Random rand = new Random();

    public ServerModel (IServerToClient networkIn) {

        System.out.println("making new ServerModel...");

        network = networkIn;

    }

    public void start () {

        System.out.println("starting the ServerModel!");

    }

    public void registerNewClient (ClientID clientID, PlayerInfo info) {

        Logger.Singleton.log(this, 0, "registerNewClient:");
        Logger.Singleton.log(this, 1, "clientID = " + clientID);
        Logger.Singleton.log(this, 1, "info = " + info);

        // if this is the first time this client has connected
        if (!clients.containsKey(clientID)) {
            // create a new client object for them
            clients.put(clientID, new Client(clientID, info) {
                // use this object's closure over the network object
                // to send new game state information to the client
                @Override public void enterGame() {
                    //Logger.Singleton.log(this, 0, "enterGame:");
                    network.enterGame(clientID.value);
                }
                @Override public void announceLobbies() {
                    //Logger.Singleton.log(this, 0, "announceLobbies:");
                    requestLobbies(clientID);
                }
                @Override public void sendGameState(GameState state) {
                    //Logger.Singleton.log(this, 0, "sendGameState:");
                    //Logger.Singleton.log(this, 1, "state = " + state);
                    network.updateGameState(clientID.value, state);
                }
                @Override public void sendEndGame(PlayerInfo winner) {
                    network.endGame(clientID.value, winner);
                }
            });
        } else {
            // TODO: this client has already connected
        }

    }

    public void createNewGameLobby (String name) {

        Logger.Singleton.log(this, 0, "createNewGameLobby:");
        Logger.Singleton.log(this, 1, "name = " + name);

        GameID id = new GameID(rand.nextInt());

        Game newLobby = new Game(id, name) {
            // use this object's closure over the network object
            // to send updated lobby information to every client
            @Override public void announceLobbies () { announceAllLobbies(); }
        };

        games.put(id, newLobby);

        newLobby.announceLobbies();

    }

    private void announceAllLobbies () {

        for (Client client : clients.values()) {
            client.announceLobbies();
        }

    }

    public void joinLobby (final ClientID clientID, GameID gameID) {

        Logger.Singleton.log(this, 0, "joinLobby:");
        Logger.Singleton.log(this, 1, "clientID = " + clientID);
        Logger.Singleton.log(this, 1, "gameID = " + gameID);

        // if this is a valid ClientID and a valid GameID
        if (clients.containsKey(clientID) && games.containsKey(gameID)) {
            // add this client to the requested game
            clients.get(clientID).setGame(games.get(gameID));
        } else {
            // TODO: game does not exist
        }

    }

    public void requestLobbies (ClientID id) {

        Logger.Singleton.log(this, 0, "requestLobbies:");
        Logger.Singleton.log(this, 1, "id = " + id);

        network.announceLobbies(id.value, makeGameToNameMap(),
                makePlayerToNameMap(), makeGameToPlayersMap());

    }

    private Map<Integer, String> makeGameToNameMap () {

        Map<Integer, String> toReturn = new HashMap<Integer, String>();

        for (Game game : games.values()) {
            toReturn.put(game.getID().value, game.getName());
        }

        return toReturn;

    }

    private Map<Integer, String> makePlayerToNameMap () {

        Map<Integer, String> toReturn = new HashMap<Integer, String>();

        for (Client client : clients.values()) {
            toReturn.put(client.getID().value, client.getName());
        }

        return toReturn;

    }

    private Map<Integer, Set<Integer>> makeGameToPlayersMap () {

        Map<Integer, Set<Integer>> toReturn = new HashMap<Integer, Set<Integer>>();

        for (Game game : games.values()) {

            Set<Integer> toAdd = new HashSet<Integer>();
            for (Client client : game.getPlayers()) {
                toAdd.add(client.getID().value);
            }

            toReturn.put(game.getID().value, toAdd);

        }

        return toReturn;

    }

    public void startGame (ClientID id) {

        Logger.Singleton.log(this, 0, "startGame:");
        Logger.Singleton.log(this, 1, "id = " + id);

        // if this client has already connected
        if (clients.containsKey(id)) {
            // start that client's game
            clients.get(id).getGame().start();
        } else {
            // TODO: this client has never connected before
        }

    }

    public void respondToClientAction (ClientID id, final ClientAction action) {

        //Logger.Singleton.log(this, 0, "respondToClientAction:");
        //Logger.Singleton.log(this, 1, "id = " + id);
        //Logger.Singleton.log(this, 1, "action = " + action);

        // if this client has already connected
        if (clients.containsKey(id)) {
            final Client client = clients.get(id);

            // wait till the server is ready, then have the client perform that action
            client.queueAction(new Callback(1, 1, () -> client.perform(action)));

        } else {
            // TODO: this client has never connected before
        }

    }

}
