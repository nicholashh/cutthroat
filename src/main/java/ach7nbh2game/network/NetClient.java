package ach7nbh2game.network;

import ach7nbh2game.main.Constants;
import ach7nbh2game.network.Network.*;
import ach7nbh2game.network.adapters.IServerToClient;
import ach7nbh2game.network.packets.ClientAction;
import ach7nbh2game.network.packets.PlayerInfo;
import ach7nbh2game.util.Logger;
import ach7nbh2game.util.id.ClientID;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

public class NetClient {

    private Client client;
    private String name;
    private String host;
    private IServerToClient adapter;
    private boolean isConnected = false;

    public NetClient (IServerToClient adapterIn) {

        System.out.println("making new NetClient...");

        adapter = adapterIn;

    }

    public void start () {

        System.out.println("starting the NetClient!");

    }

    public boolean isConnected() {
        return isConnected;
    }

    public void connectTo (String hostIn, final PlayerInfo info) throws IOException {

        if (client != null)
            client.stop();
        client = null;

        client = new Client(Constants.bufferSize, Constants.bufferSize);
        client.start();
        host = hostIn;
        name = info.getUsername();

        // For consistency, the classes to be sent over the network are
        // registered by the same method for both the client and server.
        Network.register(client);

        client.addListener(new Listener() {
            public void connected (Connection connection) {
                RegisterMessage regName = new RegisterMessage();
                regName.name = name;
                regName.pinfo = info;
                client.sendTCP(regName);
                info.setID(connection.getID());
            }

            public void received (Connection connection, Object object) {
                if (object instanceof UpdateNames) {
                    UpdateNames updateNames = (UpdateNames)object;
                    return;
                }

                if (object instanceof DiffMessage) {
                    Logger.Singleton.log(NetClient.this, 0, "received: DiffMessage");
                    DiffMessage diffMsg = (DiffMessage) object;
                    adapter.updateGameState(client.getID(), diffMsg.pkt);
                    return;
                }

                if (object instanceof EnterGame) {
                    Logger.Singleton.log(NetClient.this, 0, "received: EnterGame");
                    adapter.enterGame(connection.getID());
                }

                if (object instanceof LobbyList) {
                    Logger.Singleton.log(NetClient.this, 0, "received: LobbyList");
                    LobbyList msg = (LobbyList) object;
                    adapter.announceLobbies(client.getID(),
                            msg.lobbies, msg.players, msg.lobbyToPlayers);
                }

                if (object instanceof EndGame) {
                    EndGame msg = (EndGame) object;
                    adapter.theWinnerIs(msg.client);
                }
            }

            public void disconnected (Connection connection) {
                //TODO
            }

        });

        // just let this throw the exception if the connection fails
        client.connect(5000, host, Network.port);

        // will only happen if no exception is thrown
        isConnected = true;

    }

    public void installAdapter(IServerToClient newadapter) {
        adapter = newadapter;
    }

    public void createLobby(String name) {
        Logger.Singleton.log(NetClient.this, 0, "sending: CreateLobby");
        CreateLobby clobby = new CreateLobby();
        clobby.name = name;
        client.sendTCP(clobby);
    }

    public void reqLobbies() {
        Logger.Singleton.log(NetClient.this, 0, "sending: ReqLobbies");
        ReqLobbies req = new ReqLobbies();
        req.uname = name;
        client.sendTCP(req);
    }

    public void joinLobby(int lobbyID) {
        Logger.Singleton.log(NetClient.this, 0, "sending: JoinLobby");
        JoinLobby join = new JoinLobby();
        join.lobbyID = lobbyID;
        client.sendTCP(join);
    }

    public void startGame(int lobbyID) {
        Logger.Singleton.log(NetClient.this, 0, "sending: StartGame");
        StartGame start = new StartGame();
        start.lobbyID = lobbyID;
        client.sendTCP(start);
    }

    public void action(ClientAction actionIn) {
        Logger.Singleton.log(NetClient.this, 0, "sending: ActionMessage");
        ActionMessage actMsg = new ActionMessage();
        actMsg.action = actionIn;
        client.sendTCP(actMsg);
    }

    @Override
    public String toString () {
        return "NetClient";
    }

}
