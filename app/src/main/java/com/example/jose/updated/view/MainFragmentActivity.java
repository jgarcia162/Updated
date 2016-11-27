package com.example.jose.updated.view;

import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jose.updated.R;
import com.example.jose.updated.controller.PageAdapter;
import com.example.jose.updated.model.Page;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFragmentActivity extends FragmentActivity {
    private FragmentManager fragmentManager;
    private EditText urlInputEditText;
    private Button updateButton;
    String URL_TO_UPDATE = "http://store.nike.com/us/en_us/pw/mens-nikelab/7puZofo?cp=usns_kw_nike_http://store.nike.com/_txt!g!c!br!e!nikelab&k_clickid=a2ae1b8d-ac63-4a65-8520-b1584b4fcf86&ipp=68";
    Page newPage;
    public static PageAdapter adapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    public static List<Page> pagesToTrack;
    public static Map<String,String> pageShaMap;
    Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity_main);

        fragmentManager = getFragmentManager();
        date = new Date();
        updateButton = (Button) findViewById(R.id.track_page_button);
        urlInputEditText = (EditText) findViewById(R.id.url_input_edit_text);
        pagesToTrack = new ArrayList<>();
        pageShaMap = new HashMap<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);

        adapter = new PageAdapter(pagesToTrack);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

    }


    public void refresh(View view){
        try {
            if(isPageUpdated(pagesToTrack.get(0))){
                Toast.makeText(this, "page was updated!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "not yet", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO Finish this method to find if page is updated
    private boolean isPageUpdated(Page page) throws Exception{
        String hmtlToCheck = downloadHtml(page);
        return hmtlToCheck.equals(pageShaMap.get(page.getPageUrl()));
    }

    public static String downloadHtml(Page page) throws Exception {
        DownloadTask task = new DownloadTask();
        task.execute(page.getPageUrl());
        Log.i("TASK ",task.get());
        return task.get();
    }



    public void showAddPageDialog(View view) {
        AddPageDialogFragment addPageDialogFragment = new AddPageDialogFragment();
        addPageDialogFragment.show(fragmentManager,"addPageFragment");
    }

    static class DownloadTask extends AsyncTask<String,Void,String> {
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

        }
    }

}

