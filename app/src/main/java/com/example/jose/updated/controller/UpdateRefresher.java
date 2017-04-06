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
        DatabaseHelper databaseHelper = new DatabaseHelper();
        List<Page> allPages = databaseHelper.getAllPages();
        for (Page page : allPages) {
            if (!page.isUpdated()) {
                if (isPageUpdated(page)) {
                    databaseHelper.addToUpdatedPages(page);
                }
            }
        }
    }

    private boolean isPageUpdated(Page page) {
        DatabaseHelper databaseHelper = new DatabaseHelper();
        if (page.isActive()) {
            try {
                String htmlToCheck = downloadHtml(page);
                Page currentlyStoredPage = databaseHelper.getPage(page);
                if(htmlToCheck.equals("Error")){
                    return false;
                }
                if (!htmlToCheck.equals(currentlyStoredPage.getContents())) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    currentlyStoredPage.setUpdated(true);
                    realm.commitTransaction();
                    realm.close();
                    databaseHelper.addToUpdatedPages(currentlyStoredPage);
                    databaseHelper.updatePageHtml(currentlyStoredPage, htmlToCheck);
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
