package com.example.jose.updated.model;

import android.annotation.SuppressLint;

import com.example.jose.updated.controller.PageAdapter;
import com.example.jose.updated.controller.UpdateRefresher;
import com.example.jose.updated.view.MainActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Joe on 1/14/17.
 */

public class PagesHolder {
    private static PagesHolder instance;
    private List<Page> pagesToTrack;
    private List<Page> updatedPages;
    private Map<String,Page> pagesToTrackMap;
    private Map<String, String> pageHtmlMap;
    @SuppressLint("StaticFieldLeak")
    public static PageAdapter adapter;

    private PagesHolder() {
        this.pagesToTrack = new ArrayList<>();
        this.pagesToTrackMap = new HashMap<>();
        this.updatedPages = new ArrayList<>();
        this.pageHtmlMap = new HashMap<>();
    }

    public static PagesHolder getInstance() {
        if (instance == null) {
            instance = new PagesHolder();
        }
        return instance;
    }

    public List<Page> getPagesToTrack() {
        return pagesToTrack;
    }

    public void setPagesToTrack(List<Page> pagesToTrack) {
        this.pagesToTrack = pagesToTrack;
    }

    public List<Page> getUpdatedPages() {
        return updatedPages;
    }

    public void setUpdatedPages(List<Page> pages) {
            updatedPages = pages;

    }

    public Map<String, String> getPageHtmlMap() {
        return pageHtmlMap;
    }

    public void setPageHtmlMap(Map<String, String> pageHtmlMap) {
        this.pageHtmlMap = pageHtmlMap;
    }

    public void addToUpdatedPages(Page page) {
        if (!updatedPages.contains(page)) {
            page.setUpdated(true);
            page.setTimeOfLastUpdateInMilliSec(new Date().getTime());
            updatedPages.add(page);
        }
    }

    public void addToPagesToTrack(Page page) {
        if (!pageHtmlMap.containsKey(page.getPageUrl())) {
            pagesToTrack.add(page);
            pagesToTrackMap.put(page.getPageUrl(),page);
            try {
                pageHtmlMap.put(page.getPageUrl(), UpdateRefresher.downloadHtml(page));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void removeFromUpdatedPages(Page page) {
        updatedPages.remove(page);
    }

    public void removeFromPagesToTrack(Page page) {
        if (isTrackingPage(page)) {
            removePageHtml(page);
            pagesToTrackMap.remove(page.getPageUrl());
            MainActivity.adapter.notifyDataSetChanged();
        }
    }

    private boolean isTrackingPage(Page page) {
        return pagesToTrackMap.containsKey(page.getPageUrl());
    }

    public void addPageHtmlToMap(Page page) {
        if (!pageHtmlMap.containsKey(page.getPageUrl()))
            pageHtmlMap.put(page.getPageUrl(), page.getContents());
    }

    private void removePageHtml(Page page) {
        pageHtmlMap.remove(page.getPageUrl());
    }

    public int getSizeOfUpdatedPages() {
        return updatedPages.size();
    }

    public int getSizeOfPagesToTrack() {
        return pagesToTrack.size();
    }

    public void initializeMap(){
        for (Page page: pagesToTrack) {
            pagesToTrackMap.put(page.getPageUrl(),page);
        }
    }

}
