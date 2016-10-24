package ch.ethz.inf.vs.a3.solution.clock;

import ch.ethz.inf.vs.a3.clock.Clock;

/**
 * Created by Joel on 24.10.2016.
 */

public class VectorClock implements Clock {

    @Override
    public void update(Clock other) {

    }

    @Override
    public void setClock(Clock other) {

    }

    @Override
    public void tick(Integer pid) {

    }

    @Override
    public boolean happenedBefore(Clock other) {
        return false;
    }

    @Override
    public void setClockFromString(String clock) {

    }

    // Vector clock specific, taken from tester
    public void addProcess(Integer pid, int time) {

    }

    public int getTime(Integer pid) {

        return -1;
    }
}
