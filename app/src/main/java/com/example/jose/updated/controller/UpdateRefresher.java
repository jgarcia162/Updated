package com.example.jose.updated.controller;

import android.util.Log;

import com.example.jose.updated.model.Page;
import com.example.jose.updated.model.PagesHolder;



public class UpdateRefresher {
    private static PagesHolder pagesHolder = PagesHolder.getInstance();

    static void refreshUpdate() throws Exception {
        for(Page page : pagesHolder.getPagesToTrack()){
            if(isPageUpdated(page)) {
                pagesHolder.addToUpdatedPages(page);
            }
        }
        Log.d("Updated pages:",String.valueOf(pagesHolder.getUpdatedPages().size()));
    }

    private static boolean isPageUpdated(Page page) throws Exception{
        String htmlToCheck = downloadHtml(page);
        return htmlToCheck.equals(pagesHolder.getPageHtmlMap().get(page.getPageUrl()));
    }

    public static String downloadHtml(Page page) throws Exception {
        DownloadTask task = new DownloadTask();
        task.execute(page.getPageUrl());
        page.setContents(task.get());
        return page.getContents();
    }
}
