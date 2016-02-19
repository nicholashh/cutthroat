package ach7nbh2game.server;

import java.util.Objects;

public class Callback {

    public final int frequency;

    private int numTimesLeft;
    private final Runnable lambda;

    // clone an existing callback
    public Callback (Callback cloneMe) {
        this(cloneMe.frequency, cloneMe.numTimesLeft, cloneMe.lambda);
    }

    // numTimesLeftIn == -1: repeat forever
    public Callback(int frequencyIn, int numTimesLeftIn, Runnable lambdaIn) {
        frequency = frequencyIn;
        numTimesLeft = numTimesLeftIn;
        lambda = lambdaIn;
    }

    // returns if this registration is no longer needed
    public boolean run () {

        // if this callback has more times to be run
        if (numTimesLeft > 0) {
            // run the lambda once
            numTimesLeft--;
            lambda.run();
        }

        // if the caller wants this callback to never self-terminate
        else if (numTimesLeft == -1) {
            // run the lambda once
            lambda.run();
        }

        // return if numTimesLeft just reached 0 (down from 1)
        //Logger.Singleton.log(this, 0, "run: returning: " + (numTimesLeft == 0));
        return numTimesLeft == 0;

    }

    public void cancel() {
        numTimesLeft = 0;
    }

    @Override
    public String toString () {
        return "Callback(" + frequency + "," + numTimesLeft + ")";
    }

    @Override
    public int hashCode () {
        return Objects.hash(frequency, numTimesLeft, lambda);
    }

    @Override
    public boolean equals (Object other) {

        if (other == null) {
            return false;

        } else if (this.getClass() != other.getClass()) {
            return false;

        } else {

            Callback otherCallback = (Callback)other;

            if (this.frequency != otherCallback.frequency) {
                return false;

            } else if (this.numTimesLeft != otherCallback.numTimesLeft) {
                return false;

            } else if (!this.lambda.equals(otherCallback.lambda)) {
                return false;

            } else {
                return true;
            }

        }

    }

}
