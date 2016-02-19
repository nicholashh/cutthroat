package ach7nbh2game.server.map;

import ach7nbh2game.main.Constants;
import ach7nbh2game.server.map.components.Ground;
import ach7nbh2game.server.map.components.IMapComponent;
import ach7nbh2game.server.map.components.Wall;
import ach7nbh2game.util.Coordinate;
import ach7nbh2game.util.Logger;
import com.googlecode.blacken.grid.Grid;

import java.util.ArrayList;
import java.util.Random;

public class GameMap {

    private int height, width;
    private Grid<IMapComponent> grid;

    private Random rand = new Random();

    public GameMap (int heightIn, int widthIn) {

        height = heightIn;
        width = widthIn;

        grid = new Grid<IMapComponent>(new Ground(), height, width);

        initMap();

    }

    public IMapComponent get (Coordinate coordinate) {
        return get(coordinate.y, coordinate.x);
    }

    public IMapComponent get (int y, int x) {

        //Logger.Singleton.log(this, 0, "get:");
        //Logger.Singleton.log(this, 1, "x = " + x);
        //Logger.Singleton.log(this, 1, "y = " + y);

        if (x >= 0 && y >= 0 && x < width && y < height) {
            return grid.get(y, x);
        } else {
            // TODO this should be more graceful
            return null;
        }

    }

    public void set (int y, int x, IMapComponent thing) {

        //Logger.Singleton.log(this, 0, "set:");
        //Logger.Singleton.log(this, 1, "x = " + x);
        //Logger.Singleton.log(this, 1, "y = " + y);
        //Logger.Singleton.log(this, 1, "thing = " + thing);

        if (x >= 0 && y >= 0 && x < width && y < height) {
            grid.set(y, x, thing);
            thing.setY(y);
            thing.setX(x);
        } else {
            // TODO this should be more graceful
        }

    }

    public void swap (IMapComponent thing1, IMapComponent thing2) {

        //Logger.Singleton.log(this, 0, "swap:");
        //Logger.Singleton.log(this, 1, "thing1 = " + thing1);
        //Logger.Singleton.log(this, 1, "thing2 = " + thing2);

        int thing1y = thing1.getY();
        int thing1x = thing1.getX();
        int thing2y = thing2.getY();
        int thing2x = thing2.getX();

        set(thing1y, thing1x, thing2);
        set(thing2y, thing2x, thing1);

    }

    private void initMap () {

        Logger.Singleton.log(this, 0, "initMap:");

        grid.clear();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Ground newGround = new Ground();
                newGround.placeOnMap(this, x, y);
            }
        }

        generateTerrain();

    }

    private void generateTerrain () {

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
                            if (!(get(y, x) instanceof Ground)) {
                                overlapping = true;
                            }
                        }
                    }

                    if (!overlapping) {

                        for (int y = yMid - thingHalfHeight; y < yMid + thingHalfHeight; y++) {
                            for (int x = xMid - thingHalfWidth; x < xMid + thingHalfWidth; x++) {
                                Wall newWall = new Wall();
                                newWall.placeOnMap(this, x, y);
                            }
                        }

                        Logger.Singleton.log(this, 1, "factor = " + factor + ", which one = " + j + ", SUCCESS");
                        break;

                    } else if (attempts > maxNumAttempts) {

                        Logger.Singleton.log(this, 1, "factor = " + factor + ", which one = " + j + ", overlapping");
                        break;

                    } else {

                        attempts++;

                    }

                }

            }

        }

    }

    public ArrayList<ArrayList<IMapComponent>> getPerspectiveFrom (int x, int y) {
        return getPerspectiveFrom(x, y, Constants.clientMapWidth, Constants.clientMapHeight);
    }

    public ArrayList<ArrayList<IMapComponent>> getPerspectiveFrom (
            int x, int y, int viewWidth, int viewHeight) {

        int halfWidth = viewWidth / 2;
        int xLow = x - halfWidth;
        int xHigh = x + halfWidth + 1;

        int halfHeight = viewHeight / 2;
        int yLow = y - halfHeight;
        int yHigh = y + halfHeight + 1;

        boolean movedX = false;
        boolean movedY = false;

        if (xLow < 0) {
            xHigh -= xLow;
            movedX = true;
            xLow = 0;
        }

        if (yLow < 0) {
            yHigh -= yLow;
            movedY = true;
            yLow = 0;
        }

        if (xHigh > width) {
            if (!movedX) { xLow -= (xHigh - width); }
            xHigh = width;
        }

        if (yHigh > height) {
            if (!movedY) { yLow -= (yHigh - height); }
            yHigh = height;
        }

        return getPerspective(xLow, yLow, xHigh, yHigh);

    }

    private ArrayList<ArrayList<IMapComponent>> getPerspective (int xLow, int yLow, int xHigh, int yHigh) {

        // TODO: add safety here

        ArrayList<ArrayList<IMapComponent>> mapView = new ArrayList<>();
        for (int j = yLow; j < yHigh; j++) {
            ArrayList<IMapComponent> newRow = new ArrayList<>();
            for (int i = xLow; i < xHigh; i++) {
                newRow.add(get(j, i));
            }
            mapView.add(newRow);
        }
        return mapView;

    }

    public Coordinate getRandomLocationWithA (Class aThingLikeThis) {

        Logger.Singleton.log(this, 0, "getRandomLocationWithA:");
        Logger.Singleton.log(this, 1, "aThingLikeThis = " + aThingLikeThis.getSimpleName());

        while (true) {
            int y = rand.nextInt(height);
            int x = rand.nextInt(width);
            if (get(y, x).getClass() == aThingLikeThis) {
                return new Coordinate(y, x);
            }
        }

    }

}
