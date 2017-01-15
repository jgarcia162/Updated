package com.example.jose.updated.controller;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.jose.updated.R;
import com.example.jose.updated.model.Page;
import com.example.jose.updated.model.PagesHolder;
import com.example.jose.updated.view.MainActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Joe on 12/3/16.
 */

public class NotificationService extends IntentService implements UpdatedCallback{
    private static List<Page> pagesToTrack;
    public static List<Page> updatedPages;
    private boolean started = false;
    private Timer updateTimer;
    private TimerTask updateTimerTask;
    private long currentTime;
    private long fourtyEightHours = 60*60*48*1000;
    private NotificationManager notificationManager;
    public static final int NOTIFICATION_ID = 1;

    private Handler handler;
    private PagesHolder pagesHolder;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public NotificationService(String name) {
        super(name);
    }

    NotificationService(){
        super("NotificationService");

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        pagesHolder = PagesHolder.getInstance();
        handler = new Handler();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        currentTime = new Date().getTime();
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

    //TODO create broadcastreceiver to handle update changes and notify data set in adapter
    @Override
    public void sendBroadcast(Intent intent) {
        super.sendBroadcast(intent);
    }

    private void setUpTimer(TimerTask task) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task,0,5000);
    }

    private void createTimerTask() {
        updateTimerTask = new TimerTask() {
            @Override
            public void run() {
                refresh();
                Log.i("TIMER TASK","SERVICE RUNNING");
            }
        };
    }

    //This method will be called periodically
    public void refresh(){
        UpdateRefresher.refreshUpdate();
        if(updatedPages.size() > 0){
            onUpdateDetected(updatedPages);
        }
    }

    public static void addPageToTrack(Page page){
        page.setUpdated(true);
        pagesToTrack.add(page);
        MainActivity.notifyAdapterDataSetChange();
    }

    public void setStarted(boolean n){
        started = n;
    }

    public boolean isStarted(){
        return started;
    }

    @Override
    public void onUpdateDetected(List<Page> updatedPagesList) {
        String namesOfUpdatedPages = "";
        for(Page p :updatedPagesList){
            namesOfUpdatedPages+= p.getTitle() + ", ";
        }
        namesOfUpdatedPages += " have been updated!";
        Log.i("NAMES OF PAGES ",namesOfUpdatedPages);
        createNotification(namesOfUpdatedPages);
        Intent broadcastIntent = new Intent();
        broadcastIntent.putParcelableArrayListExtra("updated pages", (ArrayList<? extends Parcelable>) updatedPages);
        sendBroadcast(broadcastIntent);
    }

    public void createNotification(String namesOfUpdatedPages) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.default_photo).setContentTitle(getString(R.string.notification_title)).setContentText(namesOfUpdatedPages);
        notificationBuilder.setAutoCancel(true);
        Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build());
    }


}
