package com.example.jose.updated.controller;

import android.content.Context;

import com.example.jose.updated.model.Page;

import java.util.List;

import io.realm.Realm;


public class UpdateRefresher {
    private Context context;
    private String TAG = this.getClass().getSimpleName();

    public UpdateRefresher() {

    }

//    public UpdateRefresher(Context context) {
//        this.context = context;
//    }

    public void refreshUpdate() {
        RealmDatabaseHelper realmDatabaseHelper = new RealmDatabaseHelper();
        List<Page> allPages = realmDatabaseHelper.getAllPages();
        for (Page page : allPages) {
            if (!page.isUpdated()) {
                if (isPageUpdated(page)) {
                    realmDatabaseHelper.addToUpdatedPages(page);
                }
            }
        }

    }

    private boolean isPageUpdated(Page page) {
        RealmDatabaseHelper realmDatabaseHelper = new RealmDatabaseHelper();
        if (page.isActive()) {
            try {
                String htmlToCheck = downloadHtml(page);
                Page currentlyStoredPage = realmDatabaseHelper.getPage(page);
                if (!htmlToCheck.equals(currentlyStoredPage.getContents())) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    page.setUpdated(true);
                    realm.commitTransaction();
                    realmDatabaseHelper.updatePageHtml(currentlyStoredPage, htmlToCheck);
                    realm.close();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    String downloadHtml(Page page) {
        DownloadTask task = new DownloadTask();
        task.execute(page.getPageUrl());
        try {
            return task.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
