package ach7nbh2game.network;

import java.io.IOException;

import ach7nbh2game.client.PlayerInfo;
import ach7nbh2game.main.Constants.*;
import ach7nbh2game.network.adapters.IServerToClient;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ach7nbh2game.network.Network.*;
import com.esotericsoftware.minlog.Log;

public class NetClient {

    Client client;
    String name;
    String host;
    IServerToClient adapter;

    public NetClient (String hostn, PlayerInfo info) {
        client = new Client(16384, 16384);
        client.start();
        host = hostn;
        name = info.getUsername();

        // For consistency, the classes to be sent over the network are
        // registered by the same method for both the client and server.
        Network.register(client);

        client.addListener(new Listener() {
            public void connected (Connection connection) {
                RegisterMessage regName = new RegisterMessage();
                regName.name = name;
                client.sendTCP(regName);
            }

            public void received (Connection connection, Object object) {
                if (object instanceof UpdateNames) {
                    UpdateNames updateNames = (UpdateNames)object;
                    return;
                }

                if (object instanceof DiffMessage) {
                    DiffMessage diffMsg = (DiffMessage) object;
                    adapter.updateGameState(client.getID(), diffMsg.pkt);
                    return;
                }

                if (object instanceof EnterGame) {
                    adapter.enterGame(connection.getID());
                }

                if (object instanceof LobbyList) {
                    LobbyList msg = (LobbyList) object;
                    adapter.announceLobbies(client.getID(), msg.lobbies);
                }
            }

            public void disconnected (Connection connection) {
                //TODO
            }
        });

        // Request the host from the user.
        // String input = (String)JOptionPane.showInputDialog(null, "Host:", "Connect to chat server", JOptionPane
        //         .QUESTION_MESSAGE,
        //         null, null, "localhost");
        // if (input == null || input.trim().length() == 0) System.exit(1);
        // final String host = input.trim();

        // Request the user's name.
        // input = (String)JOptionPane.showInputDialog(null, "Name:", "Connect to chat server", JOptionPane
        //         .QUESTION_MESSAGE, null,
        //         null, "Test");
        // if (input == null || input.trim().length() == 0) System.exit(1);
        // name = input.trim();

        // We'll do the connect on a new thread so the ChatFrame can show a progress bar.
        // Connecting to localhost is usually so fast you won't see the progress bar.
        // new Thread("Connect") {
        //     public void run () {
                try {
                    client.connect(5000, host, Network.port);
                    // Server communication after connection can go here, or in Listener#connected().
                } catch (IOException ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
        //     }
        // }.start();
    }

    public void installAdapter(IServerToClient newadapter) {
        adapter = newadapter;
    }

    public void createLobby(String name) {
        CreateLobby clobby = new CreateLobby();
        clobby.name = name;
        client.sendTCP(clobby);
    }

    public void reqLobbies() {
        ReqLobbies req = new ReqLobbies();
        req.uname = name;
        client.sendTCP(req);
    }

    public void joinLobby(int lobbyID, PlayerInfo info) {
        JoinLobby join = new JoinLobby();
        join.lobbyID = lobbyID;
        join.info = info;
        client.sendTCP(join);
    }

    public void startGame(int lobbyID) {
        StartGame start = new StartGame();
        start.lobbyID = lobbyID;
        client.sendTCP(start);
    }

    public void move(Directions direction) {
        MoveMessage mvMsg = new MoveMessage();
        mvMsg.direction = direction;
        client.sendTCP(mvMsg);
    }

    public static void main (String[] args) {
        Log.set(Log.LEVEL_DEBUG);
        PlayerInfo info = new PlayerInfo();
        info.setUsername("Test");
        new NetClient("localhost", info);
    }
}
