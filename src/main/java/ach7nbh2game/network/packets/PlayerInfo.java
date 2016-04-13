package ach7nbh2game.network.packets;

public class PlayerInfo {

    private String username;
    private int icon;
    private int id;


    public void setUsername (String usernameIn) {
        username = usernameIn;
    }

    public String getUsername () {
        return username;
    }

    public void setIcon (int iconIn) {
        icon = iconIn;
    }

    public int getIcon () {
        return icon;
    }

    public void setID (int idIn) {
        id = idIn;
    }

    public int getID () {
        return id;
    }
}
