package com.example.jose.updated.model;

import com.example.jose.updated.controller.UpdateRefresher;
import com.example.jose.updated.view.MainActivity;

import java.util.Date;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Joe on 1/14/17.
 */

public class RealmDatabaseHelper {
    private static RealmDatabaseHelper instance;
    private Realm realm = Realm.getDefaultInstance();

    private RealmDatabaseHelper() {

    }

    public static RealmDatabaseHelper getInstance() {
        if (instance == null) {
            instance = new RealmDatabaseHelper();
        }
        return instance;
    }

    public List<Page> getPagesToTrack() {
        return realm.where(Page.class).findAll();
    }

    public List<Page> getUpdatedPages() {
        return realm.where(Page.class).equalTo("isUpdated", true).findAll();
    }

    public void addToUpdatedPages(Page page) {
        realm.beginTransaction();
        page.setTimeOfLastUpdateInMilliSec(new Date().getTime());
        realm.copyToRealmOrUpdate(page);
    }

    public void addToPagesToTrack(Page page) {
        realm.beginTransaction();
        try {
            page.setContents(UpdateRefresher.downloadHtml(page));
            realm.copyToRealmOrUpdate(page);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            realm.commitTransaction();
        }
    }

    public void removeFromUpdatedPages(Page page) {
        Page pageToRemove = realm.where(Page.class)
                .equalTo("isUpdated", true)
                .equalTo("title", page.getTitle())
                .findFirst();
        pageToRemove.setUpdated(false);
        realm.copyToRealmOrUpdate(pageToRemove);
        realm.commitTransaction();
    }

    public void removeFromPagesToTrack(Page page) {
        realm.beginTransaction();
        page.deleteFromRealm();
        MainActivity.adapter.notifyDataSetChanged();
    }

    public int getSizeOfUpdatedPages() {
        return realm.where(Page.class).equalTo("isUpdated",true).findAll().size();
    }

    public int getSizeOfPagesToTrack() {
        return realm.where(Page.class).findAll().size();
    }

}
