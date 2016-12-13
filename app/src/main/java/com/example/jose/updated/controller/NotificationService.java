package com.example.jose.updated.controller;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;

import com.example.jose.updated.model.Page;

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

    private Handler handler;

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
        handler = new Handler();
        return super.onStartCommand(intent, flags, startId);
    }


    //TODO create intent service to check for updates in background and send push notification
    @Override
    protected void onHandleIntent(Intent intent) {
        setStarted(true);
        pagesToTrack = intent.getParcelableArrayListExtra("pages to track");
        updatedPages = new ArrayList<>();
        createTimerTask();
        currentTime = new Date().getTime();
        setUpTimer(updateTimerTask);

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
            }
        };
    }


    //This method will be called periodically
    public void refresh(){
        UpdateRefresher.refreshUpdate(pagesToTrack,updatedPages);
    }

    public static void addPageToTrack(Page page){
        page.setUpdated(true);
        pagesToTrack.add(page);
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
    }


}
