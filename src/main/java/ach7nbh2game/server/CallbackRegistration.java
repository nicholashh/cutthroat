package ach7nbh2game.server;

import java.util.Objects;

public class CallbackRegistration extends Callback {

    public final int startTime;

    private final Callback callback;

    public CallbackRegistration (int startTimeIn, Callback callbackIn) {
        super(callbackIn);
        startTime = startTimeIn;
        callback = callbackIn;
    }

    @Override
    public String toString () {
        return "CallbackRegistration(" + startTime + "," + callback.toString() + ")";
    }

    @Override
    public int hashCode () {
        return Objects.hash(startTime, callback);
    }

    @Override
    public boolean equals (Object other) {

        if (other == null) {
            return false;

        } else if (this.getClass() != other.getClass()) {
            return false;

        } else {

            CallbackRegistration otherCallback = (CallbackRegistration)other;

            if (this.startTime != otherCallback.startTime) {
                return false;

            } else if (!this.callback.equals(otherCallback.callback)) {
                return false;

            } else {
                return true;
            }

        }

    }

}
