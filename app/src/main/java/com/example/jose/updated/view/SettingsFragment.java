package com.example.jose.updated.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jose.updated.R;

import static com.example.jose.updated.model.UpdatedConstants.DEFAULT_NOTIFICATIONS_ACTIVE;
import static com.example.jose.updated.model.UpdatedConstants.DEFAULT_UPDATE_FREQUENCY_SPINNER_POSITION;
import static com.example.jose.updated.model.UpdatedConstants.PREFS_NAME;
import static com.example.jose.updated.model.UpdatedConstants.SPINNER_POSITION_PREFERENCE_TAG;
import static com.example.jose.updated.model.UpdatedConstants.STOP_NOTIFICATION_PREFERENCE_TAG;
import static com.example.jose.updated.model.UpdatedConstants.UPDATE_FREQUENCY_PREFERENCE_TAG;


/**
 * Created by Joe on 2/9/17.
 */

public class SettingsFragment extends Fragment {
    private int spinnerPosition;
    private boolean stopNotifications;
    private SharedPreferences preferences;
    private Spinner spinner;
    private Switch notificationSwitch;
    private Button saveSettingsButton;
    private Button resetDefaultsButton;
    private TextView notificationsTV;
    private ImageView twitterIV;
    private ImageView instagramIV;
    private ImageView githubIV;
    private ImageView emailIV;
    String onSwitchStatus;
    private ViewGroup layout;
    ArrayAdapter adapter;
    public SettingsFragment(){
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        stopNotifications = preferences.getBoolean(STOP_NOTIFICATION_PREFERENCE_TAG,DEFAULT_NOTIFICATIONS_ACTIVE);
        spinnerPosition = preferences.getInt(SPINNER_POSITION_PREFERENCE_TAG, DEFAULT_UPDATE_FREQUENCY_SPINNER_POSITION); // aka default spinner position
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
        notificationSwitch = (Switch) view.findViewById(R.id.notifications_switch);
        saveSettingsButton = (Button) view.findViewById(R.id.save_settings_button);
        resetDefaultsButton = (Button) view.findViewById(R.id.reset_defaults_button);
        notificationsTV = (TextView) view.findViewById(R.id.notifications_settings_title);
        twitterIV = (ImageView) view.findViewById(R.id.twitter_icon);
        instagramIV = (ImageView) view.findViewById(R.id.instagram_icon);
        githubIV = (ImageView) view.findViewById(R.id.github_icon);
        emailIV = (ImageView) view.findViewById(R.id.email_icon);
        layout = (ViewGroup) view.findViewById(R.id.settings_fragment_layout);
        String notificationsStatus = preferences.getBoolean(STOP_NOTIFICATION_PREFERENCE_TAG,DEFAULT_NOTIFICATIONS_ACTIVE) ? getString(R.string.on_text) : getString(R.string.off_text);
        notificationsTV.setText(String.format(getResources().getString(R.string.notifications_settings_title),notificationsStatus));
        spinner.setAdapter(adapter);
        spinnerPosition = preferences.getInt(SPINNER_POSITION_PREFERENCE_TAG,DEFAULT_UPDATE_FREQUENCY_SPINNER_POSITION);
        spinner.setSelection(spinnerPosition);
        onSwitchStatus = preferences.getBoolean(STOP_NOTIFICATION_PREFERENCE_TAG,DEFAULT_NOTIFICATIONS_ACTIVE) ? getString(R.string.on_text) : getString(R.string.off_text);
        notificationSwitch.setChecked(stopNotifications);
        notificationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwitchStatus = notificationSwitch.isChecked() ? getString(R.string.on_text) : getString(R.string.off_text);
                notificationsTV.setText(getResources().getString(R.string.notifications_settings_title,onSwitchStatus));
            }
        });

        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettingsButtonClicked();
            }
        });

        resetDefaultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAlertDialog();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    private void resetDefaultSettings() {
        preferences.edit().clear().apply();
        notificationSwitch.setChecked(true);
        spinner.setSelection(0);
        notificationsTV.setText(getResources().getString(R.string.notifications_settings_title,onSwitchStatus));
    }

    private void displayAlertDialog() {
        AlertDialog alert = new AlertDialog.Builder(getActivity()).setTitle(R.string.reset_default_alert_title).setMessage(R.string.reset_defaults_alert_message).setPositiveButton(R.string.reset_defaults_alert_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetDefaultSettings();
                Toast.makeText(getActivity(), R.string.defaults_reset_text, Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton(R.string.reset_defaults_alert_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        alert.show();
    }

    private void saveSettingsButtonClicked() {
        spinnerPosition = getIndex(spinner,spinner.getSelectedItem().toString());
        preferences.edit().putInt(SPINNER_POSITION_PREFERENCE_TAG, spinnerPosition).apply();
        if(spinnerPosition == 0){
            preferences.edit().putLong(UPDATE_FREQUENCY_PREFERENCE_TAG, DateUtils.DAY_IN_MILLIS).apply();
        }else if(spinnerPosition == 1){
            preferences.edit().putLong(UPDATE_FREQUENCY_PREFERENCE_TAG,DateUtils.DAY_IN_MILLIS*2).apply();
        }
        preferences.edit().putInt(SPINNER_POSITION_PREFERENCE_TAG, spinnerPosition).apply();
        preferences.edit().putBoolean(STOP_NOTIFICATION_PREFERENCE_TAG,!stopNotifications).apply();
        Toast.makeText(getContext(), R.string.settings_saved_text, Toast.LENGTH_SHORT).show();
    }

    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }
}
