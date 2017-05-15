package com.example.jose.updated.controller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.jose.updated.R;
import com.example.jose.updated.model.Page;
import com.example.jose.updated.view.MainActivity;

import java.util.List;

import io.realm.Realm;

import static com.example.jose.updated.model.UpdatedConstants.DEFAULT_NOTIFICATIONS_ACTIVE;
import static com.example.jose.updated.model.UpdatedConstants.DEFAULT_UPDATE_FREQUENCY;
import static com.example.jose.updated.model.UpdatedConstants.PREFS_NAME;
import static com.example.jose.updated.model.UpdatedConstants.STOP_NOTIFICATION_PREF_TAG;
import static com.example.jose.updated.model.UpdatedConstants.UPDATE_FREQUENCY_PREF_TAG;

public class NotificationService extends JobService {
    private boolean started = false;
    private Handler handler;
    private HandlerThread handlerThread;
    private NotificationManager notificationManager;
    public static final int NOTIFICATION_ID = 1;
    private long refreshInterval;
    private String updatedPagesTitles;
    private LocalBroadcastManager localBroadcastManager;
    private SharedPreferences preferences;
    private Runnable runnable;
    private String TAG = this.getClass().getCanonicalName();

    NotificationService() {

    }


    @Override
    public void onCreate() {
        super.onCreate();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        refreshInterval = preferences.getLong(UPDATE_FREQUENCY_PREF_TAG, DEFAULT_UPDATE_FREQUENCY);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        createHandlerThread();
        runnable = createRunnable();
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        handler.postDelayed(runnable,refreshInterval);
        jobFinished(params,true);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private void createHandlerThread() {
        handlerThread = new HandlerThread("HandlerThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    private Runnable createRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    refresh();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void refresh() throws Exception {
        UpdateRefresher refresher = new UpdateRefresher();
        refresher.refreshUpdate();
        Realm realm = Realm.getDefaultInstance();
        List<Page> updatedPages = realm.where(Page.class).equalTo("isUpdated", true).findAll();
        Log.d(TAG, "refresh: " + updatedPages.toArray().length);
        realm.close();
        if (updatedPages.size() > 0) {
            if (preferences.getBoolean(STOP_NOTIFICATION_PREF_TAG, DEFAULT_NOTIFICATIONS_ACTIVE)) {
                updatedPagesTitles = getUpdatedPagesTitles(updatedPages);
                createNotification(updatedPagesTitles);
            }
            Intent broadcastIntent = new Intent();
            broadcastIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            broadcastIntent.setAction("com.example.jose.updated.controller.CUSTOM_INTENT");
            localBroadcastManager.sendBroadcast(broadcastIntent);
        }
    }

    public void createNotification(String namesOfUpdatedPages) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getBaseContext()).setSmallIcon(R.drawable.updated_logo).setContentTitle(getString(R.string.notification_title)).setContentText(namesOfUpdatedPages);
        notificationBuilder.setAutoCancel(true);
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private String getUpdatedPagesTitles(List<Page> updatedPages) {
        String namesOfUpdatedPages = "";
        for (Page p : updatedPages) {
            namesOfUpdatedPages += p.getTitle() + ", ";
        }
        namesOfUpdatedPages = (updatedPages.size() == 1) ? (namesOfUpdatedPages + " has been updated!") : (namesOfUpdatedPages + " have been updated!");
        return namesOfUpdatedPages;
    }
}
