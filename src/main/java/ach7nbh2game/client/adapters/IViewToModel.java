package ach7nbh2game.client.adapters;

import ach7nbh2game.network.packets.ClientAction;

public interface IViewToModel {
    void performAction (ClientAction actionIn);
    void selectUp();
    void selectDown();
}
