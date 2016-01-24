package ach7nbh2game.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import ach7nbh2game.main.Constants.*;

import java.util.Map;


// Holds things common to the client and the server, ostensibly all objects that will be sent between the two.
public class Network {
    static public final int port = 54555;

    // Register objects to be sent over the network.
    static public void register (EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(RegisterMessage.class);
        kryo.register(TextMessage.class);
        kryo.register(StartGame.class);
        kryo.register(EnterGame.class);
        kryo.register(String[].class);
        kryo.register(UpdateNames.class);
        kryo.register(MoveMessage.class);
        kryo.register(DiffMessage.class);
        kryo.register(CreateLobby.class);
        kryo.register(ReqLobbies.class);
        kryo.register(LobbyList.class);
        kryo.register(JoinLobby.class);
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
        public StatePacket pkt;
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