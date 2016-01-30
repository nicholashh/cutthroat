package ach7nbh2game.main;

import ach7nbh2game.client.GameClient;
import ach7nbh2game.network.adapters.IClientToServer;
import ach7nbh2game.network.adapters.LocalSTOC;
import ach7nbh2game.network.adapters.ServerNTOG;
import ach7nbh2game.server.GameServer;

public class Main {

    static final boolean localGame = false;
    static final boolean isServer = true;
    static final boolean isClient = true;

    public static void main (String[] args) {

        try {

            if (localGame) {

                // TODO this should be more robust

                GameServer gameServer = new GameServer(true);

                IClientToServer adapter = new ServerNTOG(gameServer);
                GameClient clientA = new GameClient("Client A", true, adapter);
                GameClient clientB = new GameClient("Client B", true, adapter);

                clientA.setClientID(0);
                clientB.setClientID(1);

                LocalSTOC serverAdapter = new LocalSTOC();
                serverAdapter.addClient(clientA.getClientID(), clientA);
                serverAdapter.addClient(clientB.getClientID(), clientB);
                gameServer.installAdapter(serverAdapter);

                clientA.runTest();
                clientB.runTest();

            } else {

                if (isServer) { new GameServer(false); }

                if (isClient) { new GameClient("Game Client", false, null); }

            }

        } catch (Exception e) {
            // TODO
        }

    }

}
