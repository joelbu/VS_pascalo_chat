package ch.ethz.inf.vs.a3.solution.clock;

import java.util.Map;

import ch.ethz.inf.vs.a3.clock.Clock;


public class VectorClock implements Clock {
    private Map<Integer, Integer> vector;

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
