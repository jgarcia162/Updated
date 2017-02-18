package com.example.jose.updated.view;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.jose.updated.R;
import com.example.jose.updated.controller.NotificationService;
import com.example.jose.updated.controller.PageAdapter;
import com.example.jose.updated.controller.UpdateBroadcastReceiver;
import com.example.jose.updated.controller.UpdateRefresher;
import com.example.jose.updated.model.Page;
import com.example.jose.updated.model.PagesHolder;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UpdateBroadcastReceiver.UpdatedCallback {
    private FragmentManager fragmentManager;
    public static PageAdapter adapter;
    public static List<Page> pagesToTrack;
    private AddPageDialogFragment addPageDialogFragment;
    private PagesHolder pagesHolder = PagesHolder.getInstance();
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null){
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
            fragmentManager = getFragmentManager();
            pagesHolder = PagesHolder.getInstance();
            pagesToTrack = pagesHolder.getPagesToTrack();
            setupRecyclerView();
            addPageDialogFragment = new AddPageDialogFragment();
            localBroadcastManager.registerReceiver(new UpdateBroadcastReceiver(this), new IntentFilter("com.example.jose.updated.controller.CUSTOM_INTENT"));
            createTestData();
            downloadTestData();
            Intent serviceIntent = new Intent(getBaseContext(), NotificationService.class);
            startService(serviceIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void downloadTestData() {
        for (Page page : pagesToTrack) {
            try {
                UpdateRefresher.downloadHtml(page);
            } catch (Exception e) {
                e.printStackTrace();
            }
            pagesHolder.addPageHtmlToMap(page);
        }
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        adapter = new PageAdapter();
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
        Page nike = new Page("Nike", "https://www.nike.com/us/en_us/", new Date().getTime());
        Page espn = new Page("ESPN", "https://www.espn.com", new Date().getTime());

        //for testing
        pagesToTrack.clear();
        twitter.setIsActive(true);
        inward.setIsActive(true);
        adidas.setIsActive(true);
        nike.setIsActive(true);
        espn.setIsActive(true);
        pagesToTrack.add(twitter);
        pagesToTrack.add(nike);
        pagesToTrack.add(espn);
        pagesToTrack.add(adidas);
        pagesToTrack.add(inward);
    }

    @Override
    public void onUpdateDetected() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getBaseContext(), SecondActivity.class);
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.settings_menu:
                intent.putExtra("fragment_to_load", "Settings");
                startActivity(intent);
                break;
            case R.id.contact_menu:
                intent.putExtra("fragment_to_load", "Contact");
                startActivity(intent);
                break;
            case R.id.about_menu:
                intent.putExtra("fragment_to_load", "About");
                startActivity(intent);
                break;
            case R.id.logout_menu:
                //logout
        }
        return true;
    }

//    @Override
//    protected void onDestroy() {
//        preferences.edit().putBoolean("main_activity_created",false).apply();
//        super.onStop();
//    }
}

