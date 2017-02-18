package com.example.jose.updated.model;

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
    private Map<String,String> pageHtmlMap;

    private PagesHolder(){
        pagesToTrack = new ArrayList<>();
        updatedPages = new ArrayList<>();
        pageHtmlMap = new HashMap<>();
    }

    public static PagesHolder getInstance() {
        if(instance == null){
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

    public void setUpdatedPages(List<Page> updatedPages) {
        this.updatedPages = updatedPages;
    }

    public Map<String,String> getPageHtmlMap(){
        return pageHtmlMap;
    }

    public void setPageHtmlMap(Map<String,String> pageHtmlMap){
        this.pageHtmlMap = pageHtmlMap;
    }

    public void addToUpdatedPages(Page page){
        if(!updatedPages.contains(page)){
            page.setUpdated(true);
            page.setTimeOfLastUpdateInMilliSec(new Date().getTime());
            updatedPages.add(page);
        }
    }

    public void addToPagesToTrack(Page page){
        if(!pagesToTrack.contains(page))
            pagesToTrack.add(page);
        else{
            pagesToTrack.remove(page);
            pagesToTrack.add(page);
        }
    }

    public void removeFromUpdatedPages(Page page) {
        updatedPages.remove(page);
    }

    public void removeFromPagesToTrack(Page page) {
        pagesToTrack.remove(page);
    }

    public boolean isTrackingPage(Page page){
        return pagesToTrack.contains(page);
    }

    public void addPageHtmlToMap(Page page) {
        pageHtmlMap.put(page.getPageUrl(),page.getContents());
    }

    public int getSizeOfUpdatedPages() {
        return updatedPages.size();
    }

    public int getSizeOfPagesToTrack(){
        return pagesToTrack.size();
    }
}
