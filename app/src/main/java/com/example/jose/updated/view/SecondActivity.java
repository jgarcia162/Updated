package com.example.jose.updated.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.jose.updated.R;
import com.example.jose.updated.controller.BaseActivity;
import com.example.jose.updated.model.Page;
import com.example.jose.updated.controller.RealmDatabaseHelper;

/**
 * Created by Joe on 2/6/17.
 */
public class SecondActivity extends BaseActivity {
    private FragmentManager fragmentManager;
    private int container;
    private RealmDatabaseHelper realmDatabaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        if(!(getActionBar() == null)){
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        realmDatabaseHelper = new RealmDatabaseHelper();
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
        Page page = realmDatabaseHelper.getPageFromUrl(pageUrl);
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
}
