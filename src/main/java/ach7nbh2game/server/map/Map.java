package ach7nbh2game.server.map;

import ach7nbh2game.server.map.components.Ground;
import ach7nbh2game.server.map.components.IMapComponent;
import ach7nbh2game.server.map.components.Player;
import ach7nbh2game.server.map.components.Wall;
import com.googlecode.blacken.grid.Grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Map {

    private Grid<IMapComponent> grid;
    private int height;
    private int width;

    private java.util.Map<Integer, Player> players;

    private Random rand;

    public Map (int heightIn, int widthIn) {

        height = heightIn;
        width = widthIn;

        grid = new Grid<IMapComponent>(new Ground(), height, width);
        grid.clear();

        players = new HashMap<Integer, Player>();

        rand = new Random();

        for (int i = 0; i < height * width * 0.05; i++) {
            int y = rand.nextInt(height);
            int x = rand.nextInt(width);
            grid.set(y, x, new Wall());
        }

    }

    public void addNewPlayer (int playerID) {

        int y = rand.nextInt(height);
        int x = rand.nextInt(width);
        Player newPlayer = new Player(playerID, y, x);
        players.put(playerID, newPlayer);
        grid.set(y, x, newPlayer);

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

    public void moveUp (int playerID) {

        if (players.containsKey(playerID)) {

            Player player = players.get(playerID);
            int playerX = player.getX();
            int playerY = player.getY();

            if (!(grid.get(playerY - 1, playerX) instanceof Wall)) {
                player.setY(playerY - 1);
                grid.set(playerY - 1, playerX, player);
                grid.set(playerY, playerX, new Ground());
            }

        }

    }

}
