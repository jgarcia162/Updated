package com.example.jose.updated.view;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.jose.updated.R;
import com.example.jose.updated.controller.NotificationService;
import com.example.jose.updated.controller.PageAdapter;
import com.example.jose.updated.controller.UpdateBroadcastReceiver;
import com.example.jose.updated.controller.UpdateRefresher;
import com.example.jose.updated.model.Page;
import com.example.jose.updated.model.PagesHolder;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UpdateBroadcastReceiver.UpdatedCallback{
    private FragmentManager fragmentManager;
    private EditText urlInputEditText;
    private Page newPage;
    public static PageAdapter adapter;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager layoutManager;
    public static List<Page> pagesToTrack;
    private AddPageDialogFragment addPageDialogFragment;
    private PagesHolder pagesHolder = PagesHolder.getInstance();
    private LocalBroadcastManager localBroadcastManager;
    private ProgressBar progressBar;
    static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity_main);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        Log.d(this.getLocalClassName(),"ONCREATE");
        fragmentManager = getFragmentManager();
        urlInputEditText = (EditText) findViewById(R.id.url_input_edit_text);
        progressBar = (ProgressBar) findViewById(R.id.main_activity_progress_bar);
        pagesToTrack = pagesHolder.getPagesToTrack();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        addPageDialogFragment = new AddPageDialogFragment();
        pagesHolder = PagesHolder.getInstance();
        localBroadcastManager.registerReceiver(new UpdateBroadcastReceiver(this), new IntentFilter("com.example.jose.updated.controller.CUSTOM_INTENT"));

        adapter = new PageAdapter();
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,15,true));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        createTestData();

        for (Page page : pagesToTrack) {
            try {
                UpdateRefresher.downloadHtml(page);
            } catch (Exception e) {
                e.printStackTrace();
            }
            pagesHolder.addPageHtmlToMap(page);
        }

        Intent serviceIntent = new Intent(this, NotificationService.class);
        startService(serviceIntent);
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    public void showAddPageDialog(View view) {
        addPageDialogFragment.show(fragmentManager, "addPageFragment");
    }

    public static void notifyAdapterDataSetChange(Context context) {
//        adapter.notifyDataSetChanged();
        Handler handler = new Handler(context.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        };
        handler.post(runnable);
    }

    private void createTestData() {
        Page nike = new Page("Nike", "https://www.nike.com/us/en_us/", new Date().getTime());
        Page twitter = new Page("Twitter", "https://twitter.com/AyoJoanks", new Date().getTime());
        Page inward = new Page("Inward", "https://inwardmovement.wordpress.com", new Date().getTime());
        Page adidas = new Page("Adidas", "https://www.adidas.com", new Date().getTime());
        Page espn = new Page("ESPN", "https://www.espn.com", new Date().getTime());

        //for testing
        pagesToTrack.clear();
        pagesToTrack.add(twitter);
        pagesToTrack.add(espn);
        pagesToTrack.add(adidas);
        pagesToTrack.add(nike);
        pagesToTrack.add(inward);

    }


    @Override
    public void onUpdateDetected() {
        progressBar.setVisibility(View.INVISIBLE);
        adapter.notifyDataSetChanged();
    }
}

