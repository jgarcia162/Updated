package com.example.jose.updated.controller;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.example.jose.updated.model.Page;
import com.example.jose.updated.view.MainActivity;

import io.realm.Realm;


public class UpdateRefresher {
    private Context context;

    public UpdateRefresher(){

    }

    public UpdateRefresher(Context context){
        this.context = context;
    }

    public void refreshUpdate() {
        RealmDatabaseHelper realmDatabaseHelper = new RealmDatabaseHelper();
        SwipeRefreshLayout swipeRefreshLayout = MainActivity.swipeRefreshLayout;
        for (Page page : realmDatabaseHelper.getAllPages()) {
            if (!page.isUpdated()) {
                    if (isPageUpdated(page)) {
                        realmDatabaseHelper.addToUpdatedPages(page);
                    }
            }
        }
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    private boolean isPageUpdated(Page page){
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

    String downloadHtml(Page page)  {
        DownloadTask task = new DownloadTask();
        task.execute(page.getPageUrl());
        try {

            return task.get();
        } catch (Exception e) {
            Log.d("DOWNLOADTASK", "downloadHtml: Exception");
            e.printStackTrace();
            return "Error Downloading";
        }
    }


}
