package ach7nbh2game.server;

import ach7nbh2game.client.Client;

import java.util.*;

public class Server {

    private Map<Integer, Client> clients;
    private Map<Integer, Lobby> lobbies;
    private Map<Integer, Game> games;
    private Random rand;

    public Server () {

        clients = new HashMap<Integer, Client>();
        lobbies = new HashMap<Integer, Lobby>();
        games = new HashMap<Integer, Game>();
        rand = new Random();

    }

    public int registerNewClient (Client newClient) {

        int newID = rand.nextInt();
        clients.put(newID, newClient);
        return newID;

    }

    public int createNewLobby () {

        int newID = rand.nextInt();
        Lobby newLobby = new Lobby();
        lobbies.put(newID, newLobby);
        return newID;

    }

    public Set<Integer> getLobbies () {

        return lobbies.keySet();

    }

    public boolean joinLobby (int clientID, int lobbyID) {

        if (clients.containsKey(clientID)) {
            if (lobbies.containsKey(lobbyID)) {
                lobbies.get(lobbyID).join(clientID);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    public boolean startGame (final int lobbyID) {

        if (lobbies.containsKey(lobbyID)) {

            Lobby lobby = lobbies.get(lobbyID);
            Game newGame = lobby.startGame();
            lobbies.remove(lobbyID);
            games.put(lobbyID, newGame);

            for (int playerID : newGame.getPlayerIDs()) {

                final int thisPlayerID = playerID;

                (new Thread() {
                    public void run(){
                        clients.get(thisPlayerID).enterGame(lobbyID);
                    }
                }).start();

            }

            return true;

        } else {
            return false;
        }

    }

    public ArrayList<ArrayList<Integer>> getMapView (int clientID, int gameID) {

        if (clients.containsKey(clientID)) {
            if (games.containsKey(gameID)) {
                return games.get(gameID).getMapView(clientID);
            } else {
                return null;
            }
        } else {
            return null;
        }

    }

}
