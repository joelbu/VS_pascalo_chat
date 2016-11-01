package ch.ethz.inf.vs.a3.clock;

import java.util.Comparator;

import ch.ethz.inf.vs.a3.solution.clock.VectorClock;


public class VectorClockComparator implements Comparator<VectorClock> {

    @Override
    public int compare(VectorClock lhs, VectorClock rhs) {

        // set flags left-before-right (lbr) and right-before-left (rbl)
        boolean lbr = lhs.happenedBefore(rhs);
        boolean rbl = lhs.happenedBefore(rhs);

        // -1 <=> left less than right
        // 0  <=> equal
        // 1 <=> left greater than right
        return lbr ? -1 : rbl ? 1 : 0;
        // Yes, cryptic looking code is strictly cooler and faster than easy-to-understand-code :)
    }
}
