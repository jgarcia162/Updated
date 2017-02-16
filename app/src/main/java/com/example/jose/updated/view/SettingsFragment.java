package com.example.jose.updated.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jose.updated.R;

import static com.example.jose.updated.model.UpdatedConstants.DEFAULT_NOTIFICATIONS_ACTIVE;
import static com.example.jose.updated.model.UpdatedConstants.DEFAULT_UPDATE_FREQUENCY;
import static com.example.jose.updated.model.UpdatedConstants.DEFAULT_WIFI_ONLY;
import static com.example.jose.updated.model.UpdatedConstants.PREFS_NAME;


/**
 * Created by Joe on 2/9/17.
 */

public class SettingsFragment extends Fragment {
    private long updateFrequency;
    private boolean stopNotifications;
    private boolean wifiOnly;
    private SharedPreferences preferences;
    public SettingsFragment(){
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getContext().getSharedPreferences(PREFS_NAME,0);
        updateFrequency = preferences.getLong("update_frequency", DEFAULT_UPDATE_FREQUENCY);
        stopNotifications = preferences.getBoolean("stop_notifications",DEFAULT_NOTIFICATIONS_ACTIVE);
        wifiOnly = preferences.getBoolean("wifi_only",DEFAULT_WIFI_ONLY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_page,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
