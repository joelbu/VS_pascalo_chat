package ch.ethz.inf.vs.a3.message;

import java.util.Comparator;
import ch.ethz.inf.vs.a3.solution.message.Message;

/**
 * Message comparator class. Use with PriorityQueue.
 */
public class MessageComparator implements Comparator<Message> {

    @Override
    public int compare(Message lhs, Message rhs) {
        // Write your code here
        //TODO Do actual vector clock comparison instead of lexicographic comparison
        return lhs.header.timestamp.compareTo(rhs.header.timestamp);
    }

}
