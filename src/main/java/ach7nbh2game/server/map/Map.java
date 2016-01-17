package ach7nbh2game.server.map;

import ach7nbh2game.server.map.components.Ground;
import ach7nbh2game.server.map.components.IMapComponent;
import ach7nbh2game.server.map.components.Player;
import com.googlecode.blacken.grid.Grid;

import java.util.ArrayList;
import java.util.Random;

public class Map {

    private Grid<IMapComponent> grid;
    private int height;
    private int width;

    private Random rand;

    public Map (int heightIn, int widthIn) {

        height = heightIn;
        width = widthIn;

        grid = new Grid<IMapComponent>(new Ground(), height, width);
        grid.clear();

        rand = new Random();

    }

    public void addNewPlayer (int playerID) {

        int y = rand.nextInt(height);
        int x = rand.nextInt(width);
        grid.set(y, x, new Player(playerID));

    }

    public ArrayList<ArrayList<Integer>> getMapView (int x, int y, int height, int width) {

        ArrayList<ArrayList<Integer>> mapView = new ArrayList<ArrayList<Integer>>();
        for (int i = x; i < x + width; i++) {
            ArrayList<Integer> newRow = new ArrayList<Integer>();
            for (int j = y; j < y + height; j++) {
                newRow.add(grid.get(j, i).getMapChar());
            }
            mapView.add(newRow);
        }
        return mapView;

    }

}
