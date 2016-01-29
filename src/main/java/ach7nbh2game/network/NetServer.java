package ach7nbh2game.network;

// TODO: These were in the example, just to open a window with "the server is running"
// idk how you want to handle the server's text GUI
import ach7nbh2game.main.Constants.*;
import ach7nbh2game.network.adapters.IClientToServer;
import ach7nbh2game.server.GameState;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ach7nbh2game.network.Network.*;
import com.esotericsoftware.minlog.Log;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NetServer {
    Server server;
    IClientToServer adapter;
    HashMap<Integer, String> connNames;

    public NetServer() throws IOException {
        server = new Server() {
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
                    // Send a "connected" message to everyone except the new client.
                    // TextMessage nameMsg = new TextMessage();
                    // nameMsg.msg = name + " connected.";
                    // server.sendToAllExceptTCP(connection.getID(), nameMsg);
                    // Send everyone a new list of connection names.
                    // updateNames();
                    return;
                }

                if (object instanceof StartGame) {
                    if (connection.name == null) {
                        return;
                    }
                    StartGame start = (StartGame) object;
                    adapter.startGame(start.lobbyID);
                    return;
                }

                if (object instanceof MoveMessage) {
                    // Ignore the object if a client tries to chat before registering a name.
                    if (connection.name == null) {
                        return;
                    }
                    MoveMessage mvMsg = (MoveMessage) object;
                    // Ignore the object if the chat message is invalid.
                    Directions message = mvMsg.direction;
                    if (message == null) {
                        return;
                    }
                    adapter.move(connection.getID(), message);
                    return;
                }

                if (object instanceof CreateLobby) {
                    if (connection.name == null) {
                        return;
                    }
                    CreateLobby msg = (CreateLobby) object;
                    adapter.createNewLobby(connection.getID(), msg.name);
                    return;
                }

                if (object instanceof ReqLobbies) {
                    if (connection.name == null) {
                        return;
                    }
                    adapter.requestLobbies(connection.getID());
                    return;
                }

                if (object instanceof JoinLobby) {
                    if (connection.name == null) {
                        return;
                    }
                    JoinLobby msg = (JoinLobby) object;
                    adapter.joinLobby(connection.getID(), msg.lobbyID);
                    return;
                }
            }

            public void disconnected (Connection c) {
                GameConnection connection = (GameConnection)c;
                if (connection.name != null) {
                    // Announce to everyone that someone (with a registered name) has left.
                    // TextMessage tmsg = new TextMessage();
                    // tmsg.msg = connection.name + " disconnected.";
                    // server.sendToAllTCP(tmsg);
                    // updateNames();
                }
            }
        });
        server.bind(Network.port);
        server.start();
        connNames = new HashMap<Integer, String>();

        // Open a window to provide an easy way to stop the server.
        JFrame frame = new JFrame("Chat Server");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosed (WindowEvent evt) {
                server.stop();
            }
        });
        frame.getContentPane().add(new JLabel("Close to stop the chat server."));
        frame.setSize(320, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    void updateNames () {
        // Collect the names for each connection.
        Connection[] connections = server.getConnections();
        ArrayList names = new ArrayList(connections.length);
        for (int i = connections.length - 1; i >= 0; i--) {
            GameConnection connection = (GameConnection)connections[i];
            names.add(connection.name);
        }

        // Send the names to everyone.
        // UpdateNames updateNames = new UpdateNames();
        // updateNames.names = (String[])names.toArray(new String[names.size()]);
        // server.sendToAllTCP(updateNames);
    }

    public void installAdapter(IClientToServer newadapter) {
        adapter = newadapter;
    }

    public void enterGame(int clientID) {
        EnterGame start = new EnterGame();
        start.clientID = clientID;
        server.sendToTCP(clientID, start);
    }

    public void updateGameState(int clientID, ArrayList<ArrayList<Integer>> frame) {
        GameState pkt = new GameState();
        pkt.setFrame(frame);
        DiffMessage diffMsg = new DiffMessage();
        diffMsg.pkt = pkt;
        server.sendToTCP(clientID, diffMsg);
    }

    public void announceLobbies(int clientID, Map<Integer, String> lobbies) {
        LobbyList listMsg = new LobbyList();
        listMsg.lobbies = lobbies;
        server.sendToTCP(clientID, listMsg);
    }

    // This holds per connection state.
    static class GameConnection extends Connection {
        public String name;
    }

    public static void main (String[] args) throws IOException {
        Log.set(Log.LEVEL_DEBUG);
        new NetServer();
    }
}