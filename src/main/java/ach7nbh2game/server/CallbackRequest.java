package ach7nbh2game.server;

public class CallbackRequest {

    public final int frequency;

    // TODO can we make this better?
    private int numTimesLeft;

    private final Runnable lambda;

    public CallbackRequest (Runnable lambda) {
        this(1, 1, lambda);
    }

    // if numTimesLeftIn is -1, that means infinity
    public CallbackRequest (int frequencyIn, int numTimesLeftIn, Runnable lambdaIn) {
        frequency = frequencyIn;
        numTimesLeft = numTimesLeftIn;
        lambda = lambdaIn;
    }

    // returns if this registration is no longer needed
    public boolean run () {

        if (numTimesLeft > 0) {
            numTimesLeft--;
            lambda.run();
        } else if (numTimesLeft == -1) {
            lambda.run();
        }

        return numTimesLeft == 0;

    }

    public void cancel() {
        numTimesLeft = 0;
    }

}
