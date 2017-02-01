package com.example.jose.updated.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.jose.updated.model.Page;
import com.example.jose.updated.model.PagesHolder;

import java.util.List;

public class UpdateBroadcastReceiver extends BroadcastReceiver {
    private UpdatedCallback callback;

    public UpdateBroadcastReceiver(UpdatedCallback callback){
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        List<Page> updatedPages = intent.getParcelableArrayListExtra("updated pages");
        PagesHolder.getInstance().setUpdatedPages(updatedPages);
        callback.onUpdateDetected();
    }

    public void setCallback(UpdatedCallback callback) {
        this.callback = callback;
    }

    /**
     * Created by Joe on 12/8/16.
     */
    public interface UpdatedCallback {
        void onUpdateDetected();
    }
}
