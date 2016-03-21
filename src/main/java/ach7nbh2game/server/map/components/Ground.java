package ach7nbh2game.server.map.components;

public class Ground extends AMapComponent {

    public Ground () {
        super("Ground");
    }

    public int getMapChar () {
        return ' ';
    }

    // this map component can't really be damaged
    public int getHealth () { return 0; }
    public void applyDamage (int damage, Client killer) {}
    public boolean canDie () {return false;}

}
