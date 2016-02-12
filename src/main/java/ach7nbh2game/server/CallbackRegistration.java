package ach7nbh2game.server;

import java.util.Objects;

public class CallbackRegistration {

    public final int startTime;
    public final int frequency;

    private final CallbackRequest request;

    public CallbackRegistration (int startTimeIn, CallbackRequest requestIn) {
        startTime = startTimeIn;
        request = requestIn;
        frequency = request.frequency;
    }

    // returns if this registration is no longer needed
    public boolean run () {
        return request.run();
    }

    @Override
    public int hashCode () {
        return Objects.hash(startTime, frequency, request);
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

            } else if (this.frequency != otherCallback.frequency) {
                return false;

            } else if (this.request != otherCallback.request) {
                return false;

            } else {
                return true;
            }

        }

    }

}
