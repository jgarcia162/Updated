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

}
