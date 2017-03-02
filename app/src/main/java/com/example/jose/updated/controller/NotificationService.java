package com.example.jose.updated.controller;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.example.jose.updated.R;
import com.example.jose.updated.model.Page;
import com.example.jose.updated.model.RealmDatabaseHelper;
import com.example.jose.updated.view.MainActivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;

import static com.example.jose.updated.model.UpdatedConstants.DEFAULT_NOTIFICATIONS_ACTIVE;
import static com.example.jose.updated.model.UpdatedConstants.DEFAULT_UPDATE_FREQUENCY;
import static com.example.jose.updated.model.UpdatedConstants.PREFS_NAME;
import static com.example.jose.updated.model.UpdatedConstants.STOP_NOTIFICATION_PREFERENCE_TAG;
import static com.example.jose.updated.model.UpdatedConstants.UPDATE_FREQUENCY_PREFERENCE_TAG;

public class NotificationService extends IntentService {
    private boolean started = false;
    private Timer updateTimer;
    private TimerTask updateTimerTask;
    private NotificationManager notificationManager;
    public static final int NOTIFICATION_ID = 1;
    private long timerLength;
    private RealmDatabaseHelper realmDatabaseHelper;
    Realm realm;
    private LocalBroadcastManager localBroadcastManager;
    private SharedPreferences preferences;


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    public NotificationService(String name) {
        super(name);
    }

    NotificationService() {
        super("NotificationService");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        realmDatabaseHelper = new RealmDatabaseHelper();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        preferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        timerLength = preferences.getLong(UPDATE_FREQUENCY_PREFERENCE_TAG,DEFAULT_UPDATE_FREQUENCY);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        setStarted(true);
        createTimerTask();
        setUpTimer(updateTimerTask);
    }
    private void setUpTimer(TimerTask task) {
        Timer timer = new Timer();
        //TODO don't forget to change this to timerLength when done testing
        timer.scheduleAtFixedRate(task, 0, 10000);
    }

    private void createTimerTask() {
        updateTimerTask = new TimerTask() {
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
        UpdateRefresher.refreshUpdate();
        List<Page> updatedPages = realmDatabaseHelper.getUpdatedPages();
        if (updatedPages.size() > 0) {
            if(preferences.getBoolean(STOP_NOTIFICATION_PREFERENCE_TAG,DEFAULT_NOTIFICATIONS_ACTIVE)){
                createNotification(getNamesOfUpdatedPages(updatedPages));
            }
            Intent broadcastIntent = new Intent();
            broadcastIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            broadcastIntent.setAction("com.example.jose.updated.controller.CUSTOM_INTENT");
            localBroadcastManager.sendBroadcast(broadcastIntent);
        }
    }

    public void setStarted(boolean n) {
        started = n;
    }

    public boolean isStarted() {
        return started;
    }


    public void createNotification(String namesOfUpdatedPages) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getBaseContext()).setSmallIcon(R.drawable.updated_logo).setContentTitle(getString(R.string.notification_title)).setContentText(namesOfUpdatedPages);
        notificationBuilder.setAutoCancel(true);
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private String getNamesOfUpdatedPages(List<Page> updatedPages){
        String namesOfUpdatedPages = "";
        for (Page p : updatedPages) {
            namesOfUpdatedPages += p.getTitle() + ", ";
        }
        namesOfUpdatedPages = (updatedPages.size() == 1) ? (namesOfUpdatedPages + " has been updated!") : (namesOfUpdatedPages + " have been updated!");
        return namesOfUpdatedPages;
    }

}
