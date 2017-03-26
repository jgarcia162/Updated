package com.example.jose.updated.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UpdateBroadcastReceiver extends BroadcastReceiver {
    private UpdatedCallback callback;

    public UpdateBroadcastReceiver(UpdatedCallback callback){
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        callback.onUpdateDetected();
    }

    public void setCallback(UpdatedCallback callback) {
        this.callback = callback;
    }


}
