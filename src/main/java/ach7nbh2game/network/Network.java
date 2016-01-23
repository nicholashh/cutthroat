package ach7nbh2game.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import ach7nbh2game.main.Constants.*;


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

    static public class TextMessage {
        public String msg;
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
}