package com.leroyjenkins.jose.updated.controller;

import android.os.AsyncTask;

import com.leroyjenkins.jose.updated.model.Page;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.realm.Realm;


public class UpdateRefresher {
    private DatabaseHelper databaseHelper = new DatabaseHelper();
    private List<Page> allPages = databaseHelper.getAllPages();

    public UpdateRefresher() {

    }

    public void refreshUpdate() {
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
                if (htmlToCheck.equals("Error")) {
                    return false;
                }
                if (!htmlToCheck.equals(currentlyStoredPage.getContents())) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    currentlyStoredPage.setUpdated(true);
                    currentlyStoredPage.setContents(htmlToCheck);
                    realm.copyToRealmOrUpdate(currentlyStoredPage);
                    realm.commitTransaction();
                    realm.close();
                    databaseHelper.updatePageOnFirebase(currentlyStoredPage);
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
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        private String TAG = this.getClass().getSimpleName();

        DownloadTask() {

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }
                reader.close();
                return builder.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return "Error";
        }

    }
}
