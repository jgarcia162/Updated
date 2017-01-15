package com.example.jose.updated.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.jose.updated.view.MainActivity;

public class UpdateBroadcastReceiver extends BroadcastReceiver {
    public UpdateBroadcastReceiver(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        MainActivity.updatedPages = intent.getParcelableArrayListExtra("updated pages");
        MainActivity.notifyAdapterDataSetChange();
        Toast.makeText(context, "got the broadcast", Toast.LENGTH_SHORT).show();
    }
}
