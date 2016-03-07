package ach7nbh2game.client;

import ach7nbh2game.main.Constants;
import ach7nbh2game.util.Utility;
import com.sun.org.apache.bcel.internal.generic.POP;

import javax.swing.*;
import java.util.ArrayList;

public class PopupController {

    private static ClientView cv;

    public PopupController(ClientView view) {
        cv = view;
    }

    public static String askForServerIP () {

        String prompt = "";
        prompt += "Connect to a game server!\n";
        prompt += "What is the server's IP address?";

        return askForThing(prompt, "cutthroat.pwnz.org");

    }

    public static String askForUsername () {

        String prompt = "";
        prompt += "Pick a username!\n";
        prompt += "(Must be alphanumeric.)\n";
        prompt += "(Must be 1-10 characters long.)";

        String name = askForThing(prompt, "");

        int length = name.length();
        if (!Utility.isAlphanumeric(name) || length < 1 || length > 10) {
            System.err.println("invalid username chosen; trying again...");
            return askForUsername();
        } else {
            return name;
        }

    }

    public static String askForThing (String label, String value) {

        String input = (String) JOptionPane.showInputDialog(
                null, label, null, JOptionPane.QUESTION_MESSAGE, null, null, value);
        if (input == null || input.trim().length() == 0) return "";
        else if (input.equals("exit;")) { System.exit(0); return null; }
        else return input.trim();

    }

}
