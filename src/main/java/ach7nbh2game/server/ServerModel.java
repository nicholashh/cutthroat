package ach7nbh2game.server;

import ach7nbh2game.client.PlayerInfo;
import ach7nbh2game.main.Constants;
import ach7nbh2game.network.adapters.IServerToClient;

import java.util.*;

public class ServerModel {

    private IServerToClient network;

    private Map<Integer, String> players;
    private Map<Integer, Lobby> lobbies;
    private Map<Integer, Game> games;
    private Map<Integer, Integer> playerToGame;
    private Random rand;

    public ServerModel (IServerToClient networkIn) {

        System.out.println("making new ServerModel...");

        network = networkIn;

        players = new HashMap<Integer, String>();
        lobbies = new HashMap<Integer, Lobby>();
        games = new HashMap<Integer, Game>();
        playerToGame = new HashMap<Integer, Integer>();
        rand = new Random();

    }

    public void start () {

        System.out.println("starting the ServerModel!");

    }

    public void createNewLobby (int clientID, String name) {

        System.out.println("in GameServer, createNewLobby()");
        System.out.println("  clientID = " + clientID);
        System.out.println("  name = " + name);

        int newLobbyID = rand.nextInt();
        Lobby newLobby = new Lobby(this, name);
        lobbies.put(newLobbyID, newLobby);

    }

    public void joinLobby (int clientID, int lobbyID, PlayerInfo clientInfo) {

        System.out.println("in GameServer, joinLobby()");
        System.out.println("  clientID = " + clientID);
        System.out.println("  lobbyID = " + lobbyID);

        if (lobbies.containsKey(lobbyID)) {
            lobbies.get(lobbyID).join(clientID, clientInfo);
            players.put(clientID, clientInfo.getUsername());
        } else {
            // TODO
        }

    }

    private Map<Integer, String> getLobbyToName () {

        Map<Integer, String> toReturn = new HashMap<Integer, String>();

        for (Map.Entry<Integer, Lobby> entry : lobbies.entrySet()) {
            toReturn.put(entry.getKey(), entry.getValue().getName());
        }

        return toReturn;

    }

    private Map<Integer, Set<Integer>> getLobbyToPlayers () {

        Map<Integer, Set<Integer>> toReturn = new HashMap<Integer, Set<Integer>>();

        for (Map.Entry<Integer, Lobby> entry : lobbies.entrySet()) {
            Set<Integer> toAdd = new HashSet<Integer>(entry.getValue().getPlayers());
            toReturn.put(entry.getKey(), toAdd);
        }

        return toReturn;

    }

    public void requestLobbies (int clientID) {

        System.out.println("in GameServer, requestLobbies()");
        System.out.println("  clientID = " + clientID);

        network.announceLobbies(clientID, getLobbyToName(), players, getLobbyToPlayers());

    }

    public void startGame (int lobbyID) {

        System.out.println("in GameServer, startGame()");
        System.out.println("  lobbyID = " + lobbyID);

        if (lobbies.containsKey(lobbyID)) {

            Lobby lobby = lobbies.get(lobbyID);
            Game newGame = lobby.startGame();
            lobbies.remove(lobbyID);
            games.put(lobbyID, newGame);

            for (int playerID : newGame.getPlayerInfo().keySet()) {
                network.enterGame(playerID);
                playerToGame.put(playerID, lobbyID);
            }

            sendGameState(newGame);

        } else {
            // TODO
        }

    }

    public void move (int clientID, Constants.Directions direction) {

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

    protected void sendGameState (Game game) {

        for (int playerID : game.getPlayerInfo().keySet()) {
            network.updateGameState(playerID, game.getGameState(playerID));
        }

    }

}
