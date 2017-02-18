package com.example.jose.updated.controller;

import com.example.jose.updated.model.Page;
import com.example.jose.updated.model.PagesHolder;


public class UpdateRefresher {
    private static PagesHolder pagesHolder = PagesHolder.getInstance();

    static void refreshUpdate() throws Exception {
        for (Page page : pagesHolder.getPagesToTrack()) {
            if (!page.isUpdated()) {
                if (isPageUpdated(page)) {
                    page.setUpdated(true);
                    pagesHolder.addToUpdatedPages(page);
                }
            }
        }
    }

    private static boolean isPageUpdated(Page page) throws Exception {
        if (page.isActive()) {
            String htmlToCheck = downloadHtml(page);
            return htmlToCheck.equals(pagesHolder.getPageHtmlMap().get(page.getPageUrl()));
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
