package com.example.jose.updated.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jose.updated.R;
import com.example.jose.updated.model.Page;

import io.realm.Realm;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import static com.example.jose.updated.model.UpdatedConstants.DEFAULT_NOTIFICATIONS_ACTIVE;
import static com.example.jose.updated.model.UpdatedConstants.DEFAULT_UPDATE_FREQUENCY_SPINNER_POSITION;
import static com.example.jose.updated.model.UpdatedConstants.PREFS_NAME;
import static com.example.jose.updated.model.UpdatedConstants.PREF_KEY_OAUTH_SECRET;
import static com.example.jose.updated.model.UpdatedConstants.PREF_KEY_OAUTH_TOKEN;
import static com.example.jose.updated.model.UpdatedConstants.PREF_KEY_TWITTER_LOGIN;
import static com.example.jose.updated.model.UpdatedConstants.SPINNER_POSITION_PREFERENCE_TAG;
import static com.example.jose.updated.model.UpdatedConstants.STOP_NOTIFICATION_PREFERENCE_TAG;
import static com.example.jose.updated.model.UpdatedConstants.TWITTER_CALLBACK_URL;
import static com.example.jose.updated.model.UpdatedConstants.TWITTER_CONSUMER_KEY;
import static com.example.jose.updated.model.UpdatedConstants.TWITTER_CONSUMER_SECRET;
import static com.example.jose.updated.model.UpdatedConstants.UPDATE_FREQUENCY_PREFERENCE_TAG;
import static com.example.jose.updated.model.UpdatedConstants.URL_TWITTER_OAUTH_VERIFIER;


/**
 * Created by Joe on 2/9/17.
 */

public class SettingsFragment extends Fragment {

    private Twitter twitter;
    private RequestToken requestToken;
    private int spinnerPosition;
    private boolean stopNotifications;
    private SharedPreferences preferences;
    private Spinner spinner;
    private Switch notificationSwitch;
    private Button saveSettingsButton;
    private Button resetDefaultsButton;
    private ProgressBar progressBar;
    private TextView notificationsTV;
    private ImageView twitterIV;
    private ImageView githubIV;
    private ImageView emailIV;
    private String onSwitchStatus;
    private ViewGroup layout;
    private ArrayAdapter adapter;

    public SettingsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        stopNotifications = preferences.getBoolean(STOP_NOTIFICATION_PREFERENCE_TAG, DEFAULT_NOTIFICATIONS_ACTIVE);
        spinnerPosition = preferences.getInt(SPINNER_POSITION_PREFERENCE_TAG, DEFAULT_UPDATE_FREQUENCY_SPINNER_POSITION); // aka default spinner position
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
        spinner = (Spinner) view.findViewById(R.id.frequency_spinner);
        notificationSwitch = (Switch) view.findViewById(R.id.notifications_switch);
        saveSettingsButton = (Button) view.findViewById(R.id.save_settings_button);
        resetDefaultsButton = (Button) view.findViewById(R.id.reset_defaults_button);
        progressBar = (ProgressBar) view.findViewById(R.id.settings_fragment_progress_bar);
        notificationsTV = (TextView) view.findViewById(R.id.notifications_settings_title);
        twitterIV = (ImageView) view.findViewById(R.id.twitter_icon);
        githubIV = (ImageView) view.findViewById(R.id.github_icon);
        emailIV = (ImageView) view.findViewById(R.id.email_icon);
        layout = (ViewGroup) view.findViewById(R.id.settings_fragment_layout);
        String notificationsStatus = preferences.getBoolean(STOP_NOTIFICATION_PREFERENCE_TAG, DEFAULT_NOTIFICATIONS_ACTIVE) ? getString(R.string.on_text) : getString(R.string.off_text);
        notificationsTV.setText(String.format(getResources().getString(R.string.notifications_settings_title), notificationsStatus));
        spinner.setAdapter(adapter);
        spinnerPosition = preferences.getInt(SPINNER_POSITION_PREFERENCE_TAG, DEFAULT_UPDATE_FREQUENCY_SPINNER_POSITION);
        spinner.setSelection(spinnerPosition);
        onSwitchStatus = preferences.getBoolean(STOP_NOTIFICATION_PREFERENCE_TAG, DEFAULT_NOTIFICATIONS_ACTIVE) ? getString(R.string.on_text) : getString(R.string.off_text);
        setContactClickListeners();
        notificationSwitch.setChecked(stopNotifications);
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
                displayAlertDialog();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    private void resetDefaultSettings() {
        preferences.edit().clear().apply();
        notificationSwitch.setChecked(true);
        spinner.setSelection(0);
        notificationsTV.setText(getResources().getString(R.string.notifications_settings_title, onSwitchStatus));
        Realm realm = Realm.getDefaultInstance();
        realm.delete(Page.class);
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
        spinnerPosition = getIndex(spinner, spinner.getSelectedItem().toString());
        preferences.edit().putInt(SPINNER_POSITION_PREFERENCE_TAG, spinnerPosition).apply();
        if (spinnerPosition == 0) {
            preferences.edit().putLong(UPDATE_FREQUENCY_PREFERENCE_TAG, DateUtils.DAY_IN_MILLIS).apply();
        } else if (spinnerPosition == 1) {
            preferences.edit().putLong(UPDATE_FREQUENCY_PREFERENCE_TAG, DateUtils.DAY_IN_MILLIS * 2).apply();
        }
        preferences.edit().putInt(SPINNER_POSITION_PREFERENCE_TAG, spinnerPosition).apply();
        preferences.edit().putBoolean(STOP_NOTIFICATION_PREFERENCE_TAG, !stopNotifications).apply();
        Toast.makeText(getContext(), R.string.settings_saved_text, Toast.LENGTH_SHORT).show();
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
                if (!twitterAlreadyLoggedIn()) {
                    loginToTwitter();
                }
                sendTwitterMessage();

//                String message = "This app is so handy ^_^ @SeeYaGarcia";
//                String url = "http://www.twitter.com/intent/tweet?url=https://twitter.com/&text="+message;
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                startActivity(i);
            }
        });

        githubIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        emailIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    private void loginToTwitter() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
        builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
        Configuration configuration = builder.build();
        TwitterFactory factory = new TwitterFactory(configuration);
        twitter = factory.getInstance();
        RequestTask requestTask = new RequestTask();
        requestTask.execute(TWITTER_CALLBACK_URL);
    }

    private boolean twitterAlreadyLoggedIn() {
        return preferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        afterLogin(intent);
    }

    public void afterLogin(Intent intent) {
        if (!twitterAlreadyLoggedIn()) {
            Uri uri = intent.getData();
            if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
                // oAuth verifier
                String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

                try {
                    // Get the access token
                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

                    // Shared Preferences
                    SharedPreferences.Editor editor = preferences.edit();

                    // After getting access token, access token secret
                    // store them in application preferences
                    editor.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
                    editor.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
                    // Store login status - true
                    editor.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
                    editor.apply();

                    // Getting user details from twitter
                    // For now i am getting his name only
                    long userID = accessToken.getUserId();
                    User user = twitter.showUser(userID);
                    String username = user.getName();
                    Toast.makeText(getContext(), username, Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Log.e("Twitter Login Error", "> " + e.getMessage());
                }
            }
        }
    }

    private void sendTwitterMessage() {

    }

    class RequestTask extends AsyncTask<String, Void, RequestToken> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected RequestToken doInBackground(String... params) {
            try {
                requestToken = twitter.getOAuthRequestToken(params[0]);
                getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
            } catch (TwitterException e) {
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
