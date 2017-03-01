package com.example.jose.updated.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;


public class Page extends RealmObject implements Parcelable{

    private String title;
    private String contents;
    @PrimaryKey
    private String pageUrl;
    private String nickname;
    private String notes;
    private long updateFrequency;
    private long timeOfLastUpdateInMilliSec;
    private boolean isUpdated;
    private boolean isActive;
    @Ignore
    private Bitmap bitmapIcon;



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

    public Page(Parcel in) {
        title = in.readString();
        contents = in.readString();
        pageUrl = in.readString();
        nickname = in.readString();
        notes = in.readString();
        timeOfLastUpdateInMilliSec = in.readLong();
        isUpdated = in.readByte() != 0;
        isActive = in.readByte() != 0;
    }

    public static final Creator<Page> CREATOR = new Creator<Page>() {
        @Override
        public Page createFromParcel(Parcel in) {
            return new Page(in);
        }

        @Override
        public Page[] newArray(int size) {
            return new Page[size];
        }
    };

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
    public long getUpdateFrequency() {
        return updateFrequency;
    }

    public void setUpdateFrequency(long updateFrequency) {
        this.updateFrequency = updateFrequency;
    }

    public String getFormattedTimeOfLastUpdate() {
        return String.valueOf(DateUtils.getRelativeTimeSpanString(timeOfLastUpdateInMilliSec));
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }

    public void setBitmapIcon(Bitmap icon){
        bitmapIcon = icon;
    }

    public Bitmap getBitmapIcon(){
        return bitmapIcon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(contents);
        dest.writeString(pageUrl);
        dest.writeString(nickname);
        dest.writeString(notes);
        dest.writeLong(timeOfLastUpdateInMilliSec);
        dest.writeByte((byte) (isUpdated ? 1 : 0));
        dest.writeByte((byte) (isActive ? 1 : 0));
    }

    public void setIsActive(boolean active){
        this.isActive = active;
    }
    public boolean isActive() {
        return isActive;
    }
}
