package com.example.jose.updated.view;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.example.jose.updated.R;
import com.example.jose.updated.controller.BaseActivity;
import com.example.jose.updated.controller.NotificationService;
import com.example.jose.updated.controller.PageAdapter;
import com.example.jose.updated.controller.UpdateBroadcastReceiver;
import com.example.jose.updated.controller.UpdateRefresher;
import com.example.jose.updated.model.Page;
import com.example.jose.updated.model.RealmDatabaseHelper;

import java.util.Date;

import static com.example.jose.updated.model.UpdatedConstants.DEFAULT_UPDATE_FREQUENCY;

public class MainActivity extends BaseActivity implements UpdateBroadcastReceiver.UpdatedCallback, SwipeRefreshLayout.OnRefreshListener{
    private FragmentManager fragmentManager;
    @SuppressLint("StaticFieldLeak")
    public static PageAdapter adapter;
    private AddPageDialogFragment addPageDialogFragment;
    private RealmDatabaseHelper realmDatabaseHelper = RealmDatabaseHelper.getInstance();
    private SharedPreferences preferences;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static UpdateBroadcastReceiver updateBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
            updateBroadcastReceiver = new UpdateBroadcastReceiver(this);
            fragmentManager = getFragmentManager();
            realmDatabaseHelper = RealmDatabaseHelper.getInstance();
            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
            swipeRefreshLayout.setOnRefreshListener(this);
            setupRecyclerView();
            addPageDialogFragment = new AddPageDialogFragment();
            localBroadcastManager.registerReceiver(updateBroadcastReceiver, new IntentFilter("com.example.jose.updated.controller.CUSTOM_INTENT"));
            createTestData();
            downloadTestData();
            realmDatabaseHelper.initializeMap();
            Intent serviceIntent = new Intent(getBaseContext(), NotificationService.class);
            startService(serviceIntent);

        }
    }

    private void downloadTestData() {
        for (Page page : realmDatabaseHelper.getPagesToTrack()) {
            try {
                UpdateRefresher.downloadHtml(page);
            } catch (Exception e) {
                e.printStackTrace();
            }
            realmDatabaseHelper.addPageHtmlToMap(page);
        }
        adapter.notifyDataSetChanged();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        adapter = new PageAdapter(getBaseContext());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 15, true));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }


    public void showAddPageDialog(View view) {
        addPageDialogFragment.show(fragmentManager, "addPageFragment");
    }

    public static void notifyAdapterDataSetChange(Context context) {
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
        Page twitter = new Page("Twitter", "https://twitter.com/AyoJoanks", new Date().getTime());
        Page inward = new Page("Inward", "https://inwardmovement.wordpress.com", new Date().getTime());
        Page adidas = new Page("Adidas", "https://www.adidas.com", new Date().getTime());

        //for testing
        twitter.setIsActive(true);
        inward.setIsActive(true);
        adidas.setIsActive(true);
        twitter.setUpdateFrequency(DEFAULT_UPDATE_FREQUENCY);
        inward.setUpdateFrequency(DEFAULT_UPDATE_FREQUENCY);
        adidas.setUpdateFrequency(DEFAULT_UPDATE_FREQUENCY);
        realmDatabaseHelper.addToPagesToTrack((twitter));
        realmDatabaseHelper.addToPagesToTrack(adidas);
        realmDatabaseHelper.addToPagesToTrack(inward);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onUpdateDetected() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        try {
            UpdateRefresher.refreshUpdate();
            swipeRefreshLayout.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteButtonPressed(View view){
        Toast.makeText(getApplicationContext(), ""+ realmDatabaseHelper.getSizeOfPagesToTrack(), Toast.LENGTH_SHORT).show();
        notifyAdapterDataSetChange(getApplicationContext());
        Toast.makeText(getApplicationContext(), ""+ realmDatabaseHelper.getSizeOfPagesToTrack(), Toast.LENGTH_SHORT).show();
    }

}

