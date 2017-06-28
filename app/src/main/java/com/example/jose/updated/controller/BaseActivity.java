package com.example.jose.updated.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.jose.updated.R;
import com.example.jose.updated.model.UpdatedConstants;
import com.example.jose.updated.view.LoginActivity;
import com.example.jose.updated.view.SecondActivity;
import com.example.jose.updated.view.SpotlightActivity;
import com.google.firebase.auth.FirebaseAuth;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.example.jose.updated.model.UpdatedConstants.FIRST_TIME_PREF_TAG;

/**
 * Created by Joe on 2/18/17.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(getBaseContext());
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();

//        RealmMigration migration = getRealmMigrationWithSchema();
//        RealmConfiguration config = new RealmConfiguration.Builder().schemaVersion(0).migration(migration).build();
//        Realm.deleteRealm(config);
        Realm.setDefaultConfiguration(config);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/ghms.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        SharedPreferences preferences = getSharedPreferences(UpdatedConstants.PREFS_NAME, 0);

        setContentView(R.layout.activity_base);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbar();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        if(preferences.getBoolean(FIRST_TIME_PREF_TAG,true)){
            Intent spotlightIntent = new Intent(this,SpotlightActivity.class);
            startActivity(spotlightIntent);
        }

    }

    @NonNull
    private RealmMigration getRealmMigrationWithSchema() {
        return new RealmMigration() {
                @Override
                public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                    RealmSchema schema = realm.getSchema();
                    if(oldVersion == 0){
                        schema.get("Page").addField("user",String.class);
                        oldVersion++;
                    }
                }
            };
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
                    swipeRefreshLayout.setRefreshing(true);
                    if (!isNetworkConnected()) {
                        showNetworkConnectionDialog();
                    }
                    UpdateRefresher refresher = new UpdateRefresher();
                    refresher.refreshUpdate();
                    Toast.makeText(getApplicationContext(), R.string.refreshed_toast_text, Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.settings_menu:
                intent.putExtra("fragment_to_load", "Settings");
                startActivity(intent);
                break;
            case R.id.logout_menu:
                //log out of firebase and return to login screen
                showLogoutDialog();
        }
        return true;
    }

    public void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogStyle);
        builder.setTitle(R.string.logout_dialog_title);
        builder.setMessage(R.string.logout_dialog_message);
        builder.setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!isNetworkConnected()) {
                    showNetworkConnectionDialog();
                } else {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        FirebaseAuth.getInstance().signOut();
                        new DatabaseHelper().emptyDatabase();                    }
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(getBaseContext(), R.string.logged_out, Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
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
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            getSupportActionBar().setElevation(4f);
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

    public void showNetworkConnectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext(), R.style.CustomDialogStyle);
        builder.setMessage(R.string.network_connection_error);
        builder.setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
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