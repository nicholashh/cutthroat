package ach7nbh2game.main;

import ach7nbh2game.client.GameClient;
import ach7nbh2game.network.adapters.IClientToServer;
import ach7nbh2game.network.adapters.LocalSTOC;
import ach7nbh2game.network.adapters.ServerNTOG;
import ach7nbh2game.server.GameServer;

public class Main {

    public static void main (String[] args) {

        try {

            if (false) {

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

                int numServers = 0;
                int numClients = 0;

                int length = args.length;

                System.out.print(length + " args\n");
                System.out.print("args = ");
                for (String arg : args) {
                    System.out.print(" " + arg);
                }
                System.out.print("\n");

                if (length == 0) {
                    numServers = 0;
                    numClients = 1;
                } else if (length > 1) {
                    printUsage();
                    System.exit(-1);
                } else {
                    for (char c : args[0].toCharArray()) {
                        if (c == 's') {
                            numServers = 1; // TODO ports?
                        } else if (c == 'c') {
                            numClients = 1; // TODO window focus?
                        } else if (c != '-') {
                            printUsage();
                            System.exit(-1);
                        }
                    }
                }

                for (int i = 0; i < numServers; i++) {
                    new GameServer(false);
                }

                for (int i = 0; i < numClients; i++) {
                    new GameClient("Game Client", false, null);
                }

            }

        } catch (Exception e) {
            // TODO
        }

    }

    private static void printUsage () {

        System.err.println("Usage: java -jar cutthroat.jar -[s][c]");
        System.err.println("    [s] will make one server");
        System.err.println("    [c] will make one client");
        System.err.println("    multiple s or c flags allowed");
        System.err.println("    if no flags, default is -c");
        System.err.println("    example: -ssccc");

    }

}
