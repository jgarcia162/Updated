package com.leroyjenkins.jose.updated.model;

import android.text.format.DateUtils;

/**
 * Created by Joe on 2/9/17.
 */

public class UpdatedConstants {
    public static final String PREFS_NAME = "UpdatedPreferencesFile";
    public static final String UPDATE_FREQUENCY_PREF_TAG = "update_frequency";
    public static final long DEFAULT_UPDATE_FREQUENCY = DateUtils.DAY_IN_MILLIS;
    public static final int DEFAULT_UPDATE_FREQUENCY_SPINNER_POSITION = 0;
    public static final String SPINNER_POSITION_PREF_TAG = "spinner_position";
    public static final boolean DEFAULT_NOTIFICATIONS_ACTIVE = true;
    public static final String FIRST_TIME_PREF_TAG= "first_time";
    public static final String STOP_NOTIFICATION_PREF_TAG = "stop_notifications";
    public static final String SPINNER_NUMBER_PREF_TAG = "spinner_number";
    public static final int DEFAULT_SPINNER_NUMBER = 24;
    public static final int GOOGLE_SIGN_IN_REQUEST_CODE = 2;
}
