package com.example.jose.updated.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.example.jose.updated.R;
import com.example.jose.updated.model.Page;

/**
 * Created by Joe on 2/6/17.
 */
public class SecondActivity extends MainActivity {
    private FragmentManager fragmentManager;
    private int container;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        if(!(getActionBar() == null)){
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        fragmentManager = getSupportFragmentManager();
        container = R.id.second_activity_fragment_container;
        Intent intent = getIntent();
        if(!intent.hasExtra("fragment_to_load")){
            loadPageDetailsFragment(intent);
        }else {
            loadOtherFragment(intent);
        }
    }

    private void loadOtherFragment(Intent intent) {
        String fragmentName = intent.getStringExtra("fragment_to_load");
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch(fragmentName){
            case "Settings":
                transaction.replace(container,new SettingsFragment(),"pref_fragment").commit();
                break;
            case "Contact":
                transaction.replace(container,new ContactFragment(),"contact_fragment").commit();
                break;
            case "About":
                transaction.replace(container,new AboutFragment(),"about_fragment").commit();
                break;
        }
    }

    private void loadPageDetailsFragment(Intent intent) {
        Bundle bundle = intent.getBundleExtra("page_bundle");
        Page page = bundle.getParcelable("page");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
