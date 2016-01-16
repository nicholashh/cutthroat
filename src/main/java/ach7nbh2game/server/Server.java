package ach7nbh2game.server;

import ach7nbh2game.server.map.Map;

import java.util.ArrayList;

public class Server {
	
	private Map map;

    public Server () {

        map = new Map(20, 20);

    }

    public ArrayList<ArrayList<Integer>> getMapView (int x, int y, int height, int width) {

        return map.getMapView(x, y, height, width);

    }

}
