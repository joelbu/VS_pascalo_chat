package ch.ethz.inf.vs.a3.solution.clock;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import ch.ethz.inf.vs.a3.clock.Clock;


public class VectorClock implements Clock {
    private Map<Integer, Integer> vector;

    public VectorClock() {
        vector = new HashMap<Integer, Integer>(10);
    }

    @Override
    public void update(Clock other) {
        // For each entry in the other clock copy over the corresponding time if it is larger than
        // the entry in this clock or if this clock doesn't contain an entry for that process.
        // Again no ticking of our own clock.
        for (Integer pid : ((VectorClock) other).getPids()) {
            Integer time = vector.get(pid);
            Integer otherTime = ((VectorClock) other).getTime(pid);
            if (time == null || otherTime > time) {
                vector.put(pid, otherTime);
            }
        }
    }

    @Override
    public void setClock(Clock other) {
        // Make new map and fill it with all entries of the other clock, it's a bit inefficient
        // but unfortunately the assignment sheet says to make vector private
        vector = new HashMap<Integer, Integer>(10);
        for (Integer pid : ((VectorClock) other).getPids()) {
            Integer otherTime = ((VectorClock) other).getTime(pid);
            vector.put(pid, otherTime);
        }
    }

    @Override
    public void tick(Integer pid) {
        vector.put(pid, vector.get(pid) + 1);
    }

    @Override
    public boolean happenedBefore(Clock other) {
        boolean atLeastOneLater = false;
        boolean atLeastOneEarlier = false;

        Integer time;
        Integer otherTime;

        // Construct set of common pids, checking just those seems to be the behaviour
        // the tests specify
        Set<Integer> commonPids = ((VectorClock) other).getPids();
        commonPids.retainAll(vector.keySet());

        for (Integer pid : commonPids) {

            time = vector.get(pid);
            otherTime = ((VectorClock) other).getTime(pid);

            if (time < otherTime) {
                atLeastOneEarlier = true;
            } else if (time > otherTime) {
                atLeastOneLater = true;
            }
        }

        return !atLeastOneLater && atLeastOneEarlier;
    }

    @Override
    public String toString() {
        return new JSONObject(vector).toString();
    }

    @Override
    public void setClockFromString(String clock) {
        try {
            JSONObject json = new JSONObject(clock);
            Iterator<String> keys = json.keys();
            Map<Integer, Integer> maybeNewVector = new HashMap<Integer, Integer>(10);

            while( keys.hasNext() ) {
                String keyString = keys.next();
                Integer key = Integer.valueOf(keyString);
                Integer value = json.getInt(keyString);
                maybeNewVector.put(key, value);
            }

            vector = maybeNewVector;

        } catch (JSONException | NumberFormatException e) {
            // Malformed clock
        }
    }

    public void addProcess(Integer pid, int time) {
        vector.put(pid, time);
    }

    public int getTime(Integer pid) {
        return vector.get(pid);
    }

    public Set<Integer> getPids() {
        // Make a copy of the set just to be on the safe side
        return new HashSet<Integer>(vector.keySet());
    }
}
