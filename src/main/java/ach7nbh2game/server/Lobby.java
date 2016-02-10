//package ach7nbh2game.server;
//
//import ach7nbh2game.client.PlayerInfo;
//import ach7nbh2game.main.Constants;
//
//import java.util.HashMap;
//
//public class Lobby extends APlayerContainer {
//
//    // TODO state design pattern for lobbies -> games
//    private ServerModel server;
//
//    private String name;
//
//    private int gameMapHeight;
//    private int gameMapWidth;
//
//    public Lobby (ServerModel serverIn, String nameIn) {
//
//        server = serverIn;
//
//        name = nameIn;
//        playerInfo = new HashMap<Integer, PlayerInfo>();
//        gameMapHeight = Constants.mapHeight;
//        gameMapWidth = Constants.mapWidth;
//
//    }
//
//    public void join (int newID, PlayerInfo info) {
//
//        playerInfo.put(newID, info);
//
//    }
//
//    public GameOLD startGame () {
//
//        return new GameOLD(server, playerInfo, gameMapHeight, gameMapWidth);
//
//    }
//
//    public String getName () {
//
//        return name;
//
//    }
//
//}
