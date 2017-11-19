package com.example.jzy.helloword;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by jzy on 8/10/17.
 */

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private String keyPrefRoomServerUrl;
    private String keyPrefRobotId;
    private String keyPrefBoxIP;
    private String keyprefUserInfoServerUrl;
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.onActivityCreateSetTheme(this);
        keyPrefRoomServerUrl = getString(R.string.pref_room_server_url_key);
        keyPrefRobotId = getString(R.string.pref_robot_id_key);
        keyPrefBoxIP = getString(R.string.pref_box_ip_key);
        keyprefUserInfoServerUrl = getString(R.string.pref_user_info_ip_key);
        // Display the fragment as the main content.
        settingsFragment = new SettingsFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set summary to be the user-description for the selected value
        SharedPreferences sharedPreferences =
                settingsFragment.getPreferenceScreen().getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        updateSummary(sharedPreferences, keyPrefRoomServerUrl);
        updateSummary(sharedPreferences, keyPrefRobotId);
        updateSummary(sharedPreferences, keyPrefBoxIP);
        updateSummary(sharedPreferences, keyprefUserInfoServerUrl);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences =
                settingsFragment.getPreferenceScreen().getSharedPreferences();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // clang-format off
        if (key.equals(keyPrefRoomServerUrl)
                || key.equals(keyPrefRobotId)
                || key.equals(keyPrefBoxIP)
                || key.equals(keyprefUserInfoServerUrl)
                ) {
            updateSummary(sharedPreferences, key);
        }
    }

    private void updateSummary(SharedPreferences sharedPreferences, String key) {
        Preference updatedPref = settingsFragment.findPreference(key);
        // Set summary to be the user-description for the selected value
        updatedPref.setSummary(sharedPreferences.getString(key, ""));
    }
}
