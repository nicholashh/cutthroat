package ach7nbh2game.server;

import ach7nbh2game.client.PlayerInfo;

import java.util.Map;

abstract class APlayerContainer {

    protected Map<Integer, PlayerInfo> playerInfo;

    public Map<Integer, PlayerInfo> getPlayerInfo () {

        return playerInfo;

    }

}
