package com.example.jose.updated.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.jose.updated.R;
import com.example.jose.updated.view.MainActivity;
import com.example.jose.updated.view.SecondActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Joe on 2/15/17.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Timer timer = new Timer();
        timer.schedule(
        new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        },3000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getBaseContext(),SecondActivity.class);
        switch(item.getItemId()){
            case R.id.settings_menu:
                intent.putExtra("fragment_to_load","Settings");
                startActivity(intent);
                break;
            case R.id.contact_menu:
                intent.putExtra("fragment_to_load","Contact");
                startActivity(intent);
                break;
            case R.id.about_menu:
                intent.putExtra("fragment_to_load","About");
                startActivity(intent);
                break;
            case R.id.logout_menu:
                //logout
        }
        return super.onOptionsItemSelected(item);
    }
}
