package ach7nbh2game.network;

import ach7nbh2game.main.Constants;
import ach7nbh2game.network.Network.*;
import ach7nbh2game.network.adapters.IClientToServer;
import ach7nbh2game.network.packets.ClientAction;
import ach7nbh2game.network.packets.GameState;
import ach7nbh2game.util.Logger;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NetServer {

    Server server;
    IClientToServer adapter;
    HashMap<Integer, String> connNames;

    public NetServer (IClientToServer adapterIn) {

        System.out.println("making new NetServer...");

        adapter = adapterIn;

    }

    public void start () throws IOException {

        System.out.println("starting the NetServer!");

        server = new Server(Constants.bufferSize, Constants.bufferSize) {
            protected Connection newConnection () {
                // By providing our own connection implementation, we can store per
                // connection state without a connection ID to state look up.
                return new GameConnection();
            }
        };

        // For consistency, the classes to be sent over the network are
        // registered by the same method for both the client and server.
        Network.register(server);

        server.addListener(new Listener() {
            public void received (Connection c, Object object) {
                // We know all connections for this server are actually ChatConnections.
                GameConnection connection = (GameConnection)c;

                if (object instanceof RegisterMessage) {
                    // Ignore the object if a client has already registered a name.
                    if (connection.name != null) {
                        return;
                    }
                    // Ignore the object if the name is invalid.
                    String name = ((RegisterMessage)object).name;
                    if (name == null) {
                        return;
                    }
                    name = name.trim();
                    if (name.length() == 0) {
                        return;
                    }
                    // Store the name on the connection.
                    connection.name = name;
                    RegisterMessage msg = (RegisterMessage) object;
                    try {
                        adapter.connectTo(connection.getID(), "", msg.pinfo);
                    } catch (IOException e) {
                        System.out.println("Error in NetServer: adapter.connectTo()");
                        e.printStackTrace();
                    }
                    return;
                }

                if (object instanceof StartGame) {
                    Logger.Singleton.log(NetServer.this, 0, "received: StartGame");
                    if (connection.name == null) {
                        return;
                    }
                    //StartGame start = (StartGame) object;
                    // TODO I changed this so that we just drop the lobbyID
                    // but obviously this should be made more efficient
                    adapter.startGame(connection.getID());
                    return;
                }

                if (object instanceof ActionMessage) {
                    Logger.Singleton.log(NetServer.this, 0, "received: ActionMessage");
                    // Ignore the object if a client tries to chat before registering a name.
                    if (connection.name == null) {
                        return;
                    }
                    ActionMessage actMsg = (ActionMessage) object;
                    // Ignore the object if the chat message is invalid.
                    ClientAction action = actMsg.action;
                    if (action == null) {
                        return;
                    }
                    adapter.performAction(connection.getID(), action);
                    return;
                }

                if (object instanceof CreateLobby) {
                    Logger.Singleton.log(NetServer.this, 0, "received: CreateLobby");
                    if (connection.name == null) {
                        return;
                    }
                    CreateLobby msg = (CreateLobby) object;
                    adapter.createNewLobby(connection.getID(), msg.name);
                    return;
                }

                if (object instanceof ReqLobbies) {
                    Logger.Singleton.log(NetServer.this, 0, "received: ReqLobbies");
                    if (connection.name == null) {
                        return;
                    }
                    adapter.requestLobbies(connection.getID());
                    return;
                }

                if (object instanceof JoinLobby) {
                    Logger.Singleton.log(NetServer.this, 0, "received: JoinLobby");
                    if (connection.name == null) {
                        return;
                    }
                    JoinLobby msg = (JoinLobby) object;
                    adapter.joinLobby(connection.getID(), msg.lobbyID);
                    return;
                }
            }

            public void disconnected (Connection c) {}

        });
        server.bind(Network.port);
        server.start();
        connNames = new HashMap<>();

    }

    public void enterGame(int clientID) {
        Logger.Singleton.log(this, 0, "sending: EnterGame");
        EnterGame start = new EnterGame();
        start.clientID = clientID;
        server.sendToTCP(clientID, start);
    }

    public void updateGameState(int clientID, GameState state) {
        Logger.Singleton.log(this, 0, "sending: DiffMessage");
        DiffMessage diffMsg = new DiffMessage();
        diffMsg.pkt = state;
        server.sendToTCP(clientID, diffMsg);
    }

    public void announceLobbies(int clientID,
            Map<Integer, String> lobbies,
            Map<Integer, String> players,
            Map<Integer, Set<Integer>> lobbyToPlayers) {
        Logger.Singleton.log(this, 0, "sending: LobbyList");
        LobbyList listMsg = new LobbyList();
        listMsg.lobbies = lobbies;
        listMsg.players = players;
        listMsg.lobbyToPlayers = lobbyToPlayers;
        server.sendToTCP(clientID, listMsg);
    }

    // This holds per connection state.
    static class GameConnection extends Connection {
        public String name;
    }

    @Override
    public String toString () {
        return "NetServer";
    }

}
