package ch.ethz.inf.vs.a3.pascalo.vs_pascalo_chat;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by pascal on 07.10.16.
 */

public class PrefsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
