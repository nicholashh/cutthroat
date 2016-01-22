package ach7nbh2game;

import ach7nbh2game.client.GameClient;
import ach7nbh2game.server.GameServer;

public class Main {

    public static void main (String[] args) {

        GameServer server = new GameServer();
        new GameClient("Client A", server);
        new GameClient("Client B", server);

    }

}
