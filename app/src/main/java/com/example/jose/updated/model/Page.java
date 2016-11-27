package com.example.jose.updated.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class Page {

    private String title = "";
    private String contents;
    private String pageUrl;
    private String nickname;
    private String notes;
    private long timeOfLastUpdateInMilliSec;
    private boolean isUpdated;



    public Page() {

    }

    public Page(String title, String pageUrl) {
        this.pageUrl = pageUrl;
        this.title = title;
    }

    public Page(String title, String pageUrl, long timeTracked) {
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

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setNotes(String notes){
        this.notes = notes;
    }

    public String getNotes(){
        return this.notes;
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

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }

}
