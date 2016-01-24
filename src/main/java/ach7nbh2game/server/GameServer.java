package ach7nbh2game.server;

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

    public GameServer () throws IOException {

        NetServer netServer = new NetServer();
        IClientToServer adapterGTON = new ServerNTOG(this);
        IServerToClient adapterNTOG = new ServerGTON(netServer);
        netServer.installAdapter(adapterGTON);
        network = adapterNTOG;

        lobbies = new HashMap<Integer, Lobby>();
        games = new HashMap<Integer, Game>();
        playerToGame = new HashMap<Integer, Integer>();
        rand = new Random();

    }

    public void createNewLobby (int clientID, String name) {

        int newLobbyID = rand.nextInt();
        Lobby newLobby = new Lobby(name);
        lobbies.put(newLobbyID, newLobby);

        joinLobby(clientID, newLobbyID);

    }

    public void joinLobby (int clientID, int lobbyID) {

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

        network.announceLobbies(clientID, getLobbies());

    }

    public void startGame (int lobbyID) {

        if (lobbies.containsKey(lobbyID)) {

            Lobby lobby = lobbies.get(lobbyID);
            Game newGame = lobby.startGame();
            lobbies.remove(lobbyID);
            games.put(lobbyID, newGame);

            for (int playerID : newGame.getPlayerIDs()) {
                network.enterGame(playerID);
                playerToGame.put(playerID, lobbyID);
            }

        } else {
            // TODO
        }

    }

    public void move (int clientID, Directions direction) {

        if (playerToGame.containsKey(clientID)) {

            Game game = games.get(playerToGame.get(clientID));

            game.move(clientID, direction);

            for (int playerID : game.getPlayerIDs()) {
                network.updateGameState(playerID, game.getMapView(playerID));
            }

        } else {
            // TODO
        }

    }

}
