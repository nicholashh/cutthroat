package ach7nbh2game.server;

public class CallbackRequest {

    public final int frequency;

    private int numTimesLeft;
    private final Runnable lambda;

    public CallbackRequest (Runnable lambda) {
        this(1, 1, lambda);
    }

    public CallbackRequest (int frequencyIn, int numTimesLeftIn, Runnable lambdaIn) {
        frequency = frequencyIn;
        numTimesLeft = numTimesLeftIn;
        lambda = lambdaIn;
    }

    // returns if this registration is no longer needed
    public boolean run () {

        if (numTimesLeft-- > 0) {
            lambda.run();
        }

        return numTimesLeft <= 0;

    }

}
