package com.example.jose.updated.controller;

import com.example.jose.updated.model.Page;
import com.example.jose.updated.model.RealmDatabaseHelper;


public class UpdateRefresher {
    private static RealmDatabaseHelper realmDatabaseHelper = RealmDatabaseHelper.getInstance();

    public static void refreshUpdate() throws Exception {
        for (Page page : realmDatabaseHelper.getPagesToTrack()) {
            if (!page.isUpdated()) {
                if (isPageUpdated(page)) {
                    page.setUpdated(true);
                    realmDatabaseHelper.addToUpdatedPages(page);
                }
            }
        }
    }

    private static boolean isPageUpdated(Page page) throws Exception {
        if (page.isActive()) {
            String htmlToCheck = downloadHtml(page);
            return htmlToCheck.equals(realmDatabaseHelper.getPageHtmlMap().get(page.getPageUrl()));
        }
        return false;
    }

    public static String downloadHtml(Page page) throws Exception {
        DownloadTask task = new DownloadTask();
        task.execute(page.getPageUrl());
        page.setContents(task.get());
        return task.get();
    }


}
