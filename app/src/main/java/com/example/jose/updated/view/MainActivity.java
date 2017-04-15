package com.example.jose.updated.view;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
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
import com.example.jose.updated.controller.NotificationService;
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

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class MainActivity extends BaseActivity implements UpdatedCallback, SwipeRefreshLayout.OnRefreshListener, ButtonListener {
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private FragmentManager fragmentManager;
    private PageAdapter adapter;
    private RecyclerView recyclerView;
    private TextView addPagesTextView;
    private Button selectAllButton;
    private Button deleteButton;
    private Button untrackButton;
    private ProgressBar progressBar;
    private ViewGroup buttonLayout;
    private DatabaseHelper databaseHelper;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UpdateBroadcastReceiver updateBroadcastReceiver;
    private boolean buttonsHidden = true;
    private boolean selectAllClicked = false;
    private boolean firstTime;
    private boolean loginSkipped;
    private List<Page> allPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginSkipped = getIntent().getExtras().getBoolean("loginSkipped");
        firstTime = getSharedPreferences(UpdatedConstants.PREFS_NAME, 0).getBoolean(UpdatedConstants.FIRST_TIME_PREF_TAG, true);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

        assignFields();

        Log.d("FIREBASE USER", "onCreate: " + (firebaseUser == null));

        setupViews();
        loadPages();

        localBroadcastManager.registerReceiver(updateBroadcastReceiver, new IntentFilter("com.example.jose.updated.controller.CUSTOM_INTENT"));

        Intent serviceIntent = new Intent(getApplicationContext(), NotificationService.class);
        startService(serviceIntent);

        getSharedPreferences(UpdatedConstants.PREFS_NAME, 0).edit().putBoolean(UpdatedConstants.FIRST_TIME_PREF_TAG, false).apply();

        setButtonClickListeners();
    }

    private void loadPages() {
        if (loginSkipped && firstTime) {
            allPages = new ArrayList<>();
        } else if (loginSkipped) {
            allPages = databaseHelper.getAllPages();
        } else {
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.deleteAll();
            realm.commitTransaction();
            realm.close();

            if (databaseReference.child("pages").getKey() == null) {
                databaseReference.child("pages").setValue(new ArrayList<>());
            }
            allPages = new ArrayList<>();
            loadPagesFromFirebase();
        }
        setupRecyclerView();
    }

    private void assignFields() {
        updateBroadcastReceiver = new UpdateBroadcastReceiver(this);
        fragmentManager = getFragmentManager();
        databaseHelper = new DatabaseHelper();
        if (!loginSkipped) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        }
    }

    private void loadPagesFromFirebase() {
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.child("pages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    databaseHelper.addToAllPages(snapshot.getValue(Page.class));
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                        databaseHelper.removeFromPagesToTrack(pageToDelete);
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
                        databaseHelper.deactivatePage(pageToUntrack);
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
        adapter = new PageAdapter(this, this, databaseHelper);
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
        adapter.notifyItemInserted(adapter.getItemCount() - 1);
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
}

