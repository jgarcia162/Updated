package com.example.jose.updated.controller;

import android.util.Log;

import com.example.jose.updated.model.Page;
import com.example.jose.updated.view.MainActivity;

import java.util.Date;
import java.util.List;



public class UpdateRefresher {

    public static void refreshUpdate(List<Page> pagesToTrack,List<Page> updated){
        List<Page> pages;
        pages = pagesToTrack;
        for(Page page : pages){
            try {
                if(page.isUpdated() && !updated.contains(page)){
                    updated.add(page);
                    page.setTimeOfLastUpdateInMilliSec(new Date().getTime());
                    MainActivity.notifyAdapterDataSetChange();
                }else{
                    if(updated.contains(page)){
                        updated.remove(page);
                        MainActivity.notifyAdapterDataSetChange();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(updated.size()>0){
            Log.i("PAGES UPDATED ", updated.size()+" pages have been udpated");
        }
    }
}
