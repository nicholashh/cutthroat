package ach7nbh2game.main;

public class Utility {

    public static boolean isInteger (String string) {

        try { Integer.parseInt(string); return true; }
        catch (NumberFormatException nfe) { return false; }

    }

    public static boolean isAlphanumeric (String string) {

        boolean isAlphanumeric = true;

        for (char c : string.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                isAlphanumeric = false;
            }
        }

        return isAlphanumeric;

    }

}
