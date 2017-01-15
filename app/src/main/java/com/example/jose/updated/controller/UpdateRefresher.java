package com.example.jose.updated.controller;

import android.util.Log;

import com.example.jose.updated.model.Page;
import com.example.jose.updated.model.PagesHolder;
import com.example.jose.updated.view.MainActivity;

import java.util.Date;
import java.util.List;



public class UpdateRefresher {
    private static List<Page> pagesToTrack = PagesHolder.getInstance().getPagesToTrack();
    private static List<Page> updatedPages = PagesHolder.getInstance().getUpdatedPages();

    public static void refreshUpdate(){
        for(Page page : pagesToTrack){
            try {
                if(page.isUpdated() && !updatedPages.contains(page)){
                    updatedPages.add(page);
                    page.setTimeOfLastUpdateInMilliSec(new Date().getTime());
                    MainActivity.notifyAdapterDataSetChange();
                }else{
                    if(updatedPages.contains(page)){
                        updatedPages.remove(page);
                        MainActivity.notifyAdapterDataSetChange();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(updatedPages.size()>0){
            Log.i("PAGES UPDATED ", updatedPages.size() + " pages have been udpated");
        }
    }
}
