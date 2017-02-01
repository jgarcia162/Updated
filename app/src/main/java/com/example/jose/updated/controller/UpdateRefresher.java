package com.example.jose.updated.controller;

import android.content.Context;

import com.example.jose.updated.model.Page;
import com.example.jose.updated.model.PagesHolder;

import java.util.Date;



public class UpdateRefresher {
    private static PagesHolder pagesHolder = PagesHolder.getInstance();

    public static void refreshUpdate(Context context) throws Exception {
        for(Page page : pagesHolder.getPagesToTrack()){
            if(isPageUpdated(page)) {
                page.setUpdated(true);
                if (!pagesHolder.getUpdatedPages().contains(page)) {
                    pagesHolder.addToUpdatedPages(page);
                    page.setTimeOfLastUpdateInMilliSec(new Date().getTime());
                    //MainActivity.notifyAdapterDataSetChange(context);
                }
            }
        }
    }

    private static boolean isPageUpdated(Page page) throws Exception{
        String htmlToCheck = downloadHtml(page);
        return htmlToCheck.equals(pagesHolder.getPageHtmlMap().get(page.getPageUrl()));
    }

    public static String downloadHtml(Page page) throws Exception {
        DownloadTask task = new DownloadTask();
        task.execute(page.getPageUrl());
        page.setContents(task.get());
        return task.get();
    }
}
