package com.example.jose.updated.controller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.jose.updated.R;
import com.example.jose.updated.model.Page;
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

public class NotificationService extends Service {
    private boolean started = false;
    private Timer updateTimer;
    private TimerTask updateTimerTask;
    private NotificationManager notificationManager;
    public static final int NOTIFICATION_ID = 1;
    private long timerLength;
    private LocalBroadcastManager localBroadcastManager;
    private SharedPreferences preferences;
    private ServiceHandler mServiceHandler;
    private Looper mServiceLooper;
    private String TAG = this.getClass().getCanonicalName();

    //TODO keep service alive in bakcground after app is closed, ask for help
    NotificationService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        timerLength = preferences.getLong(UPDATE_FREQUENCY_PREFERENCE_TAG, DEFAULT_UPDATE_FREQUENCY);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        localBroadcastManager = LocalBroadcastManager.getInstance(this);
//        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//        timerLength = preferences.getLong(UPDATE_FREQUENCY_PREFERENCE_TAG, DEFAULT_UPDATE_FREQUENCY);
//        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        setStarted(true);
        createTimerTask();
//        setUpTimer(updateTimerTask);
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);
        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
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
        UpdateRefresher refresher = new UpdateRefresher();
        refresher.refreshUpdate();
        Realm realm = Realm.getDefaultInstance();
        List<Page> updatedPages = realm.where(Page.class).equalTo("isUpdated", true).findAll();
        Log.d(TAG, "refresh: "+ updatedPages.toArray().length);
        realm.close();
        if (updatedPages.size() > 0) {
            if (preferences.getBoolean(STOP_NOTIFICATION_PREFERENCE_TAG, DEFAULT_NOTIFICATIONS_ACTIVE)) {
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
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private String getNamesOfUpdatedPages(List<Page> updatedPages) {
        String namesOfUpdatedPages = "";
        for (Page p : updatedPages) {
            namesOfUpdatedPages += p.getTitle() + ", ";
        }
        namesOfUpdatedPages = (updatedPages.size() == 1) ? (namesOfUpdatedPages + " has been updated!") : (namesOfUpdatedPages + " have been updated!");
        return namesOfUpdatedPages;
    }

    private final class ServiceHandler extends Handler {
        ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            setUpTimer(updateTimerTask);
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
