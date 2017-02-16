package com.example.jose.updated.controller;

import com.example.jose.updated.model.Page;
import com.example.jose.updated.model.PagesHolder;

import java.util.ArrayList;


public class UpdateRefresher {
    private static PagesHolder pagesHolder = PagesHolder.getInstance();

    static void refreshUpdate() throws Exception {
        ArrayList<Page> pages = new ArrayList<>();
        for (Page page : pagesHolder.getPagesToTrack()) {
            if (isPageUpdated(page)) {
                pages.add(page);
            }
        }
        for (Page page : pages) {
            pagesHolder.addToUpdatedPages(page);
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
