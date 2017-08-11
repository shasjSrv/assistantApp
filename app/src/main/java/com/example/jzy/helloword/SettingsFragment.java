package com.example.jzy.helloword;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by jzy on 8/10/17.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
