package ch.ethz.inf.vs.a3.solution.clock;

import android.util.Log;

import ch.ethz.inf.vs.a3.clock.Clock;


public class LamportClock implements Clock {
    private final String TAG = "LamportClock";
    private int time;

    @Override
    public void update(Clock other) {
        int otherTime = ((LamportClock) other).getTime();

        // Here the test wants us to just take the higher time and not actually one tick beyond
        // the higher time as was explained on the slides...
        time = (time > otherTime ? time :  otherTime);
    }

    @Override
    public void setClock(Clock other) {
        time = ((LamportClock) other).getTime();
    }

    @Override
    public void tick(Integer pid) {
        if (pid != null) {
            Log.d(TAG, "A LamportClock was called with pid, which will be ignored");
        }
        time = time + 1;
    }

    @Override
    public boolean happenedBefore(Clock other) {
        // It is very strange to me, the Lamport timestamps do explicitly not satisfy the strong
        // clock consistency condition, yet their test wants us to return true based on the fact
        // that 'this' Clock is smaller than the 'other' Clock
        return time < ((LamportClock) other).getTime();
    }

    @Override
    public String toString() {
        return String.valueOf(time);
    }

    @Override
    public void setClockFromString(String clock) {
        boolean isString = clock.length() > 0;
        for (int i = 0; isString && i < clock.length(); i++) {
            isString = Character.isDigit(clock.charAt(i));
        }
        if(isString) setTime(Integer.valueOf(clock));
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }
}
