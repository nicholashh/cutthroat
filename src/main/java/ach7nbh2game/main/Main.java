package ach7nbh2game.main;

import ach7nbh2game.client.GameClient;
import ach7nbh2game.network.adapters.IClientToServer;
import ach7nbh2game.network.adapters.LocalSTOC;
import ach7nbh2game.network.adapters.ServerNTOG;
import ach7nbh2game.server.GameServer;

public class Main {

    public static void main (String[] args) {

        try {

            boolean localGame = true;
            if (localGame) {

                GameServer gameServer = new GameServer(true);

                IClientToServer adapter = new ServerNTOG(gameServer);
                GameClient clientA = new GameClient("Client A", true, adapter);
                GameClient clientB = new GameClient("Client B", true, adapter);

                LocalSTOC serverAdapter = new LocalSTOC();
                serverAdapter.addClient(clientA.getClientID(), clientA);
                serverAdapter.addClient(clientB.getClientID(), clientB);
                gameServer.installAdapter(serverAdapter);

                clientA.runTest();
                clientB.runTest();

            } else {

                new GameServer(false);
                new GameClient("Client A", false, null);
                new GameClient("Client B", false, null);

            }

        } catch (Exception e) {
            // TODO
        }

    }

}
