package com.leroyjenkins.jose.updated.controller;

import android.content.Context;
import android.os.Handler;

import com.leroyjenkins.jose.updated.model.Page;
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
    private Realm realm;
    private DatabaseReference databaseReference;

    public DatabaseHelper() {
        refresher = new UpdateRefresher();
        realm = Realm.getDefaultInstance();
    }

    public DatabaseHelper(Context context) {
        this.context = context;
    }

    public List<Page> getAllPages() {
        return realm.where(Page.class).findAll();
    }

    public List<Page> getPagesToTrack() {
        return realm.where(Page.class).equalTo("isActive", true).findAll();
    }

    public List<Page> getUpdatedPages() {
        return realm.where(Page.class).equalTo("isUpdated", true).findAll();
    }

    public void addToAllPages(Page page) {
        final Page pageToAdd = page;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(pageToAdd);
            }
        });
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            addPageToFirebase(page);
        }
    }

    public void addToAllPagesFromFirebase(Page page) {
        final Page pageToAdd = page;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(pageToAdd);
            }
        });
    }

    public void updatePageOnFirebase(final Page page) {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                databaseReference = FirebaseDatabase.getInstance().getReference();
                String key = page.getFirebaseKey();
                DatabaseReference pageRef = databaseReference.child(key);
                pageRef.setValue(page);
            }
        };
        handler.post(runnable);
    }

    public void addPageToFirebase(final Page page) {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    databaseReference = FirebaseDatabase.getInstance().getReference();
                    String key = databaseReference.push().getKey();
                    page.setFirebaseKey(key);
                    DatabaseReference pageRef = databaseReference.child(key);
                    pageRef.setValue(page);
                }
            }
        };
        handler.post(runnable);
    }

    public void addToUpdatedPages(Page page) {
        final Page pageToAdd = page;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                pageToAdd.setTimeOfLastUpdateInMilliSec(new Date().getTime());
                pageToAdd.setUpdated(true);
                realm.copyToRealmOrUpdate(pageToAdd);
                List<Page> updatedPages = realm.where(Page.class).equalTo("isUpdated", true).findAll();
                setUpdatedPages(updatedPages);
            }
        });
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
        }
    }

    public void removeFromUpdatedPages(Page page) {
        if (page.isUpdated()) {
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
    }

    public void removeFromPagesToTrack(Page page) {
        Realm realm = Realm.getDefaultInstance();
        Page pageToDelete = getPage(page);
        realm.beginTransaction();
        pageToDelete.setIsActive(false);
        realm.commitTransaction();
        realm.close();
    }

    public void deletePage(Page page) {
        Realm realm = Realm.getDefaultInstance();
        Page pageToDelete = getPage(page);
        String key = pageToDelete.getFirebaseKey();
        realm.beginTransaction();
        pageToDelete.deleteFromRealm();
        realm.commitTransaction();
        realm.close();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            deleteFromFirebase(key);
        }
    }

    private void deleteFromFirebase(String key) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(key).removeValue();
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
            addToAllPages(newPage);
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
            updatePageOnFirebase(pageToUpdate);
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
            updatePageOnFirebase(pageToUpdate);
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