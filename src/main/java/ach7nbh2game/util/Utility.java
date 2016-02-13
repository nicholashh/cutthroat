package ach7nbh2game.util;

import ach7nbh2game.server.map.components.IMapComponent;

import java.util.ArrayList;

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

    public static ArrayList<ArrayList<Integer>> componentToInteger (
            ArrayList<ArrayList<IMapComponent>> in) {

        ArrayList<ArrayList<Integer>> out = new ArrayList<>();
        for (ArrayList<IMapComponent> row : in) {
            ArrayList<Integer> newRow = new ArrayList<>();
            for (IMapComponent element : row) {
                newRow.add(element.getMapChar()); }
            out.add(newRow); }
        return out;

    }

}
