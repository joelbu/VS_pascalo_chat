package ch.ethz.inf.vs.a3.pascalo.vs_pascalo_chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.PriorityQueue;

import ch.ethz.inf.vs.a3.message.MessageComparator;
import ch.ethz.inf.vs.a3.solution.message.Message;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);




        //register listener on the refresh button
        findViewById(R.id.refresh_button).setOnClickListener(this);

    }

    @Override
    public void onDestroy() {

        NetworkThread networkThread = new NetworkThread();
        networkThread.start();
        try {
            networkThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {


        // Create priority queue for messages: with initial capacity 10
        PriorityQueue<Message> queue =
                new PriorityQueue<Message>(10,  new MessageComparator());
        //Insert messages to queue like this, sorting is automatic
        //queue.add(m);
        //Read and remove minimal object in queue like this
        //while(queue.size > 0) {
        //  Message msg = queue.remove();
        //  do something with msg
        //}

    }

    private class NetworkThread extends Thread {
        @Override
        public void run() {
            CommunicationHandler comHandler = CommunicationHandler.getInstance();
            comHandler.tryDeregisteringAndRetryFiveTimes();
            comHandler.destroy();
        }
    }
}
