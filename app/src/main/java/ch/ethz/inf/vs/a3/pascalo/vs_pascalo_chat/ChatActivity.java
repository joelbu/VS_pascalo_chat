package ch.ethz.inf.vs.a3.pascalo.vs_pascalo_chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.PriorityQueue;

import ch.ethz.inf.vs.a3.message.MessageComparator;
import ch.ethz.inf.vs.a3.solution.message.Message;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private CommunicationHandler comHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        comHandler = CommunicationHandler.getInstance();



        //register listener on the refresh button
        findViewById(R.id.refresh_button).setOnClickListener(this);

    }

    @Override
    public void onDestroy() {

        DestroyThread destroyThread = new DestroyThread();
        destroyThread.start();
        try {
            destroyThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        RetrieveThread tRet = new RetrieveThread();
    }

    private class DestroyThread extends Thread {
        @Override
        public void run() {
            comHandler.tryDeregisteringAndRetryFiveTimes();
            comHandler.destroy();
        }
    }

    public void displayMessages(PriorityQueue<Message> queue){

    }


    private class RetrieveThread extends Thread {
        @Override
        public void run() {
            // Create priority queue for messages
            PriorityQueue<Message> queue;
            queue = comHandler.tryRetrieveMessages();


            //Read and remove minimal object in queue like this
            //while(queue.size > 0) {
            //  Message msg = queue.remove();
            //  do something with msg
            //}


            if (queue != null) {
                final PriorityQueue<Message> finalQueue = queue;
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        displayMessages(finalQueue);
                    }
                });
            }
        }

    }
}
