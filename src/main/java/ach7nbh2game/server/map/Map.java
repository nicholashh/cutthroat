package ach7nbh2game.server.map;

import ach7nbh2game.main.Constants;
import ach7nbh2game.main.Constants.Directions;
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
    private final int height;
    private final int width;

    private java.util.Map<Integer, Player> players;

    private Random rand;

    public Map (int heightIn, int widthIn) {

        height = heightIn;
        width = widthIn;

        grid = new Grid<IMapComponent>(new Ground(), height, width);
        grid.clear();

        players = new HashMap<Integer, Player>();

        rand = new Random();

        generateTerrain();

    }

    private void generateTerrain () {

        System.out.println("in Map, generateTerrain()");

        ArrayList<Double> factors = new ArrayList<Double>();
        factors.add(0.4);
        factors.add(0.2);
        factors.add(0.1);

        ArrayList<Integer> howMany = new ArrayList<Integer>();
        howMany.add(2);
        howMany.add(4);
        howMany.add(8);

        assert(factors.size() == howMany.size());

        for (int i = 0; i < factors.size(); i++) {

            for (int j = 0; j < howMany.get(i); j++) {

                double factor = factors.get(i);

                int thingHalfHeight = (int) (factor * 0.5 * height);
                int thingHalfWidth = (int) (factor * 0.5 * width);

                int thingHeight = thingHalfHeight * 2;
                int thingWidth = thingHalfWidth * 2;

                // TODO make this more efficient
                int attempts = 0;
                int maxNumAttempts = 10;
                while (true) {

                    boolean overlapping = false;

                    int yMid = rand.nextInt(height - thingHeight) + thingHalfHeight;
                    int xMid = rand.nextInt(width - thingWidth) + thingHalfWidth;

                    for (int y = yMid - thingHalfHeight; y < yMid + thingHalfHeight; y++) {
                        for (int x = xMid - thingHalfWidth; x < xMid + thingHalfWidth; x++) {
                            if (!(grid.get(y, x) instanceof Ground)) {
                                overlapping = true;
                            }
                        }
                    }

                    if (!overlapping) {

                        for (int y = yMid - thingHalfHeight; y < yMid + thingHalfHeight; y++) {
                            for (int x = xMid - thingHalfWidth; x < xMid + thingHalfWidth; x++) {
                                grid.set(y, x, new Wall());
                            }
                        }

                        System.out.println("  factor = " + factor + ", which one = " + j + ", SUCCESS");
                        break;

                    } else if (attempts > maxNumAttempts) {

                        System.out.println("  factor = " + factor + ", which one = " + j + ", overlapping");
                        break;

                    } else {

                        attempts++;

                    }

                }

            }

        }

    }

    public void addNewPlayer (int playerID) {

        while (true) {

            int y = rand.nextInt(height);
            int x = rand.nextInt(width);

            if (grid.get(y, x) instanceof Ground) {

                Player newPlayer = new Player(playerID, y, x);
                players.put(playerID, newPlayer);
                grid.set(y, x, newPlayer);

                break;

            }

        }

    }

    public ArrayList<ArrayList<Integer>> getMapView (int playerID) {

        Player player = players.get(playerID);
        int x = player.getX();
        int y = player.getY();

        //System.out.println("in Map, getMapView()");
        //System.out.println("  height = " + height);
        //System.out.println("  width = " + width);
        //System.out.println("  player.getX() = " + player.getX());
        //System.out.println("  player.getY() = " + player.getY());

        int halfWidth = Constants.clientWidth / 2;
        int xLow = x - halfWidth;
        int xHigh = x + halfWidth + 1;

        int halfHeight = Constants.clientHeight / 2;
        int yLow = y - halfHeight;
        int yHigh = y + halfHeight + 1;

        //System.out.println("  initial values");
        //System.out.println("    xLow = " + xLow);
        //System.out.println("    xHigh = " + xHigh);
        //System.out.println("    yLow = " + yLow);
        //System.out.println("    yHigh = " + yHigh);

        boolean movedX = false;
        boolean movedY = false;

        if (xLow < 0) {

            xHigh -= xLow;
            movedX = true;
            //System.out.println("  moving 1, xHigh = " + xHigh);

            xLow = 0;
            //System.out.println("  moving 2, xLow = " + xLow);

        }

        if (yLow < 0) {

            yHigh -= yLow;
            movedY = true;
            //System.out.println("  moving 3, yHigh = " + yHigh);

            yLow = 0;
            //System.out.println("  moving 4, yLow = " + yLow);

        }

        if (xHigh > width) {

            if (!movedX) {
                xLow -= (xHigh - width);
                //System.out.println("  moving 5, xLow = " + xLow);
            }

            xHigh = width;
            //System.out.println("  moving 6, xHigh = " + xHigh);

        }

        if (yHigh > height) {

            if (!movedY) {
                yLow -= (yHigh - height);
                //System.out.println("  moving 7, yLow = " + yLow);
            }

            yHigh = height;
            //System.out.println("  moving 8, yHigh = " + yHigh);

        }

        return getMapView(xLow, yLow, xHigh, yHigh);

    }

    private ArrayList<ArrayList<Integer>> getMapView (int xLow, int yLow, int xHigh, int yHigh) {

        //System.out.println("  final values");
        //System.out.println("    xLow = " + xLow);
        //System.out.println("    xHigh = " + xHigh);
        //System.out.println("    yLow = " + yLow);
        //System.out.println("    yHigh = " + yHigh);

        ArrayList<ArrayList<Integer>> mapView = new ArrayList<ArrayList<Integer>>();
        for (int j = yLow; j < yHigh; j++) {
            ArrayList<Integer> newRow = new ArrayList<Integer>();
            for (int i = xLow; i < xHigh; i++) {
                newRow.add(grid.get(j, i).getMapChar());
            }
            mapView.add(newRow);
        }
        return mapView;

    }

    public void move (int playerID, Directions direction) {

        if (players.containsKey(playerID)) {

            Player player = players.get(playerID);
            int curX = player.getX();
            int curY = player.getY();

            int newX = curX;
            int newY = curY;
            switch (direction) {
                case UP:
                    newX = curX;
                    newY = curY - 1;
                    break;
                case DOWN:
                    newX = curX;
                    newY = curY + 1;
                    break;
                case LEFT:
                    newX = curX - 1;
                    newY = curY;
                    break;
                case RIGHT:
                    newX = curX + 1;
                    newY = curY;
                    break;
            }

            if (newX >= 0 && newY >= 0 && newX < width && newY < height) {
                if (grid.get(newY, newX) instanceof Ground) {
                    player.setY(newY);
                    player.setX(newX);
                    grid.set(newY, newX, player);
                    grid.set(curY, curX, new Ground());
                }
            }

        }

    }

}
