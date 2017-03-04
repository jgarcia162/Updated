package com.example.jose.updated.controller;

import com.example.jose.updated.model.Page;

import java.util.Date;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Joe on 1/14/17.
 */

public class RealmDatabaseHelper {
    private Realm realm;

    public RealmDatabaseHelper() {
        realm = Realm.getDefaultInstance();
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
        realm.copyToRealmOrUpdate(page);
    }

    public void addToUpdatedPages(Page page) {
        realm.beginTransaction();
        page.setTimeOfLastUpdateInMilliSec(new Date().getTime());
        page.setUpdated(true);
        realm.copyToRealmOrUpdate(page);
        realm.commitTransaction();
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
        realm.beginTransaction();
        pageToRemove.setUpdated(false);
        realm.copyToRealmOrUpdate(pageToRemove);
        realm.commitTransaction();
    }

    public void removeFromPagesToTrack(Page page) {
        Page pageToDelete = getPage(page);
        realm.beginTransaction();
        pageToDelete.deleteFromRealm();
        realm.commitTransaction();
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
            newPage.setContents(UpdateRefresher.downloadHtml(newPage));
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
    }

    public void savePageSettings(Page page, String title, String notes, boolean isActive) {
        Page pageToUpdate = realm.where(Page.class).equalTo("pageUrl",page.getPageUrl()).findFirst();
        realm.beginTransaction();
        pageToUpdate.setTitle(title);
        pageToUpdate.setNotes(notes);
        pageToUpdate.setIsActive(isActive);
        realm.copyToRealmOrUpdate(pageToUpdate);
        realm.commitTransaction();
    }
}
