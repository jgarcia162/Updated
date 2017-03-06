package com.example.jose.updated.controller;

import android.support.v4.widget.SwipeRefreshLayout;

import com.example.jose.updated.model.Page;
import com.example.jose.updated.view.MainActivity;

import io.realm.Realm;


public class UpdateRefresher {

    public static void refreshUpdate() throws Exception {
        RealmDatabaseHelper realmDatabaseHelper = new RealmDatabaseHelper();
        SwipeRefreshLayout swipeRefreshLayout = MainActivity.swipeRefreshLayout;
        for (Page page : realmDatabaseHelper.getAllPages()) {
            if (!page.isUpdated()) {
                if (isPageUpdated(page)) {
                    realmDatabaseHelper.addToUpdatedPages(page);
                }
            }
        }
        if(swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    private static boolean isPageUpdated(Page page) throws Exception {
        RealmDatabaseHelper realmDatabaseHelper = new RealmDatabaseHelper();
        if (page.isActive()) {
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
        }
        return false;
    }

    private static String downloadHtml(Page page) throws Exception {
        DownloadTask task = new DownloadTask();
        task.execute(page.getPageUrl());
        return task.get();
    }


}
