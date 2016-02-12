package ach7nbh2game.client.adapters;

import ach7nbh2game.main.Constants.*;
import ach7nbh2game.network.packets.ClientAction;

public interface IViewToModel {
    void action(ClientAction actionIn);
}
