package com.example.jose.updated.controller;

import com.example.jose.updated.model.Page;
import com.example.jose.updated.model.RealmDatabaseHelper;


public class UpdateRefresher {
    private static RealmDatabaseHelper realmDatabaseHelper = new RealmDatabaseHelper();

    public static void refreshUpdate() throws Exception {
        for (Page page : realmDatabaseHelper.getAllPages()) {
            if (!page.isUpdated()) {
                if (isPageUpdated(page)) {
                    realmDatabaseHelper.addToUpdatedPages(page);
                }
            }
        }
    }

    private static boolean isPageUpdated(Page page) throws Exception {
        if (page.isActive()) {
            String htmlToCheck = downloadHtml(page);
            Page currentlyStoredPage = realmDatabaseHelper.getPage(page);
            if (!htmlToCheck.equals(currentlyStoredPage.getContents())) {
                realmDatabaseHelper.updatePageHtml(currentlyStoredPage, htmlToCheck);
                return true;
            }
        }
        return false;
    }

    public static String downloadHtml(Page page) throws Exception {
        DownloadTask task = new DownloadTask();
        task.execute(page.getPageUrl());
        return task.get();
    }


}
