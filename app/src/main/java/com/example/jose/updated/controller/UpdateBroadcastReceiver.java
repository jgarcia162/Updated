package com.example.jose.updated.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.jose.updated.model.Page;
import com.example.jose.updated.model.PagesHolder;
import com.example.jose.updated.view.MainActivity;

import java.util.List;

public class UpdateBroadcastReceiver extends BroadcastReceiver {
    public UpdateBroadcastReceiver(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        List<Page> updatedPages = intent.getParcelableArrayListExtra("updated pages");
        PagesHolder.getInstance().setUpdatedPages(updatedPages);
        MainActivity.notifyAdapterDataSetChange();
    }
}
