package com.example.jose.updated.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jose.updated.BuildConfig;
import com.example.jose.updated.R;

import io.realm.Realm;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import static com.example.jose.updated.model.UpdatedConstants.DEFAULT_NOTIFICATIONS_ACTIVE;
import static com.example.jose.updated.model.UpdatedConstants.DEFAULT_SPINNER_NUMBER;
import static com.example.jose.updated.model.UpdatedConstants.DEFAULT_UPDATE_FREQUENCY_SPINNER_POSITION;
import static com.example.jose.updated.model.UpdatedConstants.PREFS_NAME;
import static com.example.jose.updated.model.UpdatedConstants.SPINNER_NUMBER_PREF_TAG;
import static com.example.jose.updated.model.UpdatedConstants.SPINNER_POSITION_PREF_TAG;
import static com.example.jose.updated.model.UpdatedConstants.STOP_NOTIFICATION_PREF_TAG;
import static com.example.jose.updated.model.UpdatedConstants.UPDATE_FREQUENCY_PREF_TAG;


/**
 * Created by Joe on 2/9/17.
 */

public class SettingsFragment extends Fragment {


    private Twitter twitter;
    private RequestToken requestToken;
    private int spinnerPosition;
    private int frequencyNumber;
    private boolean stopNotifications;
    private SharedPreferences preferences;
    private Spinner spinner;
    private TextInputEditText frequencyEditText;
    private Switch notificationSwitch;
    private ProgressBar progressBar;
    private TextView notificationsTV;
    private ImageView twitterIV;
    private ImageView githubIV;
    private ImageView emailIV;
    private Button saveSettingsButton;
    private Button resetDefaultsButton;
    private String onSwitchStatus;
    private ArrayAdapter adapter;

    public SettingsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        stopNotifications = preferences.getBoolean(STOP_NOTIFICATION_PREF_TAG, DEFAULT_NOTIFICATIONS_ACTIVE);
        spinnerPosition = preferences.getInt(SPINNER_POSITION_PREF_TAG, DEFAULT_UPDATE_FREQUENCY_SPINNER_POSITION);
        frequencyNumber = preferences.getInt(SPINNER_NUMBER_PREF_TAG, DEFAULT_SPINNER_NUMBER);
        adapter = ArrayAdapter.createFromResource(getContext(), R.array.frequency_spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        findViews(view);
        String notificationsStatus = preferences.getBoolean(STOP_NOTIFICATION_PREF_TAG, DEFAULT_NOTIFICATIONS_ACTIVE) ? getString(R.string.on_text) : getString(R.string.off_text);
        notificationsTV.setText(String.format(getResources().getString(R.string.notifications_settings_title), notificationsStatus));
        spinner.setAdapter(adapter);
        spinner.setSelection(spinnerPosition);
        frequencyEditText.setText(String.valueOf(frequencyNumber));
        //save value to prefs
        onSwitchStatus = (preferences.getBoolean(STOP_NOTIFICATION_PREF_TAG, DEFAULT_NOTIFICATIONS_ACTIVE) ? getString(R.string.on_text) : getString(R.string.off_text));
        setContactClickListeners();
        notificationSwitch.setChecked(stopNotifications);
        setButtonClickListeners(saveSettingsButton, resetDefaultsButton);
        super.onViewCreated(view, savedInstanceState);
    }

    private void findViews(View view) {
        spinner = (Spinner) view.findViewById(R.id.frequency_units_spinner);
        frequencyEditText = (TextInputEditText) view.findViewById(R.id.frequency_edit_text);
        notificationSwitch = (Switch) view.findViewById(R.id.notifications_switch);
        saveSettingsButton = (Button) view.findViewById(R.id.save_settings_button);
        resetDefaultsButton = (Button) view.findViewById(R.id.reset_defaults_button);
        progressBar = (ProgressBar) view.findViewById(R.id.settings_fragment_progress_bar);
        notificationsTV = (TextView) view.findViewById(R.id.notifications_settings_title);
        twitterIV = (ImageView) view.findViewById(R.id.twitter_icon);
        githubIV = (ImageView) view.findViewById(R.id.github_icon);
        emailIV = (ImageView) view.findViewById(R.id.email_icon);
    }

