package com.example.jose.updated.model;

import android.text.format.DateUtils;

/**
 * Created by Joe on 2/9/17.
 */

public class UpdatedConstants {
    public static final String PREFS_NAME = "UpdatedPreferencesFile";
    public static final String UPDATE_FREQUENCY_PREFERENCE_TAG = "update_frequency";
    public static final long DEFAULT_UPDATE_FREQUENCY = DateUtils.DAY_IN_MILLIS;
    public static final int DEFAULT_UPDATE_FREQUENCY_SPINNER_POSITION = 0;
    public static final String SPINNER_POSITION_PREFERENCE_TAG = "spinner_position";
    public static final boolean DEFAULT_NOTIFICATIONS_ACTIVE = true;
    public static final String STOP_NOTIFICATION_PREFERENCE_TAG = "stop_notifications";
    public static final String SPINNER_NUMBER_PREFERENCE_TAG = "spinner_number";
    public static final int DEFAULT_SPINNER_NUMBER = 24;

}
