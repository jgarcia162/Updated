package com.example.jose.updated.controller;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.jose.updated.model.Page;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Joe on 1/14/17.
 */

public class DatabaseHelper {
    private Context context;
    private UpdateRefresher refresher;

    public DatabaseHelper() {
        refresher = new UpdateRefresher();
    }

    public DatabaseHelper(Context context) {
        this.context = context;
    }

    public List<Page> getAllPages() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(Page.class).findAll();
    }

    public List<Page> getPagesToTrack() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(Page.class).equalTo("isActive", true).findAll();
    }

    public List<Page> getUpdatedPages() {
        Realm realm = Realm.getDefaultInstance();
        List<Page> updatedPages = realm.where(Page.class).equalTo("isUpdated", true).findAll();
        return updatedPages;
    }

    public void addToAllPages(Page page) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(page);
        realm.commitTransaction();
        realm.close();
        addToPagesToTrack(page);
        updateFirebaseContents();
    }

    public void updateFirebaseContents() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("pages").setValue(getAllPages());
                }
                Log.d("DONE ADDING", "doInBackground: ");
            }
        };
        handler.post(runnable);
    }

    public void addToUpdatedPages(Page page) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        page.setTimeOfLastUpdateInMilliSec(new Date().getTime());
        page.setUpdated(true);
        realm.copyToRealmOrUpdate(page);
        realm.commitTransaction();
        List<Page> updatedPages = realm.where(Page.class).equalTo("isUpdated", true).findAll();
        realm.close();
        setUpdatedPages(updatedPages);
    }

    public void addToPagesToTrack(Page page) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        try {
            page.setContents(refresher.downloadHtml(page));
            realm.copyToRealmOrUpdate(page);
        } catch (Exception e) {
            e.printStackTrace();
            realm.close();
        } finally {
            realm.commitTransaction();
            realm.close();
            updateFirebaseContents();
        }
    }

    public void removeFromUpdatedPages(Page page) {
        Realm realm = Realm.getDefaultInstance();
        Page pageToRemove = realm.where(Page.class)
                .equalTo("title", page.getTitle())
                .findFirst();
        realm.beginTransaction();
        pageToRemove.setUpdated(false);
        realm.copyToRealmOrUpdate(pageToRemove);
        realm.commitTransaction();
        realm.close();
    }

    public void removeFromPagesToTrack(Page page) {
        Realm realm = Realm.getDefaultInstance();
        Page pageToDelete = getPage(page);
        realm.beginTransaction();
        pageToDelete.setIsActive(false);
        realm.commitTransaction();
        realm.close();
        updateFirebaseContents();

    }

    public void deletePage(Page page) {
        Realm realm = Realm.getDefaultInstance();
        Page pageToDelete = getPage(page);
        realm.beginTransaction();
        pageToDelete.deleteFromRealm();
        realm.commitTransaction();
        realm.close();
        updateFirebaseContents();
    }

    public int getSizeOfUpdatedPages() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(Page.class).equalTo("isUpdated", true).findAll().size();
    }

    public int getSizeOfPagesToTrack() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(Page.class).findAll().size();
    }

    public void deactivatePage(Page page) {
        Realm realm = Realm.getDefaultInstance();
        page = realm.where(Page.class).equalTo("title", page.getTitle()).findFirst();
        realm.beginTransaction();
        page.setIsActive(false);
        realm.copyToRealmOrUpdate(page);
        realm.commitTransaction();
        realm.close();
    }

    public Page getPage(Page page) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Page pageToReturn = realm.where(Page.class).equalTo("pageUrl", page.getPageUrl()).findFirst();
        realm.commitTransaction();
        realm.close();
        return pageToReturn;
    }

    public void setUpdatedPages(List<Page> updatedPages) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(updatedPages);
        realm.commitTransaction();
        realm.close();
    }

    public void createPage(String titleText, String urlText, long time) {
        Page newPage = new Page(titleText, urlText, time);
        try {
            newPage.setContents(refresher.downloadHtml(newPage));
            newPage.setTimeOfLastUpdateInMilliSec(new Date().getTime());
            newPage.setIsActive(true);
            addToPagesToTrack(newPage);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Page getPageFromUrl(String pageUrl) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(Page.class).equalTo("pageUrl", pageUrl).findFirst();
    }

    public void updatePageHtml(Page page, String html) {
        Realm realm = Realm.getDefaultInstance();
        Page pageToUpdate = realm.where(Page.class).equalTo("pageUrl", page.getPageUrl()).findFirst();
        realm.beginTransaction();
        pageToUpdate.setContents(html);
        realm.copyToRealmOrUpdate(pageToUpdate);
        realm.commitTransaction();
        realm.close();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            updateFirebaseContents();
        }
    }

    public void savePageSettings(Page page, String title, String notes, boolean isActive) {
        Realm realm = Realm.getDefaultInstance();
        Page pageToUpdate = realm.where(Page.class).equalTo("pageUrl", page.getPageUrl()).findFirst();
        realm.beginTransaction();
        pageToUpdate.setTitle(title);
        pageToUpdate.setNotes(notes);
        pageToUpdate.setIsActive(isActive);
        realm.copyToRealmOrUpdate(pageToUpdate);
        realm.commitTransaction();
        realm.close();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            updateFirebaseContents();
        }
    }

    public void emptyDatabase() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
        realm.close();
    }
}