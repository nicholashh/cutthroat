package ach7nbh2game.util;

import ach7nbh2game.server.map.AGameActor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private Logger () {}
    public static Logger Singleton = new Logger();

    private final int tabSize = 4;
    public boolean loggingClient = false;
    public boolean loggingServer = false;
    public boolean loggingNetwork = false;

    private final int classNameMaxLength = 15;
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("[yyyy-MM-dd][HH:mm:ss.SSS]");

    public void log (Object host, int numTabs, String message) {

        Class hostClass = host.getClass();

        String hostClassSimpleName;
        if (host instanceof AGameActor) {
            hostClassSimpleName = host.toString();
        } else {
            hostClassSimpleName = hostClass.getSimpleName();
        }

        int color = -1;
        String hostClassFullName = hostClass.getName();
        if (loggingClient && hostClassFullName.contains(".client")) {
            color = 32; // green
        } else if (loggingServer && hostClassFullName.contains(".server")) {
            color = 34; // blue
        } else if (loggingNetwork && hostClassFullName.contains(".network")) {
            color = 35; // magenta
        }

        if (color != -1) {

            System.out.println("\u001B[" + color + "m" + dateFormat.format(new Date()) + "[" +
                    String.format("%" + classNameMaxLength + "s", hostClassSimpleName) + "] " +
                    (new String(new char[numTabs * tabSize]).replace("\0", " ")) + message);

        }

    }

}
