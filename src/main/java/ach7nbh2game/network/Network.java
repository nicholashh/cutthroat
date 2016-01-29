package ach7nbh2game.network;

import ach7nbh2game.server.GameState;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import ach7nbh2game.main.Constants.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


// Holds things common to the client and the server, ostensibly all objects that will be sent between the two.
public class Network {
    static public final int port = 54555;

    // Register objects to be sent over the network.
    static public void register (EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(String[].class, 13381);
        kryo.register(Directions.class, 13382);
        kryo.register(GameState.class, 13383);
        kryo.register(Map.class, 13384);
        kryo.register(HashMap.class, 13385);
        kryo.register(ArrayList.class, 13386);

        kryo.register(RegisterMessage.class, 13370);
        kryo.register(TextMessage.class, 13371);
        kryo.register(StartGame.class, 13372);
        kryo.register(EnterGame.class, 13373);
        kryo.register(UpdateNames.class, 13374);
        kryo.register(MoveMessage.class, 13375);
        kryo.register(DiffMessage.class, 13376);
        kryo.register(CreateLobby.class, 13377);
        kryo.register(ReqLobbies.class, 13378);
        kryo.register(LobbyList.class, 13379);
        kryo.register(JoinLobby.class, 13380);
    }

    static public class RegisterMessage {
        public String name;
    }

    static public class TextMessage {
        public String msg;
    }

    static public class StartGame {
        public int lobbyID;
    }

    static public class EnterGame {
        public int clientID;
    }

    static public class UpdateNames {
        public String[] names;
    }

    static public class MoveMessage {
        public Directions direction;
    }

    static public class DiffMessage {
        public GameState pkt;
    }

    static public class CreateLobby {
        public String name;
    }

    static public class ReqLobbies {
        public String uname;
    }

    static public class LobbyList {
        public Map<Integer, String> lobbies;
    }

    static public class JoinLobby {
        public int lobbyID;
    }
}