    private void setButtonClickListeners(Button saveSettingsButton, Button resetDefaultsButton) {
        notificationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwitchStatus = notificationSwitch.isChecked() ? getString(R.string.on_text) : getString(R.string.off_text);
                notificationsTV.setText(getResources().getString(R.string.notifications_settings_title, onSwitchStatus));
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
                displayResetDefaultsAlertDialog();
            }
        });
    }

    private void resetDefaultSettings() {
        preferences.edit().clear().apply();
        notificationSwitch.setChecked(DEFAULT_NOTIFICATIONS_ACTIVE);
        spinner.setSelection(DEFAULT_UPDATE_FREQUENCY_SPINNER_POSITION);
        notificationsTV.setText(getResources().getString(R.string.notifications_settings_title, onSwitchStatus));
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.deleteAll();
        realm.close();
//        pageAdapterListener.onDataChanged();
    }

    private void displayResetDefaultsAlertDialog() {
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
        int spinnerNumber;
        String frequencyString = frequencyEditText.getText().toString();
        if (TextUtils.isEmpty(frequencyString)) {
            Toast.makeText(getContext(), R.string.must_enter_number, Toast.LENGTH_SHORT).show();
            frequencyEditText.requestFocusFromTouch();
            InputMethodManager lManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            lManager.showSoftInput(frequencyEditText, 0);
        } else {
            spinnerNumber = Integer.parseInt(frequencyString);
            spinnerPosition = getIndex(spinner, spinner.getSelectedItem().toString());
            preferences.edit().putInt(SPINNER_POSITION_PREF_TAG, spinnerPosition).apply();
            preferences.edit().putInt(SPINNER_NUMBER_PREF_TAG, spinnerNumber).apply();
            if (spinnerPosition == 0) {
                preferences.edit().putLong(UPDATE_FREQUENCY_PREF_TAG, DateUtils.MINUTE_IN_MILLIS * spinnerNumber).apply();
            } else if (spinnerPosition == 1) {
                preferences.edit().putLong(UPDATE_FREQUENCY_PREF_TAG, DateUtils.HOUR_IN_MILLIS * spinnerNumber).apply();
            } else if (spinnerPosition == 2) {
                preferences.edit().putLong(UPDATE_FREQUENCY_PREF_TAG, DateUtils.DAY_IN_MILLIS * spinnerNumber).apply();
            }
            preferences.edit().putInt(SPINNER_POSITION_PREF_TAG, spinnerPosition).apply();
            preferences.edit().putBoolean(STOP_NOTIFICATION_PREF_TAG, !stopNotifications).apply();
            Toast.makeText(getContext(), R.string.settings_saved_text, Toast.LENGTH_SHORT).show();
        }
    }

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(myString)) {
                index = i;
            }
        }
        return index;
    }

    public void setContactClickListeners() {
        twitterIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginToTwitter();
            }
        });

        githubIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInCustomTabBrowser();
            }
        });

        emailIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getString(R.string.email_intent))));
            }
        });
    }

    private void openInCustomTabBrowser() {
        Uri pageUri = Uri.parse(getString(R.string.github_url));
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setCloseButtonIcon(BitmapFactory.decodeResource(
                getContext().getResources(), R.drawable.ic_arrow_back_white_24dp));
        builder.addDefaultShareMenuItem();
        builder.setStartAnimations(getContext(), R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        // set toolbar color and/or setting custom actions before invoking build()
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            customTabsIntent.intent.putExtra(Intent.EXTRA_REFERRER,
                    Uri.parse(Intent.URI_ANDROID_APP_SCHEME + "//" + getContext().getPackageName()));
        }
        customTabsIntent.launchUrl(getContext(), pageUri);
    }

    private void loginToTwitter() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(BuildConfig.twitterConsumerKey);
        builder.setOAuthConsumerSecret(BuildConfig.twitterConsumerSecret);
        Configuration configuration = builder.build();
        TwitterFactory factory = new TwitterFactory(configuration);
        twitter = factory.getInstance();
        RequestTask requestTask = new RequestTask();
        requestTask.execute(BuildConfig.twitterCallbackUrl);
    }

    private class RequestTask extends AsyncTask<String, Void, RequestToken> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected RequestToken doInBackground(String... params) {
            try {
                requestToken = twitter.getOAuthRequestToken(params[0]);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return requestToken;
        }

        @Override
        protected void onPostExecute(RequestToken requestToken) {
            super.onPostExecute(requestToken);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
