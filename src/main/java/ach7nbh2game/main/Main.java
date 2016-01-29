package ach7nbh2game.main;

import ach7nbh2game.client.GameClient;
import ach7nbh2game.network.adapters.IClientToServer;
import ach7nbh2game.network.adapters.LocalSTOC;
import ach7nbh2game.network.adapters.ServerNTOG;
import ach7nbh2game.server.GameServer;
import com.esotericsoftware.minlog.Log;

public class Main {

    static boolean localGame = false;
    static boolean isServer = false;

    public static void main (String[] args) {

        try {

            if (localGame) {

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

                if (isServer) {

                    Log.set(Log.LEVEL_DEBUG);
                    GameServer gs = new GameServer(false);

                    Log.set(Log.LEVEL_DEBUG);
                    GameClient gcA = new GameClient("Client A", false, null);

                    gcA.runTest();

                } else {

                    Log.set(Log.LEVEL_DEBUG);
                    GameClient gcB = new GameClient("Client B", false, null);

                    Log.set(Log.LEVEL_DEBUG);
                    GameClient gcC = new GameClient("Client C", false, null);

                    gcC.runTest();
                    gcB.runTest();

                }

            }
        } catch (Exception e) {
            // TODO
        }
    }
}
