package com.example.jose.updated.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.jose.updated.R;
import com.example.jose.updated.view.ExceptionDialogBox;
import com.example.jose.updated.view.MainActivity;
import com.example.jose.updated.view.SecondActivity;

import static com.example.jose.updated.model.UpdatedConstants.*;

/**
 * Created by Joe on 2/18/17.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    protected Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setToolbar();
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
            case R.id.refresh_menu:
                try {
                    MainActivity.swipeRefreshLayout.setRefreshing(true);
                    if(!isNetworkConnected()){
                        buildAlertDialog(this);

                    }
                    UpdateRefresher refresher = new UpdateRefresher();
                    refresher.refreshUpdate();
                    Toast.makeText(getApplicationContext(), R.string.refreshed_toast_text, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.settings_menu:
                intent.putExtra("fragment_to_load", "Settings");
                startActivity(intent);
                break;
            case R.id.logout_menu:
                //logout and unregister receiver
                LocalBroadcastManager.getInstance(this).unregisterReceiver(MainActivity.updateBroadcastReceiver);
                SharedPreferences.Editor e = getPreferences(0).edit();
                e.remove(PREF_KEY_OAUTH_TOKEN);
                e.remove(PREF_KEY_OAUTH_SECRET);
                e.remove(PREF_KEY_TWITTER_LOGIN);
                e.apply();
                Toast.makeText(this, "logged out", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public void buildAlertDialog(Context context){
        ExceptionDialogBox box = new ExceptionDialogBox(context);
        box.buildAlertDialog();
    }

    private boolean isNetworkConnected(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }


    private void setToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null && showBackHomeAsUpIndicator()) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
//            getSupportActionBar().setShowHideAnimationEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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

    //Toolbar related methods
    protected boolean showBackHomeAsUpIndicator() {
        return false;
    }

    protected String toolbarTitle() {
        return getResources().getString(R.string.app_name);
    }

    protected abstract int setActivityIdentifier();


}
