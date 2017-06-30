package com.example.jose.updated.view;

import android.app.FragmentManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.davidecirillo.multichoicerecyclerview.MultiChoiceToolbar;
import com.example.jose.updated.R;
import com.example.jose.updated.controller.BaseActivity;
import com.example.jose.updated.controller.ButtonListener;
import com.example.jose.updated.controller.DatabaseHelper;
import com.example.jose.updated.controller.NotificationJobService;
import com.example.jose.updated.controller.PageAdapter;
import com.example.jose.updated.controller.UpdateBroadcastReceiver;
import com.example.jose.updated.controller.UpdateRefresher;
import com.example.jose.updated.controller.UpdatedCallback;
import com.example.jose.updated.model.Page;
import com.example.jose.updated.model.UpdatedConstants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wooplr.spotlight.prefs.PreferencesManager;
import com.wooplr.spotlight.utils.SpotlightSequence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

import static com.example.jose.updated.model.UpdatedConstants.DEFAULT_UPDATE_FREQUENCY;
import static com.example.jose.updated.model.UpdatedConstants.UPDATE_FREQUENCY_PREF_TAG;

public class MainActivity extends BaseActivity implements UpdatedCallback, SwipeRefreshLayout.OnRefreshListener, ButtonListener, View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private FragmentManager fragmentManager;
    private PageAdapter adapter;
    private DatabaseHelper databaseHelper;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UpdateBroadcastReceiver updateBroadcastReceiver;
    private SharedPreferences preferences;
    private RecyclerView recyclerView;
    private TextView addPagesTextView;
    private Button selectAllButton;
    private Button deleteButton;
    private Button untrackButton;
    private ProgressBar progressBar;
    private ViewGroup buttonLayout;
    private boolean buttonsHidden = true;
    private boolean selectAllClicked = false;
    private boolean firstTime;
    private boolean loginSkipped;
    private List<Page> allPages;
    private Button fakeButton;

    private static final String INTRO_REPEAT = "repeat_intro";
    private static final String INTRO_SEQUENCE = "sequence_intro";
    private Button startButton;
    private Button skipButton;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(UpdatedConstants.PREFS_NAME, 0);
        loginSkipped = getIntent().getExtras().getBoolean("loginSkipped");
        firstTime = preferences.getBoolean(UpdatedConstants.FIRST_TIME_PREF_TAG, true);
        fakeButton = (Button) findViewById(R.id.fake_button);
        startButton = (Button) findViewById(R.id.start_button);
        skipButton = (Button) findViewById(R.id.skip_tutorial_button);
        startButton.setOnClickListener(this);
        skipButton.setOnClickListener(this);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

        initializeFields();

        setupViews();

        if (firstTime) {
            databaseHelper.createPage("Fake Page","http://www.google.com", new Date().getTime());
            startButton.setVisibility(View.VISIBLE);
            skipButton.setVisibility(View.VISIBLE);
            preferences.edit().putBoolean(UpdatedConstants.FIRST_TIME_PREF_TAG, false).apply();
        }else{
            fakeButton.setVisibility(View.GONE);
            startButton.setVisibility(View.GONE);
            skipButton.setVisibility(View.GONE);
        }

        loadPages();

        localBroadcastManager.registerReceiver(updateBroadcastReceiver, new IntentFilter("com.example.jose.updated.controller.CUSTOM_INTENT"));

        startJobService();
        setButtonClickListeners();
    }

    private void showTutorial() {
        PreferencesManager preferencesManager = new PreferencesManager(MainActivity.this);
        preferencesManager.resetAll();
        fakeButton.setVisibility(View.VISIBLE);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                SpotlightSequence.getInstance(MainActivity.this, null)
                        .addSpotlight(fakeButton, "New Pages", "Click the + to add a page", INTRO_SEQUENCE)
                        .addSpotlight(recyclerView.getChildAt(0),"Pages","Click a page to view it, click the gear to edit",INTRO_REPEAT)
                        .startSequence();
            }
        }, 400);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startJobService() {
        ComponentName serviceName = new ComponentName(getApplicationContext(), NotificationJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(1, serviceName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPeriodic(preferences.getLong(UPDATE_FREQUENCY_PREF_TAG, DEFAULT_UPDATE_FREQUENCY))
                .build();
        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.schedule(jobInfo);
    }

    private void loadPages() {
        if (loginSkipped && firstTime) {
            allPages = new ArrayList<>();
        } else if (loginSkipped) {
            allPages = databaseHelper.getAllPages();
            setupRecyclerView();
        } else {
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.deleteAll();
                    realm.commitTransaction();
                    realm.close();

                    if (databaseReference.child("pages") == null) {
                        databaseReference.child("pages").setValue(new ArrayList<>());
                    }

                    allPages = new ArrayList<>();
                    setupRecyclerView();
                    loadPagesFromFirebase();
                }
            };
            handler.post(runnable);
        }
    }

    private void initializeFields() {
        updateBroadcastReceiver = new UpdateBroadcastReceiver(this);
        fragmentManager = getFragmentManager();
        databaseHelper = new DatabaseHelper();

        if (!loginSkipped) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            attachAuthStateListener();
        }
    }

    private void attachAuthStateListener() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {

                } else {
                    detachChildListener();
                }
            }
        };
    }

    private void loadPagesFromFirebase() {
        progressBar.setVisibility(View.VISIBLE);
        attachChildEventListener();
        progressBar.setVisibility(View.GONE);
//        databaseReference.child("pages").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//
//                    databaseHelper.addToPagesToTrack(snapshot.getValue(Page.class));
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("DATABASE ERROR", "onCancelled: " + databaseError.getDetails());
//                Log.d("DATABASE ERROR", "onCancelled: " + databaseError.getMessage());
//            }
//        });

    }

    private void attachChildEventListener() {
        databaseHelper.emptyDatabase();
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Page pageToAdd;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    pageToAdd = snapshot.getValue(Page.class);
                    if (pageToAdd.getUser().equals(firebaseUser.getEmail())) {
                        databaseHelper.addToAllPagesFromFirebase(pageToAdd);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(valueEventListener);
    }

    private void detachChildListener() {
        if (valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
            valueEventListener = null;
        }
    }

    private void setButtonClickListeners() {
        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectAllClicked) {
                    adapter.selectAll();
                    selectAllClicked = true;
                    selectAllButton.setText(R.string.deselect_button_text);
                } else {
                    adapter.deselectAll();
                    selectAllClicked = false;
                    selectAllButton.setText(R.string.select_all_button_text);
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.getSelectedItemCount() > 0) {
                    List<Page> allPages = databaseHelper.getAllPages();
                    List<Page> pagesToDelete = new ArrayList<>();
                    for (Integer i : adapter.getSelectedItemList()) {
                        pagesToDelete.add(allPages.get(i));
                    }
                    for (Page pageToDelete : pagesToDelete) {
                        databaseHelper.deletePage(pageToDelete);
                    }
                    adapter.notifyDataSetChanged();
                    toolbar.postInvalidate();
                }
                resetButtonLayout();
            }
        });
        untrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.getSelectedItemCount() > 0) {
                    List<Page> allPages = databaseHelper.getAllPages();
                    List<Page> pagesToUntrack = new ArrayList<>();
                    for (Integer i : adapter.getSelectedItemList()) {
                        pagesToUntrack.add(allPages.get(i));
                    }
                    for (Page pageToUntrack : pagesToUntrack) {
                        databaseHelper.removeFromPagesToTrack(pageToUntrack);
                    }
                    adapter.notifyDataSetChanged();
                }
                resetButtonLayout();
            }
        });
    }

    private void resetButtonLayout() {
        adapter.deselectAll();
        selectAllButton.setText(R.string.select_all_button_text);
        buttonLayout.setVisibility(View.GONE);
    }

    private void setupViews() {
        addPagesTextView = (TextView) findViewById(R.id.add_pages_text_view);
        if (!firstTime) {
            addPagesTextView.setVisibility(View.GONE);
        }

        buttonLayout = (ViewGroup) findViewById(R.id.buttons_layout);
        selectAllButton = (Button) findViewById(R.id.select_all_button);
        deleteButton = (Button) findViewById(R.id.delete_all_button);
        untrackButton = (Button) findViewById(R.id.untrack_all_button);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent, null));
        } else {
            swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        }

    }

    private synchronized void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        adapter = new PageAdapter(this, this);
        adapter.setSingleClickMode(false);
        adapter.setMultiChoiceToolbar(createMultiChoiceToolbar());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 15, true));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onBackPressed() {
        if (adapter.getSelectedItemCount() > 0) {
            adapter.deselectAll();
        }
    }

    private MultiChoiceToolbar createMultiChoiceToolbar() {
        return new MultiChoiceToolbar.Builder(this, toolbar)
                .setTitles(toolbarTitle(), getString(R.string.selected_toolbar_title))
                .build();
    }

    public void showAddPageDialog() {
        AddPageDialogFragment addPageDialogFragment = new AddPageDialogFragment();
        addPageDialogFragment.show(fragmentManager, "addPageFragment");
        addPageDialogFragment.setCallback(this);
    }

    //needed to refresh adapter after deleting a page from its detail fragment
    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        if (firebaseAuth != null) {
            firebaseAuth.addAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (valueEventListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        detachChildListener();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Realm realm = Realm.getDefaultInstance();
        realm.close();
    }

    @Override
    public void onUpdateDetected() {
        onResume();
    }


    @Override
    public void onItemInserted() {
        if (addPagesTextView.getVisibility() != View.GONE) {
            addPagesTextView.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        if (!isNetworkConnected()) {
            super.showNetworkConnectionDialog();
            resetSwipeRefreshLayout();
        } else {
            try {
                UpdateRefresher updateRefresher = new UpdateRefresher();
                updateRefresher.refreshUpdate();
                resetSwipeRefreshLayout();
                Toast.makeText(getApplicationContext(), "Refreshed", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void resetSwipeRefreshLayout() {
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected int setActivityIdentifier() {
        return R.layout.activity_main;
    }

    @Override
    protected String toolbarTitle() {
        return getString(R.string.app_name);
    }

    @Override
    protected boolean showBackHomeAsUpIndicator() {
        return false;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void hideButtons() {
        if (!buttonsHidden) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
            animation.setDuration(500);
            buttonLayout.startAnimation(animation);
            buttonLayout.setVisibility(View.GONE);
            buttonsHidden = true;
        }
    }

    @Override
    public void showButtons() {
        if (buttonsHidden) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
            animation.setDuration(500);
            buttonLayout.startAnimation(animation);
            buttonLayout.setVisibility(View.VISIBLE);
            buttonsHidden = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_page_menu:
                showAddPageDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.start_button:
                showTutorial();
                break;
            case R.id.skip_tutorial_button:
                startButton.setVisibility(View.GONE);
                skipButton.setVisibility(View.GONE);
                databaseHelper.emptyDatabase();
                adapter.notifyDataSetChanged();
                break;
        }
    }
}

