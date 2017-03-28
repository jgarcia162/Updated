package com.example.jose.updated.view;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.davidecirillo.multichoicerecyclerview.MultiChoiceToolbar;
import com.example.jose.updated.R;
import com.example.jose.updated.controller.BaseActivity;
import com.example.jose.updated.controller.ButtonListener;
import com.example.jose.updated.controller.NotificationService;
import com.example.jose.updated.controller.PageAdapter;
import com.example.jose.updated.controller.RealmDatabaseHelper;
import com.example.jose.updated.controller.UpdateBroadcastReceiver;
import com.example.jose.updated.controller.UpdateRefresher;
import com.example.jose.updated.controller.UpdatedCallback;
import com.example.jose.updated.model.Page;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class MainActivity extends BaseActivity implements UpdatedCallback, SwipeRefreshLayout.OnRefreshListener, ButtonListener {
    private FragmentManager fragmentManager;
    private PageAdapter adapter;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager layoutManager;
    private Button selectAllButton;
    private Button deleteButton;
    private Button untrackButton;
    private ProgressBar progressBar;
    private ViewGroup buttonLayout;
    private AddPageDialogFragment addPageDialogFragment;
    private RealmDatabaseHelper realmDatabaseHelper;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static UpdateBroadcastReceiver updateBroadcastReceiver;
    private boolean buttonsHidden = true;
    private boolean selectAllClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(getApplicationContext());
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        updateBroadcastReceiver = new UpdateBroadcastReceiver(this);
        fragmentManager = getFragmentManager();
        realmDatabaseHelper = new RealmDatabaseHelper();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        setupViews();
        setupRecyclerView();
        setButtonClickListeners();
        addPageDialogFragment = new AddPageDialogFragment();
        localBroadcastManager.registerReceiver(updateBroadcastReceiver, new IntentFilter("com.example.jose.updated.controller.CUSTOM_INTENT"));
        Intent serviceIntent = new Intent(getApplicationContext(), NotificationService.class);
        startService(serviceIntent);
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
                    List<Page> allPages = realmDatabaseHelper.getAllPages();
                    List<Page> pagesToDelete = new ArrayList<>();
                    for (Integer i : adapter.getSelectedItemList()) {
                        pagesToDelete.add(allPages.get(i));
                    }
                    for (Page pageToDelete : pagesToDelete) {
                        realmDatabaseHelper.removeFromPagesToTrack(pageToDelete);
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
                    List<Page> allPages = realmDatabaseHelper.getAllPages();
                    List<Page> pagesToUntrack = new ArrayList<>();
                    for (Integer i : adapter.getSelectedItemList()) {
                        pagesToUntrack.add(allPages.get(i));
                    }
                    for (Page pageToUntrack : pagesToUntrack) {
                        realmDatabaseHelper.deactivatePage(pageToUntrack);
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
        buttonLayout = (ViewGroup) findViewById(R.id.buttons_layout);
        selectAllButton = (Button) findViewById(R.id.select_all_button);
        deleteButton = (Button) findViewById(R.id.delete_all_button);
        untrackButton = (Button) findViewById(R.id.untrack_all_button);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    private void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        adapter = new PageAdapter(this, this);
        adapter.setSingleClickMode(false);
        adapter.setMultiChoiceToolbar(createMultiChoiceToolbar());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 15, true));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        adapter.notifyDataSetChanged();
    }

    private MultiChoiceToolbar createMultiChoiceToolbar() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            toolbar.setBackground(getDrawable(R.drawable.main_gradient_background));
//        }
        return new MultiChoiceToolbar.Builder(this, toolbar)
                .setTitles(toolbarTitle(), getString(R.string.selected_toolbar_title))
                .build();
    }

    public void showAddPageDialog(View view) {
        addPageDialogFragment.show(fragmentManager, "addPageFragment");
        addPageDialogFragment.setCallback(this);
        view.setVisibility(View.GONE);
    }

    //needed to refresh adapter after deleting a page from its detail fragment
    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Realm realm = Realm.getDefaultInstance();
        realm.close();
    }

    @Override
    public void onUpdateDetected() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showFloatingActionButton() {
        findViewById(R.id.floating_action_button).setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemInserted() {
        adapter.notifyItemInserted(adapter.getItemCount() - 1);
    }

    @Override
    public void onRefresh() {
        if (!isNetworkConnected()) {
            super.buildAlertDialog();
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
            buttonLayout.setVisibility(View.GONE);
            buttonsHidden = true;
        }
    }

    @Override
    public void showButtons() {
        if (buttonsHidden) {
            buttonLayout.setVisibility(View.VISIBLE);
            buttonsHidden = false;
        }
    }

    @Override
    public void resetToolbar() {
        adapter.setMultiChoiceToolbar(createMultiChoiceToolbar());
    }
}

