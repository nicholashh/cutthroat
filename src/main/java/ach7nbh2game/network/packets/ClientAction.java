package ach7nbh2game.network.packets;

import ach7nbh2game.main.Constants.*;

public class ClientAction {

    public Direction direction;

    public Action action;

    public ClientAction () {
    }

    public void setDirection(Direction directionIn) {
        direction = directionIn;
    }

    public void setAction(Action actionIn) {
        action = actionIn;
    }

    public Direction getDirection () {
        return direction;
    }

    public Action getAction() {
        return action;
    }

}
