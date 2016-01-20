package ach7nbh2game.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

// Holds things common to the client and the server, ostensibly all objects that will be sent between the two.
public class Network {
    static public final int port = 54555;

    // Register objects to be sent over the network.
    static public void register (EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(JoinMessage.class);
        kryo.register(String[].class);
        kryo.register(UpdateNames.class);
        kryo.register(DiffMessage.class);
    }

    static public class JoinMessage {
        public String name;
    }

    static public class UpdateNames {
        public String[] names;
    }

    static public class CmdMessage {
        public String command;
        public long tick;
    }

    static public class DiffMessage {
        // maybe we should have a GameState object that has the next frame and game variables
        public long tick;
    }
}