package com.example.jose.updated.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.jose.updated.R;
import com.wooplr.spotlight.prefs.PreferencesManager;
import com.wooplr.spotlight.utils.SpotlightSequence;

/**
 * Created by Joe on 6/27/17.
 * Tutorial activity using Spotlight third-party library
 */

public class SpotlightActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView addPagesTextView;
    private TextView skipTutorialTextView;
    private TextView startTutorialTextView;
    private TextView replayTutorialTextView;
    private RecyclerView recyclerView;
    private Toolbar toolbar;

    private static final String INTRO_REPEAT = "repeat_intro";
    private static final String INTRO_SEQUENCE = "sequence_intro";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotlight);
        addPagesTextView = (TextView) findViewById(R.id.add_pages_text_view);
        addPagesTextView.setVisibility(View.GONE);
        skipTutorialTextView = (TextView) findViewById(R.id.skip_tutorial_button);
        startTutorialTextView = (TextView) findViewById(R.id.start_button);
        replayTutorialTextView = (TextView) findViewById(R.id.replay_button);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        skipTutorialTextView.setOnClickListener(this);
        startTutorialTextView.setOnClickListener(this);
        replayTutorialTextView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        PreferencesManager preferencesManager = new PreferencesManager(SpotlightActivity.this);
        switch (v.getId()) {
            case R.id.skip_tutorial_button:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.start_button:
                preferencesManager.resetAll();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SpotlightSequence.getInstance(SpotlightActivity.this, null)
                                .addSpotlight(recyclerView, "Pages", "When you add a page you'll see them here", INTRO_SEQUENCE)
                                .addSpotlight(toolbar, "Toolbar", "Click the + to add a page", INTRO_REPEAT)
                                .startSequence();
                    }
                }, 400);
                Intent loginIntent = new Intent(this,LoginActivity.class);
//                startActivity(loginIntent);
                replayTutorialTextView.setVisibility(View.VISIBLE);
                startTutorialTextView.setVisibility(View.GONE);
                break;
            case R.id.replay_button:
                onClick(startTutorialTextView);
                break;
        }

    }
}
