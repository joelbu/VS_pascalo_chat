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
                //create new intent
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);

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

                //store IP address in the intent
                intent.putExtra("address", ipAddress);

                //get port from preference
                String port = PreferenceManager.getDefaultSharedPreferences(this).getString("Port", getString(R.string.default_port));
                //store port in the intent
                intent.putExtra("port", port);

                //get username from textfield
                String username = mUsernameField.getText().toString();
                if (username.equals("")) username = "Mr. NoName";

                //generate uuid
                String uuid = UUID.randomUUID().toString();

                //store username and uuid in the intent
                intent.putExtra("username", username);
                intent.putExtra("uuid", uuid);

                // build register message
                Message reg_msg = new Message();
                reg_msg.set_header(username, uuid, "", MessageTypes.REGISTER);

                // attempt sending asynchronously with 5 write attempts
                Log.d(TAG, "Sending register message:\n" + reg_msg.toString());
                Thread t = new registrationHandler(reg_msg, ip, Integer.parseInt(port), intent);
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //TODO: start main activity at this point not from registerThread

                break;
            default:
                Log.e(TAG, "onClick got called with an unexpected view.");
                finish();
                break;
        }
    }
    private class registrationHandler extends Thread {
        private Message m;
        private InetAddress address;
        private int port;
        private boolean success;
        private Intent intent;
        public registrationHandler(Message m, InetAddress address, int port, Intent intent){
            this.m =m;
            this.address = address;
            this.port = port;
            this.intent = intent;
        }

        @Override
        public void run() {
            try {
                // create UDP Socket
                DatagramSocket socket = new DatagramSocket(port);
                socket.setSoTimeout(NetworkConsts.SOCKET_TIMEOUT);

                // Prepare data packet
                byte[] send_buf = m.toString().getBytes("UTF-8");
                DatagramPacket packet = new DatagramPacket(send_buf, send_buf.length, address, port);
                byte[] recv_buf = new byte[NetworkConsts.PAYLOAD_SIZE];
                DatagramPacket answer = new DatagramPacket(recv_buf, recv_buf.length, address, port);

                // attempt sending the packet
                boolean wait_for_ack = true;
                for(int i = 0; wait_for_ack && i <= 5; i++ ){
                    socket.send(packet);
                    try {
                        socket.receive(answer);
                        Message ack = new Message( new String(answer.getData(), 0, answer.getLength(),"UTF-8"));
                        wait_for_ack = ack.header.type.equals(MessageTypes.ACK_MESSAGE);
                        success = true;
                    } catch (SocketTimeoutException e){
                        Log.d(TAG, "Receive timeout.");
                        e.printStackTrace();
                        if (i == 5) success = false;
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
                success = false;
            }
            if(success){startActivity(intent);}
        }
    }
}
