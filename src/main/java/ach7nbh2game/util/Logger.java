package ach7nbh2game.util;

public class Logger {

    private static final int tabSize = 4;

    public static Logger Singleton = new Logger();
    private Logger () {}

    public void log (Object host, int numTabs, String message) {
        System.out.println("[" + host.getClass().getSimpleName() + "] "
                + (new String(new char[numTabs * tabSize]).replace("\0", " ")) + message);
    }

}
