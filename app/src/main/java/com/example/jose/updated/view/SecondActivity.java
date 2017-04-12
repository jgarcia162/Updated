package com.example.jose.updated.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.example.jose.updated.R;
import com.example.jose.updated.controller.BaseActivity;
import com.example.jose.updated.controller.DatabaseHelper;
import com.example.jose.updated.model.Page;

/**
 * Created by Joe on 2/6/17.
 */
public class SecondActivity extends BaseActivity {
    private FragmentManager fragmentManager;
    private int container;
    private DatabaseHelper databaseHelper;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setToolbar();
        databaseHelper = new DatabaseHelper();
        fragmentManager = getSupportFragmentManager();
        container = R.id.second_activity_fragment_container;
        Intent intent = getIntent();
        if(!intent.hasExtra("fragment_to_load")){
            loadPageDetailsFragment(intent);
        }else {
            loadSettingsFragment(intent);
        }
    }

    private void loadSettingsFragment(Intent intent) {
        String fragmentName = intent.getStringExtra("fragment_to_load");
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch(fragmentName){
            case "Settings":
                transaction.replace(container,new SettingsFragment(),"pref_fragment").commit();
                break;
        }
    }

    private void loadPageDetailsFragment(Intent intent) {
        Bundle bundle = intent.getBundleExtra("page_bundle");
        String pageUrl = bundle.getString("page");
        Page page = databaseHelper.getPageFromUrl(pageUrl);
        PageDetailsFragment pageDetailsFragment;
        if (page != null) {
            String pageTag = page.getTitle() + "_fragment";
            if (fragmentManager.findFragmentByTag(page.getTitle()) == null) {
                pageDetailsFragment = new PageDetailsFragment();
                pageDetailsFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(container, pageDetailsFragment, pageTag).commit();
            } else {
                pageDetailsFragment = (PageDetailsFragment) fragmentManager.findFragmentByTag(page.getTitle());
                fragmentManager.beginTransaction().replace(container, pageDetailsFragment, pageTag).commit();
            }
        }
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null && showBackHomeAsUpIndicator()) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
//            getSupportActionBar().setShowHideAnimationEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            if (toolbarTitle() != null) {
                getSupportActionBar().setTitle(toolbarTitle());
            }
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
            menu.findItem(R.id.add_page_menu).setEnabled(false);
            menu.findItem(R.id.add_page_menu).setVisible(false);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected int setActivityIdentifier() {
        return R.layout.second_activity;

    }
    @Override
    protected String toolbarTitle() {
        return getString(R.string.app_name);
    }

    @Override
    protected boolean showBackHomeAsUpIndicator() {
        return true;
    }
}
