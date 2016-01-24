package ach7nbh2game.main;

import ach7nbh2game.client.GameClient;
import ach7nbh2game.server.GameServer;

public class Main {

    public static void main (String[] args) {

        try {

            GameServer server = new GameServer();
            GameClient gameA = new GameClient("Client A");
            GameClient gameB = new GameClient("Client B");

        } catch (Exception e) {
            // TODO
        }

    }

}
