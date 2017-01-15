package com.example.jose.updated.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joe on 1/14/17.
 */

public class PagesHolder {
    private static PagesHolder instance;
    private List<Page> pagesToTrack;
    private List<Page> updatedPages;

    private PagesHolder(){
        pagesToTrack = new ArrayList<>();
        updatedPages = new ArrayList<>();
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
}
