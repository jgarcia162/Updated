package com.example.jose.updated.controller;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class DownloadTask extends AsyncTask<String,Void,String>{

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
        return null;
    }

    @Override
    protected void onPostExecute(String taskResult) {

    }

}
