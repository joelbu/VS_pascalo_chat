package ch.ethz.inf.vs.a3.pascalo.vs_pascalo_chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import ch.ethz.inf.vs.a3.queue.PriorityQueue;

import ch.ethz.inf.vs.a3.message.MessageComparator;
import ch.ethz.inf.vs.a3.solution.message.Message;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private CommunicationHandler comHandler;

    private String TAG = "ChatActivity";

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
        Toast.makeText(getApplicationContext(), "Refreshing Chat Log", Toast.LENGTH_LONG).show();
        RetrieveThread tRet = new RetrieveThread();
        tRet.start();
    }

    private class DestroyThread extends Thread {
        @Override
        public void run() {
            comHandler.tryDeregisteringAndRetryFiveTimes();
            comHandler.destroy();
        }
    }

    public void displayString(String s){
        TextView display = (TextView) findViewById(R.id.chat_view);

        display.setText(s);
    }


    private class RetrieveThread extends Thread {

        private String messageString = "";


        @Override
        public void run() {

            Log.d(TAG, "RetrieveThread started");

            // Create priority queue for messages
            PriorityQueue<Message> queue;
            queue = comHandler.tryRetrieveMessages();

            Log.d(TAG, "messages stored in priority queue");


            //Read and remove minimal object in queue like this
            while(queue.size() > 0){
                //extragt min element and store it in msg
                Message msg = queue.poll();
                Log.d(TAG, "Adding message to display string, following timestamp: " + msg.header.timestamp.toString());
                //  do something with msg
                try {
                    messageString = messageString + msg.body.getString("content") + "\n";
                } catch (JSONException e) {
                    Log.d(TAG, "unexpected JSON body");
                    e.printStackTrace();
                }
            }


            if (queue != null) {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        displayString(messageString);
                    }
                });
            }
        }

    }
}
