package com.example.jose.updated.controller;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import static com.example.jose.updated.model.UpdatedConstants.PREFS_NAME;
import static com.example.jose.updated.model.UpdatedConstants.PREF_KEY_TWITTER_LOGGED_IN;
import static com.example.jose.updated.model.UpdatedConstants.TWITTER_CONSUMER_KEY;
import static com.example.jose.updated.model.UpdatedConstants.TWITTER_CONSUMER_SECRET;

/**
 * Created by Joe on 2/18/17.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    protected Toolbar toolbar;
    private boolean loggedIn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbar();
        loggedIn = getSharedPreferences(PREFS_NAME, 0).getBoolean(PREF_KEY_TWITTER_LOGGED_IN, false);

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
                    if (!isNetworkConnected()) {
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
                if (!loggedIn) {
                    ConfigurationBuilder builder = new ConfigurationBuilder();
                    builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
                    builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
                    Configuration configuration = builder.build();
                    TwitterFactory factory = new TwitterFactory(configuration);
                    Twitter twitter = factory.getInstance();
                    twitter.setOAuthAccessToken(null);
                }
                break;
        }
        return true;
    }

    public void buildAlertDialog(Context context) {
        ExceptionDialogBox box = new ExceptionDialogBox(context);
        box.buildAlertDialog();
    }

    private boolean isNetworkConnected() {
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
