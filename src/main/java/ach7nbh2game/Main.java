package ach7nbh2game;

import ach7nbh2game.client.Client;
import ach7nbh2game.server.Server;

public class Main {

    public static void main (String[] args) {

        Server server = new Server();
        new Client("Client A", server);
        new Client("Client B", server);

    }

}
