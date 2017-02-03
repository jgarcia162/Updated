package com.example.jose.updated.controller;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.jose.updated.R;
import com.example.jose.updated.model.Page;
import com.example.jose.updated.model.PagesHolder;
import com.example.jose.updated.view.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends IntentService {
    private static List<Page> pagesToTrack;
    private static List<Page> updatedPages;
    private boolean started = false;
    private Timer updateTimer;
    private TimerTask updateTimerTask;
    private NotificationManager notificationManager;
    public static final int NOTIFICATION_ID = 1;

    private PagesHolder pagesHolder;
    private LocalBroadcastManager localBroadcastManager;


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
        pagesHolder = PagesHolder.getInstance();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        setStarted(true);
        pagesToTrack = pagesHolder.getPagesToTrack();
        updatedPages = pagesHolder.getUpdatedPages();
        createTimerTask();
        setUpTimer(updateTimerTask);
    }

    private void setUpTimer(TimerTask task) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 5000);
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
        String namesOfUpdatedPages;
        if (updatedPages.size() > 0) {
            namesOfUpdatedPages = "";
            for (Page p : updatedPages) {
                namesOfUpdatedPages += p.getTitle() + ", ";
            }
            namesOfUpdatedPages += " have been updated!";
            Log.i("NAMES OF PAGES ", namesOfUpdatedPages);
            createNotification(namesOfUpdatedPages);
            Intent broadcastIntent = new Intent();
            broadcastIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            broadcastIntent.putParcelableArrayListExtra("updated pages", (ArrayList<? extends Parcelable>) updatedPages);
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
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getBaseContext()).setSmallIcon(R.drawable.default_photo).setContentTitle(getString(R.string.notification_title)).setContentText(namesOfUpdatedPages);
        notificationBuilder.setAutoCancel(true);
        Intent notificationIntent = new Intent(getBaseContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

}
