package ach7nbh2game;

import ach7nbh2game.network.GameClient;
import ach7nbh2game.server.Server;

public class Main {

    public static void main (String[] args) {

        Server server = new Server();
        new GameClient("Client A", server);
        new GameClient("Client B", server);

    }

}
