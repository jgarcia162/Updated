package com.example.jose.updated.view;

import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.jose.updated.R;
import com.example.jose.updated.controller.PageAdapter;
import com.example.jose.updated.model.Page;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainFragmentActivity extends FragmentActivity {
    FragmentManager fragmentManager;
    EditText urlInputEditText;
    Button updateButton;
    String URL_TO_UPDATE = "http://store.nike.com/us/en_us/pw/mens-nikelab/7puZofo?cp=usns_kw_nike_http://store.nike.com/_txt!g!c!br!e!nikelab&k_clickid=a2ae1b8d-ac63-4a65-8520-b1584b4fcf86&ipp=68";
    Page newPage;
    PageAdapter adapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    static List<Page> pagesToTrack;
    Date date;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity_main);

        fragmentManager = getFragmentManager();
        date = new Date();
        updateButton = (Button) findViewById(R.id.add_page_button);
        urlInputEditText = (EditText) findViewById(R.id.url_input_edit_text);
        pagesToTrack = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);

        adapter = new PageAdapter(pagesToTrack);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

//        updateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(!urlInputEditText.getText().toString().equalsIgnoreCase("")){
//                    pagesToTrack.add(new Page("New Page",urlInputEditText.getText().toString(),date.getTime()));
//                    adapter.notifyDataSetChanged();
//                }
//                //new DownloadTask().execute(newPage.getPageUrl());
//            }
//        });


    }

    private boolean isPageUpdated(String pageSourceCode) throws Exception{
        URL urlToCheckFor = new URL(URL_TO_UPDATE);
        HttpURLConnection connection = (HttpURLConnection) urlToCheckFor.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null){
            stringBuilder.append(line).append("\n");
        }
        reader.close();

        return stringBuilder.toString().equals(pageSourceCode);
    }

    public void showAddPageDialog(View view) {
        AddPageDialogFragment addPageDialogFragment = new AddPageDialogFragment();
        addPageDialogFragment.show(fragmentManager,"addPageFragment");
    }

    private class DownloadTask extends AsyncTask<String,Void,String>{

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
                Log.e("ASYNC RESULT: ", "Download failed");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String taskResult) {
            newPage.setContents(taskResult);
            pagesToTrack.add(newPage);

        }
    }
}

