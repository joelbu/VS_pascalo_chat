package ch.ethz.inf.vs.a3.pascalo.vs_pascalo_chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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
