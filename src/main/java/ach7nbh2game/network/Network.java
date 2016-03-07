package ach7nbh2game.network;

import ach7nbh2game.network.packets.*;
import ach7nbh2game.main.Constants.*;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import java.util.*;


// Holds things common to the client and the server, ostensibly all objects that will be sent between the two.
public class Network {
    // static public final int port = 54555;
    static public final int port = 18101;

    // Register objects to be sent over the network.
    static public void register (EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(String[].class, 3381);
        kryo.register(ClientAction.class, 3382);
        kryo.register(GameState.class, 3383);
        kryo.register(Map.class, 3384);
        kryo.register(HashMap.class, 3385);
        kryo.register(ArrayList.class, 3386);
        kryo.register(PlayerInfo.class, 3387);
        kryo.register(HashSet.class, 3388);
        kryo.register(Direction.class, 3389);
        kryo.register(Action.class, 3390);
        kryo.register(PlayerState.class, 3391);
        kryo.register(PlayerObservableState.class, 3392);

        kryo.register(RegisterMessage.class, 3370);
        kryo.register(TextMessage.class, 3371);
        kryo.register(StartGame.class, 3372);
        kryo.register(EnterGame.class, 3373);
        kryo.register(UpdateNames.class, 3374);
        kryo.register(ActionMessage.class, 3375);
        kryo.register(DiffMessage.class, 3376);
        kryo.register(CreateLobby.class, 3377);
        kryo.register(ReqLobbies.class, 13378);
        kryo.register(LobbyList.class, 3379);
        kryo.register(JoinLobby.class, 3380);
    }

    static public class RegisterMessage {
        public String name;
        public PlayerInfo pinfo;
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

    static public class ActionMessage {
        public ClientAction action;
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
        public Map<Integer, String> players;
        public Map<Integer, Set<Integer>> lobbyToPlayers;
    }

    static public class JoinLobby {
        public int lobbyID;
    }
}