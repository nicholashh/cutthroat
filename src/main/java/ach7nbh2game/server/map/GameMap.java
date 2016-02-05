package ach7nbh2game.server.map;

import ach7nbh2game.client.PlayerInfo;
import ach7nbh2game.main.Constants;
import ach7nbh2game.main.Constants.Directions;
import ach7nbh2game.server.Game;
import ach7nbh2game.server.GameState;
import ach7nbh2game.server.map.components.*;
import com.googlecode.blacken.grid.Grid;

import java.util.*;

public class GameMap {

    private Game game;

    private Grid<IMapComponent> grid;
    private int height;
    private int width;

    private Map<Integer, Player> players;
    private GameState gameState;

    private Random rand;

    private int levelID;

    private Thread timer;

    public GameMap (Game gameIn, int heightIn, int widthIn) {

        game = gameIn;
        height = heightIn;
        width = widthIn;

        grid = new Grid<IMapComponent>(new Ground(), height, width);
        players = new HashMap<Integer, Player>();
        gameState = new GameState();
        rand = new Random();

        initMap();
        moveBullets(); // TODO make more general

    }

    public GameState getGameState () {
        return gameState;
    }

    private void initMap () {

        grid.clear();
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

    public void addNewPlayer (int playerID, PlayerInfo info) {

        while (true) {

            int y = rand.nextInt(height);
            int x = rand.nextInt(width);

            if (grid.get(y, x) instanceof Ground) {

                Player newPlayer = new Player(playerID, info, y, x);
                players.put(playerID, newPlayer);
                grid.set(y, x, newPlayer);

                String username = newPlayer.getPlayerInfo().getUsername();
                if (!gameState.getScores().containsKey(username)) {
                    gameState.updateScore(username, 0);
                }

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

    private Set<Bullet> bullets = new HashSet<Bullet>();

    private void moveBullets () {

        (new Thread() { public void run() {

            while (true) {

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // TODO need to think about how to deal with java.util.ConcurrentModificationException
                Set<Bullet> bulletsCopy = new HashSet<Bullet>(bullets);
                for (Bullet bullet : bulletsCopy) {

                    System.out.println("found one bullet");

                    // TODO don't repeat this logic
                    int curX = bullet.getX();
                    int curY = bullet.getY();
                    int newX = curX;
                    int newY = curY;
                    switch (bullet.getDirection()) {
                        case GUN_UP:
                            newX = curX;
                            newY = curY - 1;
                            break;
                        case GUN_DOWN:
                            newX = curX;
                            newY = curY + 1;
                            break;
                        case GUN_LEFT:
                            newX = curX - 1;
                            newY = curY;
                            break;
                        case GUN_RIGHT:
                            newX = curX + 1;
                            newY = curY;
                            break;
                    }

                    System.out.println("cur: x = " + curX + ", y = " + curY);
                    System.out.println("new: x = " + newX + ", y = " + newY);

                    if (newX >= 0 && newY >= 0 && newX < width && newY < height) {

                        IMapComponent thing = grid.get(newY, newX);

                        if (thing instanceof Ground) {

                            System.out.println("thing instanceof Ground");

                            bullet.setY(newY);
                            bullet.setX(newX);
                            grid.set(newY, newX, bullet);
                            grid.set(curY, curX, new Ground());

                        } else if (thing instanceof Player) {

                            System.out.println("thing instanceof Player");

                            String bulletPlayerName = players.get(bullet.getOwner().getID()).getPlayerInfo().getUsername();
                            String otherPlayerName = players.get(((Player) thing).getID()).getPlayerInfo().getUsername();

                            int curScore = gameState.getScores().get(bulletPlayerName);
                            gameState.updateScore(bulletPlayerName, curScore + 1);
                            curScore = gameState.getScores().get(otherPlayerName);
                            gameState.updateScore(otherPlayerName, curScore - 1);

                            restartGame();

                        } else if (thing instanceof Wall) {

                            System.out.println("thing instanceof Wall");

                            bullets.remove(bullet);
                            grid.set(curY, curX, new Ground());

                        }

                    } else {

                        System.out.println("went off the screen");

                        bullets.remove(bullet);
                        grid.set(curY, curX, new Ground());

                    }

                }

                if (bullets.size() > 0) {
                    game.broadcastState();
                }

            }

        }}).start();

    }

    public void move (int playerID, Directions direction) {

        if (players.containsKey(playerID)) {

            Player player = players.get(playerID);
            int curX = player.getX();
            int curY = player.getY();

            boolean isGun = false;
            int newX = curX;
            int newY = curY;
            switch (direction) {
                case GUN_UP:
                    isGun = true;
                case UP:
                    newX = curX;
                    newY = curY - 1;
                    break;
                case GUN_DOWN:
                    isGun = true;
                case DOWN:
                    newX = curX;
                    newY = curY + 1;
                    break;
                case GUN_LEFT:
                    isGun = true;
                case LEFT:
                    newX = curX - 1;
                    newY = curY;
                    break;
                case GUN_RIGHT:
                    isGun = true;
                case RIGHT:
                    newX = curX + 1;
                    newY = curY;
                    break;
            }

            if (newX >= 0 && newY >= 0 && newX < width && newY < height) {
                IMapComponent thing = grid.get(newY, newX);

                if (thing instanceof Ground) {

                    if (isGun) {

                        Bullet newBullet = new Bullet(newY, newX, direction, player);
                        grid.set(newY, newX, newBullet);
                        bullets.add(newBullet);

                    } else {

                        player.setY(newY);
                        player.setX(newX);
                        grid.set(newY, newX, player);
                        grid.set(curY, curX, new Ground());

                    }

                } else if (thing instanceof Player) {
                    // TODO don't use instanceof

                    if (!isGun) {

                        String myPlayerName = players.get(playerID).getPlayerInfo().getUsername();
                        String otherPlayerName = players.get(((Player) thing).getID()).getPlayerInfo().getUsername();

                        String whoItIs = gameState.getWhoItIs();
                        if (myPlayerName.equals(whoItIs) ||
                                otherPlayerName.equals(whoItIs)) {

                            int curScore = gameState.getScores().get(whoItIs);
                            gameState.updateScore(whoItIs, curScore + 1);
                            // TODO this should use a teamID instead of a string

                            restartGame();

                        }

                    } // TODO if is gun (should be fixed automatically with dispatchers)

                }

            }

        }

    }

    private void restartGame () {

        System.out.println("in Map, restartGame()");

        timer.interrupt();

        initMap();

        Map<Integer, PlayerInfo> allPlayers = new HashMap<Integer, PlayerInfo>();
        for (Integer playerID : players.keySet()) {
            allPlayers.put(playerID, players.get(playerID).getPlayerInfo());
        }

        players.clear();
        bullets.clear();

        for (Integer playerID : allPlayers.keySet()) {
            addNewPlayer(playerID, allPlayers.get(playerID));
        }

        startLevel();

    }

    public void startLevel () {

        levelID = rand.nextInt();

        Object[] values = players.values().toArray();
        Player randPlayer = (Player) values[rand.nextInt(values.length)];
        gameState.setWhoItIs(randPlayer.getPlayerInfo().getUsername());
        // for (Player player : players.values()) {
        //     if (i == index) {
        //         gameState.setWhoItIs(player.getPlayerInfo().getUsername());
        //         break;
        //     } else {
        //         i++;
        //     }
        // }

        timer = new Thread(new Runnable() {
            public void run() {
                try {
                    while (!Thread.currentThread().isInterrupted()) {

                        final int thisLevelID = levelID;
                        for (int i = 30; i >= 0; i--) {
                            if (thisLevelID == levelID) {
                                gameState.setTimeRemaining(i);
                                game.broadcastState();
                            }
                            Thread.sleep(1000);
                        }

                        String whoItIs = gameState.getWhoItIs();
                        for (Player player : players.values()) {
                            String playerName = player.getPlayerInfo().getUsername();
                            if (!playerName.equals(whoItIs)) {
                                int curScore = gameState.getScores().get(playerName);
                                gameState.updateScore(playerName, curScore + 1);
                            }
                        }

                        restartGame();

                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        timer.start();

        // (new Thread () { public void run () {

        //     final int thisLevelID = levelID;
        //     for (int i = 30; i >= 0; i--) {

        //         if (thisLevelID == levelID) {
        //             gameState.setTimeRemaining(i);
        //             game.broadcastState();
        //         }

        //         try {
        //             this.sleep(1000);
        //         } catch (Exception e) {
        //         }

        //     }

        // }}).start();

    }

}
