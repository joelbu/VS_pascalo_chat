package ch.ethz.inf.vs.a3.pascalo.vs_pascalo_chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
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

    private class NetworkThread extends Thread {
        @Override
        public void run() {
            CommunicationHandler comHandler = CommunicationHandler.getInstance();
            comHandler.tryDeregisteringAndRetryFiveTimes();
            comHandler.destroy();
        }
    }
}
