package ach7nbh2game.server.map;

import ach7nbh2game.server.map.components.Ground;
import ach7nbh2game.server.map.components.IMapComponent;
import ach7nbh2game.server.map.components.Player;
import com.googlecode.blacken.grid.Grid;

import java.util.ArrayList;

public class Map {

    private Grid<IMapComponent> grid;

    public Map (int height, int width) {

        grid = new Grid<IMapComponent>(new Ground(), height, width);
        grid.clear();

        grid.set(7, 11, new Player());

    }

    public ArrayList<ArrayList<Integer>> getMapView (int x, int y, int height, int width) {

        ArrayList<ArrayList<Integer>> mapView = new ArrayList<ArrayList<Integer>>();
        for (int i = x; i < x + width; i++) {
            ArrayList<Integer> newRow = new ArrayList<Integer>();
            for (int j = y; j < y + height; j++) {
                newRow.add(grid.get(i, j).getMapChar());
            }
            mapView.add(newRow);
        }
        return mapView;

    }

}
