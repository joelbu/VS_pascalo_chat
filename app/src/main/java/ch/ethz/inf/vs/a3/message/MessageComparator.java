package ch.ethz.inf.vs.a3.message;

import java.util.Comparator;

import ch.ethz.inf.vs.a3.solution.clock.VectorClock;
import ch.ethz.inf.vs.a3.solution.message.Message;

/**
 * Message comparator class. Use with PriorityQueue.
 */
public class MessageComparator implements Comparator<Message> {

    @Override
    public int compare(Message lhs, Message rhs) {
        // Write your code here

        // Converting timestamp to VectorClock
        VectorClock left_time = new VectorClock();
        left_time.setClockFromString(lhs.header.timestamp);

        VectorClock right_time = new VectorClock();
        right_time.setClockFromString(rhs.header.timestamp);

        // set flags left-before-right (lbr) and right-before-left (rbl)
        boolean lbr = left_time.happenedBefore(right_time);
        boolean rbl = right_time.happenedBefore(left_time);

        // -1 <=> left less than right
        // 0  <=> equal
        // 1 <=> left greater than right
        return lbr ? -1 : rbl ? 1 : 0;
        // Yes, cryptic looking code is strictly cooler and faster than easy-to-understand-code :)
    }

}
