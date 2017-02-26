package com.example.jose.updated.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.jose.updated.R;

import static com.example.jose.updated.model.UpdatedConstants.DEFAULT_NOTIFICATIONS_ACTIVE;
import static com.example.jose.updated.model.UpdatedConstants.DEFAULT_UPDATE_FREQUENCY;
import static com.example.jose.updated.model.UpdatedConstants.PREFS_NAME;


/**
 * Created by Joe on 2/9/17.
 */

public class SettingsFragment extends Fragment {
    private long updateFrequency;
    private boolean stopNotifications;
    private SharedPreferences preferences;
    private Spinner spinner;
    private TextInputEditText frequencyET;
    private TextInputEditText emailMessageET;
    ArrayAdapter adapter;
    public SettingsFragment(){
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getContext().getSharedPreferences(PREFS_NAME,0);
        updateFrequency = preferences.getLong("update_frequency", DEFAULT_UPDATE_FREQUENCY);
        stopNotifications = preferences.getBoolean("stop_notifications",DEFAULT_NOTIFICATIONS_ACTIVE);
        adapter = ArrayAdapter.createFromResource(getContext(),R.array.frequency_spinner_array,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_page,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        spinner = (Spinner) view.findViewById(R.id.frequency_spinner);
        emailMessageET = (TextInputEditText) view.findViewById(R.id.message_et);
        spinner.setAdapter(adapter);
        super.onViewCreated(view, savedInstanceState);
    }
}
