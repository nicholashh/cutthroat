package ach7nbh2game.main;

import ach7nbh2game.client.GameClient;
import ach7nbh2game.server.GameServer;
import ach7nbh2game.util.Logger;

public class Main {

    public static void main (String[] args) {

        try {

            int numServers = 0;
            int numClients = 0;

            int length = args.length;

            if (length == 0) {
                numServers = 0;
                numClients = 1;
            } else if (length > 1) {
                printUsage();
                System.exit(-1);
            } else {

                System.out.println("CUTTHROAT ~ code version: 49");
                System.out.println("running Main with arguments " + args[0]);

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
                //Log.set(Log.LEVEL_DEBUG);
                Logger.Singleton.loggingServer = true;
                Logger.Singleton.loggingNetwork = true;
                GameServer newServer = new GameServer();
                newServer.start();
            }

            for (int i = 0; i < numClients; i++) {
                //Log.set(Log.LEVEL_DEBUG);
                //Logger.Singleton.loggingClient = true;
                GameClient newClient = new GameClient();
                newClient.start();
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
