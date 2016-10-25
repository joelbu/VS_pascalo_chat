package ch.ethz.inf.vs.a3.pascalo.vs_pascalo_chat;

import org.junit.Test;

import java.util.UUID;

import ch.ethz.inf.vs.a3.message.MessageTypes;
import ch.ethz.inf.vs.a3.solution.message.Message;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void MessageParsing() throws Exception {
        Message msg = new Message();
        msg.set_header("Tester name", UUID.randomUUID().toString(), "1-1-1", MessageTypes.CHAT_MESSAGE);

        Message parsed_msg = new Message(msg.get_json());
        assert(msg.toString().equals(parsed_msg.toString()));
    }
}