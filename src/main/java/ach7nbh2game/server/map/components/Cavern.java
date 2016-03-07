package ach7nbh2game.server.map.components;

import ach7nbh2game.main.Constants;

import java.util.ArrayList;
import java.util.Random;

public class Cavern {

    private Constants.CavernSize size;
    private ArrayList<CavernWall> walls = new ArrayList<>();
    private ArrayList<Constants.Item> contains = new ArrayList<>();

    private Random rand = new Random();

    public Cavern(Constants.CavernSize sizeIn) {
        size = sizeIn;
    }

    public void init() {
        if (size == Constants.CavernSize.LARGE) {
            contains.add(Constants.Item.GUN2);
            contains.add(Constants.Item.PICK3);

        } else if (size == Constants.CavernSize.MEDIUM) {
            if (rand.nextDouble() < 0.5) {
                contains.add(Constants.Item.GUN2);
            } else {
                contains.add(Constants.Item.PICK3);
            }
        }

        assert(walls.size() >= contains.size());

        for (int i = 0; i < walls.size()*0.2-contains.size(); i++) {
            contains.add(Constants.Item.BULLET1);
        }

        for (int i = 0; i < contains.size(); i++) {
            boolean placedItem = false;
            while (!placedItem) {
                int wallIndex = rand.nextInt(walls.size());
                if (walls.get(wallIndex).getItem() == null) {
                    walls.get(wallIndex).setItem(contains.get(i));
                    placedItem = true;
                }
            }
        }
    }

    public void addWall(CavernWall wall) {
        walls.add(wall);
    }

    public void discovered() {
        for (CavernWall wall : walls) {
            wall.setVisible();
        }
    }
}
