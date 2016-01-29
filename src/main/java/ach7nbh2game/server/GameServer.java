package ach7nbh2game.server;

import ach7nbh2game.client.PlayerInfo;
import ach7nbh2game.main.Constants.Directions;
import ach7nbh2game.network.NetServer;
import ach7nbh2game.network.adapters.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameServer {

    private IServerToClient network;

    private Map<Integer, Lobby> lobbies;
    private Map<Integer, Game> games;
    private Map<Integer, Integer> playerToGame;
    private Random rand;

    public GameServer (boolean localGame) throws IOException {

        System.out.println("making GameServer");

        if (!localGame) {

            NetServer netServer = new NetServer();
            IClientToServer adapterGTON = new ServerNTOG(this);
            IServerToClient adapterNTOG = new ServerGTON(netServer);
            netServer.installAdapter(adapterGTON);
            network = adapterNTOG;

        }

        lobbies = new HashMap<Integer, Lobby>();
        games = new HashMap<Integer, Game>();
        playerToGame = new HashMap<Integer, Integer>();
        rand = new Random();

    }

    public void installAdapter (IServerToClient adapter) {

        System.out.println("in GameServer, installAdapter()");

        network = adapter;

        // TODO need to add safety for what if installAdapter() is never called?

    }

    public void createNewLobby (int clientID, String name) {

        System.out.println("in GameServer, createNewLobby()");
        System.out.println("  clientID = " + clientID);
        System.out.println("  name = " + name);

        int newLobbyID = rand.nextInt();
        Lobby newLobby = new Lobby(name);
        lobbies.put(newLobbyID, newLobby);

    }

    public void joinLobby (int clientID, int lobbyID, PlayerInfo info) {

        System.out.println("in GameServer, joinLobby()");
        System.out.println("  clientID = " + clientID);
        System.out.println("  lobbyID = " + lobbyID);

        // TODO use info

        if (lobbies.containsKey(lobbyID)) {
            lobbies.get(lobbyID).join(clientID);
        } else {
            // TODO
        }

    }

    private Map<Integer, String> getLobbies () {

        Map<Integer, String> toReturn = new HashMap<Integer, String>();
        for (Map.Entry<Integer, Lobby> entry : lobbies.entrySet()) {
            toReturn.put(entry.getKey(), entry.getValue().getName());
        }

        return toReturn;

    }

    public void requestLobbies (int clientID) {

        System.out.println("in GameServer, requestLobbies()");
        System.out.println("  clientID = " + clientID);

        network.announceLobbies(clientID, getLobbies());

    }

    public void startGame (int lobbyID) {

        System.out.println("in GameServer, startGame()");
        System.out.println("  lobbyID = " + lobbyID);

        if (lobbies.containsKey(lobbyID)) {

            Lobby lobby = lobbies.get(lobbyID);
            Game newGame = lobby.startGame();
            lobbies.remove(lobbyID);
            games.put(lobbyID, newGame);

            for (int playerID : newGame.getPlayerIDs()) {
                network.enterGame(playerID);
                playerToGame.put(playerID, lobbyID);
            }

            sendGameState(newGame);

        } else {
            // TODO
        }

    }

    public void move (int clientID, Directions direction) {

        System.out.println("in GameServer, move()");
        System.out.println("  clientID = " + clientID);
        System.out.println("  direction = " + direction);

        if (playerToGame.containsKey(clientID)) {

            Game game = games.get(playerToGame.get(clientID));

            game.move(clientID, direction);

            sendGameState(game);

        } else {
            // TODO
        }

    }

    private void sendGameState (Game game) {

        for (int playerID : game.getPlayerIDs()) {

            // TODO GameState better
            GameState state = new GameState();
            state.setFrame(game.getMapView(playerID));

            network.updateGameState(playerID, state);

        }

    }

}
