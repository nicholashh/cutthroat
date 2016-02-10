package ach7nbh2game.util;

import ach7nbh2game.server.map.components.AMapComponent;

public class Logger {

    private Logger () {}
    public static Logger Singleton = new Logger();

    private final int tabSize = 4;
    public boolean loggingClient = false;
    public boolean loggingServer = false;

    public void log (Object host, int numTabs, String message) {

        Class hostClass = host.getClass();

        String hostClassSimpleName;
        if (host instanceof AMapComponent) {
            hostClassSimpleName = host.toString();
        } else {
            hostClassSimpleName = hostClass.getSimpleName();
        }

        String hostClassFullName = hostClass.getName();
        if ((loggingClient && hostClassFullName.contains(".client")) ||
                (loggingServer && hostClassFullName.contains(".server"))) {

            if (numTabs == 0) {
                System.out.println();
            }

            System.out.println("[" + hostClassSimpleName + "] " + (new String(
                    new char[numTabs * tabSize]).replace("\0", " ")) + message);

        }

    }

}
