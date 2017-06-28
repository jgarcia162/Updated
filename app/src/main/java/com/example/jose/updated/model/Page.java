package com.example.jose.updated.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Page extends RealmObject implements Parcelable{

    @PrimaryKey
    private long idKey;
    private String firebaseKey;
    private String user;
    private String pageUrl;
    private String title;
    private String contents;
    private String nickname;
    private String notes;
    private String formattedTimeOfLastUpdate;
    private long updateFrequency;
    private long timeOfLastUpdateInMilliSec;
    private boolean active;
    private boolean isUpdated;
    private boolean managed;
    private boolean loaded;
    private boolean valid;


    public long getIdKey() {
        return idKey;
    }

    public void setIdKey(long idKey) {
        this.idKey = idKey;
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setFormattedTimeOfLastUpdate(String formattedTimeOfLastUpdate) {
        this.formattedTimeOfLastUpdate = formattedTimeOfLastUpdate;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setManaged(boolean managed) {
        this.managed = managed;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }




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
        idKey = in.readInt();
        firebaseKey = in.readString();
        user = in.readString();
        title = in.readString();
        contents = in.readString();
        pageUrl = in.readString();
        nickname = in.readString();
        notes = in.readString();
        timeOfLastUpdateInMilliSec = in.readLong();
        isUpdated = in.readByte() != 0;
        active = in.readByte() != 0;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(idKey);
        dest.writeString(firebaseKey);
        dest.writeString(user);
        dest.writeString(title);
        dest.writeString(contents);
        dest.writeString(pageUrl);
        dest.writeString(nickname);
        dest.writeString(notes);
        dest.writeLong(timeOfLastUpdateInMilliSec);
        dest.writeByte((byte) (isUpdated ? 1 : 0));
        dest.writeByte((byte) (active ? 1 : 0));
    }

    @Exclude
    public Map<String, Object> toMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("idKey",idKey);
        map.put("firebaseKey",firebaseKey);
        map.put("user",user);
        map.put("pageUrl",pageUrl);
        map.put("title",title);
        map.put("contents",contents);
        map.put("nickname",nickname);
        map.put("notes",notes);
        map.put("formattedTimeOfLastUpdate",formattedTimeOfLastUpdate);
        map.put("updateFrequency",updateFrequency);
        map.put("timeOfLastUpdateInMilliSec",timeOfLastUpdateInMilliSec);
        map.put("active",active);
        map.put("isUpdated",isUpdated);
        return map;
    }

    public void setIsActive(boolean active){
        this.active = active;
    }
    public boolean isActive() {
        return active;
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }
}
