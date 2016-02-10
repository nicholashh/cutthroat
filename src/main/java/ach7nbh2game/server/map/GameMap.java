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

    private int height;
    private int width;

    private Grid<IMapComponent> grid = new Grid<IMapComponent>(new Ground(), height, width);
    private Random rand = new Random();

    //private int levelID;
    //
    //private Thread timer;

    public GameMap (int heightIn, int widthIn) {

        height = heightIn;
        width = widthIn;

        grid = new Grid<IMapComponent>(new Ground(), height, width);

        initMap();
        //moveBullets(); // TODO make more general

    }

    public IMapComponent get (int y, int x) {
        if (x >= 0 && y >= 0 && x < width && y < height) {
            return grid.get(y, x);
        } else {
            // TODO should this be better?
            return null;
        }
    }

    public void set (int y, int x, IMapComponent thing) {
        if (x >= 0 && y >= 0 && x < width && y < height) {
            grid.set(y, x, thing);
        } else {
            // TODO should this be better?
        }
    }

    private void initMap () {

        Logger.Singleton.log(this, 0, "initMap:");

        // TODO should we remove this clear?
        grid.clear();

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

    public ArrayList<ArrayList<Integer>> getPerspectiveFrom (int x, int y) {

        Logger.Singleton.log(this, 0, "getPerspectiveFrom:");
        Logger.Singleton.log(this, 1, "x = " + x);
        Logger.Singleton.log(this, 1, "y = " + y);

        //System.out.println("in Map, getMapView()");
        //System.out.println("  height = " + height);
        //System.out.println("  width = " + width);
        //System.out.println("  player.getX() = " + player.getX());
        //System.out.println("  player.getY() = " + player.getY());

        int halfWidth = Constants.clientMapWidth / 2;
        int xLow = x - halfWidth;
        int xHigh = x + halfWidth + 1;

        int halfHeight = Constants.clientMapHeight / 2;
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

        return getPerspective(xLow, yLow, xHigh, yHigh);

    }

    private ArrayList<ArrayList<Integer>> getPerspective (int xLow, int yLow, int xHigh, int yHigh) {

        //System.out.println("  final values");
        //System.out.println("    xLow = " + xLow);
        //System.out.println("    xHigh = " + xHigh);
        //System.out.println("    yLow = " + yLow);
        //System.out.println("    yHigh = " + yHigh);

        // TODO: add safety here

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

    public Coordinate getRandomLocationWithA (Class aThingLikeThis) {
        while (true) {
            int y = rand.nextInt(height);
            int x = rand.nextInt(width);
            if (grid.get(y, x).getClass() == aThingLikeThis) {
                return new Coordinate(y, x);
            }
        }
    }

    // TODO awful, remove asap
    //private Set<Bullet> bullets = new HashSet<Bullet>();

    // TODO awful, remove asap
    //private void moveBullets () {
    //
    //    (new Thread() { public void run() {
    //
    //        while (true) {
    //
    //            try {
    //                Thread.sleep(100);
    //            } catch (InterruptedException e) {
    //                e.printStackTrace();
    //            }
    //
    //            // TODO need to think about how to deal with java.util.ConcurrentModificationException
    //            Set<Bullet> bulletsCopy = new HashSet<Bullet>(bullets);
    //            for (Bullet bullet : bulletsCopy) {
    //
    //                System.out.println("found one bullet");
    //
    //                // TODO don't repeat this logic
    //                int curX = bullet.getX();
    //                int curY = bullet.getY();
    //                int newX = curX;
    //                int newY = curY;
    //                switch (bullet.getDirection()) {
    //                    case GUN_UP:
    //                        newX = curX;
    //                        newY = curY - 1;
    //                        break;
    //                    case GUN_DOWN:
    //                        newX = curX;
    //                        newY = curY + 1;
    //                        break;
    //                    case GUN_LEFT:
    //                        newX = curX - 1;
    //                        newY = curY;
    //                        break;
    //                    case GUN_RIGHT:
    //                        newX = curX + 1;
    //                        newY = curY;
    //                        break;
    //                }
    //
    //                System.out.println("cur: x = " + curX + ", y = " + curY);
    //                System.out.println("new: x = " + newX + ", y = " + newY);
    //
    //                if (newX >= 0 && newY >= 0 && newX < width && newY < height) {
    //
    //                    IMapComponent thing = grid.get(newY, newX);
    //
    //                    if (thing instanceof Ground) {
    //
    //                        System.out.println("thing instanceof Ground");
    //
    //                        bullet.setY(newY);
    //                        bullet.setX(newX);
    //                        grid.set(newY, newX, bullet);
    //                        grid.set(curY, curX, new Ground());
    //
    //                    } else if (thing instanceof Player) {
    //
    //                        System.out.println("thing instanceof Player");
    //
    //                        String bulletPlayerName = players.get(bullet.getOwner().getID()).getPlayerInfo().getUsername();
    //                        String otherPlayerName = players.get(((Player) thing).getID()).getPlayerInfo().getUsername();
    //
    //                        int curScore = gameState.getScores().get(bulletPlayerName);
    //                        gameState.updateScore(bulletPlayerName, curScore + 1);
    //                        curScore = gameState.getScores().get(otherPlayerName);
    //                        gameState.updateScore(otherPlayerName, curScore - 1);
    //
    //                        restartGame();
    //
    //                    } else if (thing instanceof Wall) {
    //
    //                        System.out.println("thing instanceof Wall");
    //
    //                        bullets.remove(bullet);
    //                        grid.set(curY, curX, new Ground());
    //
    //                    }
    //
    //                } else {
    //
    //                    System.out.println("went off the screen");
    //
    //                    bullets.remove(bullet);
    //                    grid.set(curY, curX, new Ground());
    //
    //                }
    //
    //            }
    //
    //            if (bullets.size() > 0) {
    //                game.broadcastState();
    //            }
    //
    //        }
    //
    //    }}).start();
    //
    //}

    // TODO awful, remove asap
    //private void restartGame () {
    //
    //    System.out.println("in Map, restartGame()");
    //
    //    //timer.interrupt();
    //
    //    initMap();
    //
    //    Map<Integer, PlayerInfo> allPlayers = new HashMap<Integer, PlayerInfo>();
    //    for (Integer playerID : players.keySet()) {
    //        allPlayers.put(playerID, players.get(playerID).getPlayerInfo());
    //    }
    //
    //    //players.clear();
    //    //bullets.clear();
    //
    //    for (Integer playerID : allPlayers.keySet()) {
    //        addNewPlayer(playerID, allPlayers.get(playerID));
    //    }
    //
    //    startLevel();
    //
    //}

    // TODO awful, remove asap
    //public void startLevel () {
    //
    //    //levelID = rand.nextInt();
    //
    //    Object[] values = players.values().toArray();
    //    Player randPlayer = (Player) values[rand.nextInt(values.length)];
    //    gameState.setWhoItIs(randPlayer.getPlayerInfo().getUsername());
    //
    //    // for (Player player : players.values()) {
    //    //     if (i == index) {
    //    //         gameState.setWhoItIs(player.getPlayerInfo().getUsername());
    //    //         break;
    //    //     } else {
    //    //         i++;
    //    //     }
    //    // }
    //
    //    //timer = new Thread(new Runnable() {
    //    //    public void run() {
    //    //        try {
    //    //            while (!Thread.currentThread().isInterrupted()) {
    //    //
    //    //                final int thisLevelID = levelID;
    //    //                for (int i = 30; i >= 0; i--) {
    //    //                    if (thisLevelID == levelID) {
    //    //                        gameState.setTimeRemaining(i);
    //    //                        game.broadcastState();
    //    //                    }
    //    //                    Thread.sleep(1000);
    //    //                }
    //    //
    //    //                String whoItIs = gameState.getWhoItIs();
    //    //                for (Player player : players.values()) {
    //    //                    String playerName = player.getPlayerInfo().getUsername();
    //    //                    if (!playerName.equals(whoItIs)) {
    //    //                        int curScore = gameState.getScores().get(playerName);
    //    //                        gameState.updateScore(playerName, curScore + 1);
    //    //                    }
    //    //                }
    //    //
    //    //                restartGame();
    //    //
    //    //            }
    //    //        } catch (InterruptedException e) {
    //    //            Thread.currentThread().interrupt();
    //    //        }
    //    //    }
    //    //});
    //    //timer.start();
    //
    //    // (new Thread () { public void run () {
    //
    //    //     final int thisLevelID = levelID;
    //    //     for (int i = 30; i >= 0; i--) {
    //
    //    //         if (thisLevelID == levelID) {
    //    //             gameState.setTimeRemaining(i);
    //    //             game.broadcastState();
    //    //         }
    //
    //    //         try {
    //    //             this.sleep(1000);
    //    //         } catch (Exception e) {
    //    //         }
    //
    //    //     }
    //
    //    // }}).start();
    //
    //}

}
