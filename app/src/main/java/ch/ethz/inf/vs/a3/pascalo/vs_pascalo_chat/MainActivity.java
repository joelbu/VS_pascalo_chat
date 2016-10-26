package ch.ethz.inf.vs.a3.pascalo.vs_pascalo_chat;

import android.content.Intent;
import android.nfc.Tag;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import ch.ethz.inf.vs.a3.message.MessageTypes;
import ch.ethz.inf.vs.a3.solution.message.Message;
import ch.ethz.inf.vs.a3.udpclient.NetworkConsts;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mUsernameField;
    private final String TAG = "Main Activity";
    public CommunicationHandler mCommHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        PreferenceManager.getDefaultSharedPreferences(this);

        mUsernameField = (EditText) findViewById(R.id.main_username_field);
        findViewById(R.id.main_join_button).setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.settings :
                Intent myIntent = new Intent(this, SettingsActivity.class);
                this.startActivity(myIntent);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_join_button:

                //get IP address from preference
                String ipAddress = PreferenceManager.getDefaultSharedPreferences(this).getString("address", getString(R.string.default_ip_address));

                //cast IP address to InetAddress
                String[] numbers = ipAddress.split("[.]");
                InetAddress ip;
                try {
                    if (numbers.length == 4) {
                        byte[] chosenIp = new byte[4];
                        for(int i = 0; i < 4; i++ ){
                            chosenIp[i] = (byte) Integer.parseInt(numbers[i]);
                        }
                        ip = InetAddress.getByAddress(chosenIp);
                    } else {
                        //ip = StringToInet( NetworkConsts.SERVER_ADDRESS )
                        byte[] defaultIp = {10, 0, 2, 2};
                        ip = InetAddress.getByAddress(defaultIp);
                    }
                }
                catch(Exception e) {
                    Log.d(TAG, "Could not resolve IP.");
                    e.printStackTrace();
                    return;
                }

                //get port from preference
                String port = PreferenceManager.getDefaultSharedPreferences(this).getString("Port", getString(R.string.default_port));

                //get username from textfield
                String username = mUsernameField.getText().toString();
                if (username.equals("")) username = "Mr. NoName";

                //generate uuid
                String uuid = UUID.randomUUID().toString();

                new NetworkThread(ip, Integer.parseInt(port), username, uuid).start();
                break;
            default:
                Log.e(TAG, "onClick got called with an unexpected view.");
                finish();
                break;
        }

    }

    public void transitionToChat() {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        startActivity(intent);
    }

    private class NetworkThread extends Thread {
        private InetAddress mAddress;
        private int mPort;
        private String mUsername;
        private String mUUID;

        public NetworkThread (InetAddress address, int port, String username, String uuid) {
            mAddress = address;
            mPort = port;
            mUsername = username;
            mUUID = uuid;
        }

        @Override
        public void run() {
            CommunicationHandler.Initialise(mAddress, mPort, mUsername, mUUID);
            mCommHandler = CommunicationHandler.getInstance();
            boolean success = mCommHandler.tryRegisteringAndRetryFiveTimes();

            // I'm not sure it matters but it feels better, I don't want the new activity to
            // somehow run on the network thread or anything
            if (success) runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    transitionToChat();
                }
            });
        }
    }
}
