package ach7nbh2game.main;

import ach7nbh2game.client.GameClient;
import ach7nbh2game.server.GameServer;
import ach7nbh2game.util.Logger;

public class Main {

    public static void main (String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread () {
            public void run () { Logger.Singleton.log("exiting..."); }});

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

                System.out.println("CUTTHROAT ~ code version: 57");
                System.out.println("running Main with arguments " + args[0]);

                for (char c : args[0].toCharArray()) {
                    if (c == 's') {
                        numServers = 1; // TODO ports?
                    } else if (c == 'c') {
                        numClients = 1; // TODO window focus?
                    } else if (c == 'v') {
                        Logger.Singleton.loggingServer = true;
                        Logger.Singleton.loggingNetwork = true;
                        Logger.Singleton.loggingClient = true;
                    } else if (c == 'l') {
                        Constants.defaultHostname = "localhost";
                    } else if (c == 'i') {
                        Constants.defaultHostname = "";
                    } else if (c != '-') {
                        printUsage();
                        System.exit(-1);
                    }
                }

            }

            for (int i = 0; i < numServers; i++) {
                GameServer newServer = new GameServer();
                newServer.start();
            }

            for (int i = 0; i < numClients; i++) {
                GameClient newClient = new GameClient();
                newClient.start();
            }

        } catch (Exception e) {
            // TODO
        }

    }

    private static void printUsage () {

        System.err.println("Usage: java -jar cutthroat.jar -[s][c][v]");
        System.err.println("    [s] will make one Server");
        System.err.println("    [c] will make one Client");
        System.err.println("    [v] will turn on Verbose logging");
        System.err.println("    redundant flags will have no effect");
        System.err.println("    if no flags given, default is -c");

    }

}
