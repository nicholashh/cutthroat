package ach7nbh2game.util.id;

import java.util.Objects;

public abstract class ID
// implements Comparable<ID>
{

    public final int value;

    public ID (int valueIn) {
        value = valueIn;
    }

    //public int compareTo (ID other) {
    //    return (new Integer(value)).compareTo(other.value);
    //}

    @Override
    public String toString () {
        return "" + value;
    }

    @Override
    public int hashCode () {
        return Objects.hash(value);
    }

    @Override
    public boolean equals (Object other) {

        if (other == null) {
            return false;

        } else if (this.getClass() != other.getClass()) {
            return false;

        } else if (this.value != ((ID)other).value) {
            return false;

        } else {
            return true;
        }

    }

}
