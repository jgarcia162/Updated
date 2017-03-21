package com.example.jose.updated.model;

import android.text.format.DateUtils;

import com.example.jose.updated.R;

/**
 * Created by Joe on 2/9/17.
 */

public class UpdatedConstants {
    public static final String PREFS_NAME = "UpdatedPreferencesFile";
    public static final String DEFAULT_NOTES = "No Notes";
    public static String UPDATE_FREQUENCY_PREFERENCE_TAG = "update_frequency";
    public static long DEFAULT_UPDATE_FREQUENCY = DateUtils.DAY_IN_MILLIS;
    public static int DEFAULT_UPDATE_FREQUENCY_SPINNER_POSITION = 0;
    public static String SPINNER_POSITION_PREFERENCE_TAG = "spinner_position";
    public static boolean DEFAULT_NOTIFICATIONS_ACTIVE = true;
    public static String STOP_NOTIFICATION_PREFERENCE_TAG = "stop_notifications";
    public static boolean DEFAULT_WIFI_ONLY = false;
    public static int DEFAULT_FAVICON = R.drawable.updated_logo;
    public static String IS_ACTIVE_TAG = "_is_active";
    public static String NOTES_TAG = "_notes";
    public static final int INVALID_RES = -1;
    public static final String TWITTER_CONSUMER_KEY = "lfIGDxjsZvUiz7BYu0le3qhtE";
    public static final String TWITTER_CONSUMER_SECRET = "kyWoitJ4j2TPeN1PtbTH8nmuhz9JSUelaZYWXeTg4sgr0K5Dtb";
    public static final String TWITTER_ACCESS_TOKEN= "3070747791-eH8iIdUhpCga8H1Uw3uKgYCv8Y157TufuvF8M33";
    public static final String TWITTER_ACCESS_TOKEN_SECRET= "tGodancbGWhKHtHIWZpsxz2jat96CRhuGoERRBLSOsZS8";
    public static final String PREFERENCE_NAME = "twitter_oauth";
    public static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    public static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    public static final String PREF_KEY_TWITTER_LOGGED_IN = "isTwitterLoggedIn";
    public static final String TWITTER_CALLBACK_URL = "https://twitter.com/direct_messages/create/seeyagarcia";
    public static final String URL_TWITTER_AUTH = "https://api.twitter.com/oauth/authorize";
    public static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    public static final String URL_TWITTER_OAUTH_TOKEN = "https://api.twitter.com/oauth/access_token";
}
