package ach7nbh2game.server.map.components;

public interface IMapComponent {

    int getX ();
    int getY ();
    void setX (int xIn);
    void setY (int yIn);

    int getHealth ();
    void applyDamage (int damage, Client killer);
    boolean isDead ();
    boolean canDie ();

    int getMapChar ();

}
