package ch.ethz.inf.vs.a3.pascalo.vs_pascalo_chat;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        PreferenceManager.getDefaultSharedPreferences(this);
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
                Intent mIntent = new Intent();

                //get IP address from preference
                String mIPAddress = PreferenceManager.getDefaultSharedPreferences(this).getString("address", getString(R.string.default_ip_address));
                //store IP address in the intent
                mIntent.putExtra("address", mIPAddress);

                //get port from preference
                String mPort = PreferenceManager.getDefaultSharedPreferences(this).getString("Port", getString(R.string.default_port));
                //store port in the intent
                mIntent.putExtra("port", mPort);

                //start activity with the intent
                startActivity(mIntent, ChatActivity.class);
                break;
        }
    }
}
