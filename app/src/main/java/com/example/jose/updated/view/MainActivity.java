package com.example.jose.updated.view;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.jose.updated.R;
import com.example.jose.updated.controller.DownloadTask;
import com.example.jose.updated.controller.NotificationService;
import com.example.jose.updated.controller.PageAdapter;
import com.example.jose.updated.controller.UpdateRefresher;
import com.example.jose.updated.model.Page;
import com.example.jose.updated.model.PagesHolder;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    private FragmentManager fragmentManager;
    private EditText urlInputEditText;
    private Button updateButton;
    private Page newPage;
    public static PageAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public static  List<Page> pagesToTrack;
    public static  List<Page> updatedPages;
    public static Map<String,String> pageHtmlMap;
    private AddPageDialogFragment addPageDialogFragment;
    private PagesHolder pagesHolder = PagesHolder.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity_main);

        fragmentManager = getFragmentManager();
        updateButton = (Button) findViewById(R.id.track_page_button);
        urlInputEditText = (EditText) findViewById(R.id.url_input_edit_text);
        pagesToTrack = pagesHolder.getPagesToTrack();
        updatedPages = pagesHolder.getUpdatedPages();
        pageHtmlMap = new HashMap<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        addPageDialogFragment = new AddPageDialogFragment();


        adapter = new PageAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        Page nike = new Page("Nike","https://www.nike.com/us/en_us/", new Date().getTime());
        Page twitter = new Page("Twitter","https://twitter.com/AyoJoanks", new Date().getTime());
        Page inward = new Page("Inward","https://inwardmovement.wordpress.com",new Date().getTime());
        Page adidas = new Page("Adidas","https://www.adidas.com",new Date().getTime());
        Page mlb = new Page("MLB","https://www.mlb.com",new Date().getTime());
        Page espn = new Page("ESPN","https://www.espn.com",new Date().getTime());

        //for testing
        pagesToTrack.add(twitter);
        pagesToTrack.add(inward);
        pagesToTrack.add(nike);
        pagesToTrack.add(adidas);
        pagesToTrack.add(mlb);
        pagesToTrack.add(espn);

        Intent serviceIntent = new Intent(this,NotificationService.class);
        startService(serviceIntent);
    }

    public void refresh(View view){
        UpdateRefresher.refreshUpdate();
    }

    private boolean isPageUpdated(Page page) throws Exception{
        String hmtlToCheck = downloadHtml(page);
        return hmtlToCheck.equals(pageHtmlMap.get(page.getPageUrl()));
    }

    public static String downloadHtml(Page page) throws Exception {
        DownloadTask task = new DownloadTask();
        task.execute(page.getPageUrl());
        page.setContents(task.get());
        return task.get();
    }

    public void showAddPageDialog(View view) {
        addPageDialogFragment.show(fragmentManager,"addPageFragment");
    }

    public static void notifyAdapterDataSetChange(){
        adapter.notifyDataSetChanged();
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return super.registerReceiver(receiver, filter);
    }
}

