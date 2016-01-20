package ach7nbh2game.examples.kryoChat;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

// This class is a place to keep things common to both the client and server.
public class Network {
    static public final int port = 54555;

    // This registers objects that are going to be sent over the network.
    static public void register (EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(JoinMessage.class);
        kryo.register(String[].class);
        kryo.register(UpdateNames.class);
        kryo.register(CmdMessage.class);
    }

    static public class JoinMessage {
        public String name;
        public String ip;
        public String conn;
    }

    static public class UpdateNames {
        public String[] names;
    }

    static public class CmdMessage {
        public String command;
        public long tick;
    }

    static public class DiffMessage {
        // maybe we should have a 'GameState' object with a terminal and all the game variables
        public long tick;
    }
}