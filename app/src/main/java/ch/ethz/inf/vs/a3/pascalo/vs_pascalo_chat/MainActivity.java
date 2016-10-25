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

import org.w3c.dom.Text;

import java.util.UUID;

import ch.ethz.inf.vs.a3.message.MessageTypes;
import ch.ethz.inf.vs.a3.solution.message.Message;

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
                //TODO
                //send(reg_msg);

                // FIXME: This should happen after receiving an ACK

                //start activity with the intent
                startActivity(intent);
                break;
            default:
                Log.e(TAG, "onClick got called with an unexpected view.");
                finish();
                break;
        }
    }
}
