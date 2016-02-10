package ach7nbh2game.network.packets;

import ach7nbh2game.main.Constants.Directions;

public class ClientAction {

    public Directions direction;

    public ClientAction (Directions directionIn) {
        direction = directionIn;
    }

    public Directions getDirection () {
        return direction;
    }

}
