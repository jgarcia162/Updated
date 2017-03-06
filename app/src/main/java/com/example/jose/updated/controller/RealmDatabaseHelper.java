package com.example.jose.updated.controller;

import android.content.Context;

import com.example.jose.updated.model.Page;

import java.util.Date;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Joe on 1/14/17.
 */

public class RealmDatabaseHelper {
    private Realm realm;
    private Context context;
    private UpdateRefresher refresher;

    public RealmDatabaseHelper() {
        realm = Realm.getDefaultInstance();
        refresher = new UpdateRefresher();
    }

    public RealmDatabaseHelper(Context context) {
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
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(page);
        realm.commitTransaction();
        realm.close();
    }

    public void addToUpdatedPages(Page page) {
        //TODO attempt this with executeTransaction method
        realm.beginTransaction();
        page.setTimeOfLastUpdateInMilliSec(new Date().getTime());
        page.setUpdated(true);
        realm.copyToRealmOrUpdate(page);
        realm.commitTransaction();
        realm.close();
    }

    public void addToPagesToTrack(Page page) {
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
        Page pageToRemove = realm.where(Page.class)
                .equalTo("isUpdated", true)
                .equalTo("title", page.getTitle())
                .findFirst();
        realm.beginTransaction();
        pageToRemove.setUpdated(false);
        realm.copyToRealmOrUpdate(pageToRemove);
        realm.commitTransaction();
        realm.close();
    }

    public void removeFromPagesToTrack(Page page) {
        Page pageToDelete = getPage(page);
        realm.beginTransaction();
        pageToDelete.deleteFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    public int getSizeOfUpdatedPages() {
        return realm.where(Page.class).equalTo("isUpdated", true).findAll().size();
    }

    public int getSizeOfPagesToTrack() {
        return realm.where(Page.class).findAll().size();
    }

    public void deactivatePage(Page page) {
        page = realm.where(Page.class).equalTo("title", page.getTitle()).findFirst();
        realm.beginTransaction();
        page.setIsActive(!page.isActive());
        realm.copyToRealmOrUpdate(page);
        realm.commitTransaction();
        realm.close();
    }

    public Page getPage(Page page) {
        realm.beginTransaction();
        Page pageToReturn = realm.where(Page.class).equalTo("pageUrl", page.getPageUrl()).findFirst();
        realm.commitTransaction();
        return pageToReturn;
    }

    public void setUpdatedPages(List<Page> updatedPages) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(updatedPages);
        realm.commitTransaction();
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
        return realm.where(Page.class).equalTo("pageUrl", pageUrl).findFirst();
    }

    public void updatePageHtml(Page page, String html) {
        Page pageToUpdate = realm.where(Page.class).equalTo("pageUrl", page.getPageUrl()).findFirst();
        realm.beginTransaction();
        pageToUpdate.setContents(html);
        realm.copyToRealmOrUpdate(pageToUpdate);
        realm.commitTransaction();
        realm.close();
    }

    public void savePageSettings(Page page, String title, String notes, boolean isActive) {
        Page pageToUpdate = realm.where(Page.class).equalTo("pageUrl", page.getPageUrl()).findFirst();
        realm.beginTransaction();
        pageToUpdate.setTitle(title);
        pageToUpdate.setNotes(notes);
        pageToUpdate.setIsActive(isActive);
        realm.copyToRealmOrUpdate(pageToUpdate);
        realm.commitTransaction();
        realm.close();
    }
}
