package com.example.jose.updated.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.jose.updated.R;
import com.example.jose.updated.model.Page;

/**
 * Created by Joe on 2/6/17.
 */
public class SecondActivity extends AppCompatActivity {
    private Bundle bundle;
    private Page page;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        fragmentManager = getSupportFragmentManager();
        Intent intent = getIntent();
        bundle = intent.getBundleExtra("page_bundle");
        page = bundle.getParcelable("page");
        PageDetailsFragment pageDetailsFragment;
        if(page != null){
            String pageTag = page.getTitle()+"_fragment";
            if (fragmentManager.findFragmentByTag(page.getTitle()) == null){
                pageDetailsFragment = new PageDetailsFragment();
                pageDetailsFragment.setArguments(bundle);
                fragmentManager.beginTransaction().add(R.id.second_activity_fragment_container,pageDetailsFragment,pageTag).commit();
            }else{
                pageDetailsFragment = (PageDetailsFragment) fragmentManager.findFragmentByTag(page.getTitle());
                fragmentManager.beginTransaction().add(R.id.second_activity_fragment_container,pageDetailsFragment,pageTag).commit();
            }
        }
    }

}
