package com.example.jose.updated.controller;

import java.io.Serializable;

/**
 * Created by Joe on 5/14/17.
 */

interface CustomAlarmListener extends Serializable {
    void onReceiveAlarm();
}
