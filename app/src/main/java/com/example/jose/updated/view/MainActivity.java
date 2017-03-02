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

import com.example.jose.updated.R;
import com.example.jose.updated.controller.BaseActivity;
import com.example.jose.updated.controller.NotificationService;
import com.example.jose.updated.controller.PageAdapter;
import com.example.jose.updated.controller.UpdateBroadcastReceiver;
import com.example.jose.updated.controller.UpdateRefresher;
import com.example.jose.updated.model.RealmDatabaseHelper;

import io.realm.Realm;

public class MainActivity extends BaseActivity implements UpdateBroadcastReceiver.UpdatedCallback, SwipeRefreshLayout.OnRefreshListener {
    private FragmentManager fragmentManager;
    @SuppressLint("StaticFieldLeak")
    public static PageAdapter adapter;
    private AddPageDialogFragment addPageDialogFragment;
    private RealmDatabaseHelper realmDatabaseHelper;
    private SharedPreferences preferences;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static UpdateBroadcastReceiver updateBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(getApplicationContext());
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        updateBroadcastReceiver = new UpdateBroadcastReceiver(this);
        fragmentManager = getFragmentManager();
        realmDatabaseHelper = new RealmDatabaseHelper();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        setupRecyclerView();
        addPageDialogFragment = new AddPageDialogFragment();
        localBroadcastManager.registerReceiver(updateBroadcastReceiver, new IntentFilter("com.example.jose.updated.controller.CUSTOM_INTENT"));
        Intent serviceIntent = new Intent(getApplicationContext(), NotificationService.class);
        startService(serviceIntent);

    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        adapter = new PageAdapter(getApplicationContext());
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

}

