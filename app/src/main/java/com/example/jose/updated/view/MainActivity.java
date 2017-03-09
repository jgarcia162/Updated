package com.example.jose.updated.view;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.davidecirillo.multichoicerecyclerview.MultiChoiceToolbar;
import com.example.jose.updated.R;
import com.example.jose.updated.controller.BaseActivity;
import com.example.jose.updated.controller.NotificationService;
import com.example.jose.updated.controller.PageAdapter;
import com.example.jose.updated.controller.RealmDatabaseHelper;
import com.example.jose.updated.controller.UpdateBroadcastReceiver;
import com.example.jose.updated.controller.UpdateRefresher;

import io.realm.Realm;

public class MainActivity extends BaseActivity implements UpdateBroadcastReceiver.UpdatedCallback, SwipeRefreshLayout.OnRefreshListener {
    private FragmentManager fragmentManager;
    @SuppressLint("StaticFieldLeak")
    public static PageAdapter adapter;
    private AddPageDialogFragment addPageDialogFragment;
    private RealmDatabaseHelper realmDatabaseHelper;
    private SharedPreferences preferences;
    public static SwipeRefreshLayout swipeRefreshLayout;
    public static UpdateBroadcastReceiver updateBroadcastReceiver;
    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
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
        adapter.setSingleClickMode(false);
        adapter.setMultiChoiceToolbar(createMultiChoiceToolbar());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 15, true));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }


    private MultiChoiceToolbar createMultiChoiceToolbar() {
        return new MultiChoiceToolbar.Builder(MainActivity.this, toolbar)
                .setTitles(toolbarTitle(), getString(R.string.selected_toolbar_title))
                .setMultiChoiceColours(R.color.colorPrimary, R.color.colorPrimaryDark)
                .build();
    }


    public void showAddPageDialog(View view) {
        addPageDialogFragment.show(fragmentManager, "addPageFragment");
    }

//    public static void notifyAdapterDataSetChange(Context context) {
//        Handler handler = new Handler(context.getMainLooper());
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                adapter.notifyDataSetChanged();
//            }
//        };
//        handler.post(runnable);
//    }


    @Override
    protected void onResume() {
        super.onResume();
//        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Realm realm = Realm.getDefaultInstance();
        realm.close();
    }

    @Override
    public void onUpdateDetected() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        if(!isNetworkConnected()){
            super.buildAlertDialog(this);
            resetSwipeRefreshLayout();
        }else{
            try {
                UpdateRefresher updateRefresher = new UpdateRefresher();
                updateRefresher.refreshUpdate();
                resetSwipeRefreshLayout();
                Toast.makeText(getApplicationContext(), "Refreshed", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void resetSwipeRefreshLayout() {
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected int setActivityIdentifier() {
        return R.layout.activity_main;
    }

    @Override
    protected String toolbarTitle() {
        return getString(R.string.app_name);
    }

    @Override
    protected boolean showBackHomeAsUpIndicator() {
        return false;
    }

    private boolean isNetworkConnected(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

}

