package com.leroyjenkins.jose.updated.controller;

import android.os.AsyncTask;

import com.leroyjenkins.jose.updated.model.Page;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import io.realm.Realm;


public class UpdateRefresher {

    public UpdateRefresher() {

    }

    public void refreshUpdate() {
        DatabaseHelper databaseHelper = new DatabaseHelper();
        List<Page> allPages = databaseHelper.getAllPages();
        for (Page page : allPages) {
            if (!page.isUpdated()) {
                if (isPageUpdated(page)) {
                    databaseHelper.addToUpdatedPages(page);
                }
            }
        }
    }

    private boolean isPageUpdated(Page page) {
        DatabaseHelper databaseHelper = new DatabaseHelper();
        if (page.isActive()) {
            try {
                String htmlToCheck = downloadHtml(page);
                Page currentlyStoredPage = databaseHelper.getPage(page);
                if(htmlToCheck.equals("Error")){
                    return false;
                }
                if (!htmlToCheck.equals(currentlyStoredPage.getContents())) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    currentlyStoredPage.setUpdated(true);
                    realm.commitTransaction();
                    realm.close();
                    databaseHelper.addToUpdatedPages(currentlyStoredPage);
                    databaseHelper.updatePageHtml(currentlyStoredPage, htmlToCheck);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    String downloadHtml(Page page) {
        DownloadTask task = new DownloadTask();
        task.execute(page.getPageUrl());
        try {
            return task.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private class DownloadTask extends AsyncTask<String,Void,String> {

        private String TAG = this.getClass().getSimpleName();

        DownloadTask(){

        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                URL url = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                StringBuilder builder = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null){
                    builder.append(line).append("\n");
                }
                reader.close();
                return builder.toString();
            }catch(Exception e){
                e.printStackTrace();
            }
            return "Error";
        }

        @Override
        protected void onPostExecute(String taskResult) {

        }
    }
}
