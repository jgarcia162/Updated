package com.example.jose.updated.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class Page {

    private String title;
    private String contents;
    private String pageUrl;
    private long timeOfLastUpdateInMilliSec;


    public Page(){

    }

    public Page(String title,String pageUrl){
        this.pageUrl = pageUrl;
        this.title = title;
    }

    public Page(String title,String pageUrl,long timeTracked){
        this.pageUrl = pageUrl;
        this.title = title;
        this.timeOfLastUpdateInMilliSec = timeTracked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public long getTimeOfLastUpdateInMilliSec() {
        return timeOfLastUpdateInMilliSec;
    }

    public void setTimeOfLastUpdateInMilliSec(long timeOfLastUpdateInMilliSec) {
        this.timeOfLastUpdateInMilliSec = timeOfLastUpdateInMilliSec;
    }

    public String getFormattedTimeOfLastUpdate() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.US);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeOfLastUpdateInMilliSec);
        return formatter.format(calendar.getTime());
    }

}
