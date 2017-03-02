package com.example.jose.updated.view;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.example.jose.updated.R;
import com.example.jose.updated.model.Page;
import com.example.jose.updated.model.RealmDatabaseHelper;

import static com.example.jose.updated.model.UpdatedConstants.DEFAULT_NOTES;
import static com.example.jose.updated.model.UpdatedConstants.IS_ACTIVE_TAG;
import static com.example.jose.updated.model.UpdatedConstants.NOTES_TAG;
import static com.example.jose.updated.model.UpdatedConstants.PREFS_NAME;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageDetailsFragment extends Fragment {
    private TextInputEditText pageTitle;
    private TextView timeLastUpdatedTV;
    private EditText urlEditText;
    private EditText notesEditText;
    private Button saveSettingsButton;
    private Button deleteButton;
    private Switch trackingSwitch;
    private PackageManager packageManager;
    private Page page;
    private Bundle bundle;
    private SharedPreferences preferences;
    private RealmDatabaseHelper realmDatabaseHelper;

    public PageDetailsFragment() {
        realmDatabaseHelper = new RealmDatabaseHelper();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (bundle == null) {
            bundle = getArguments();
            String pageUrl = bundle.getString("page");
            page = realmDatabaseHelper.getPageFromUrl(pageUrl);
        }
        preferences = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_details_layout, container, false);
        findViews(view);
        return view;
    }

    private void findViews(View view) {
        packageManager = getContext().getPackageManager();
        pageTitle = (TextInputEditText) view.findViewById(R.id.details_page_title_tv);
        timeLastUpdatedTV = (TextView) view.findViewById(R.id.details_timelastupdated_tv);
        urlEditText = (EditText) view.findViewById(R.id.details_url_tv);
        trackingSwitch = (Switch) view.findViewById(R.id.page_active_switch);
        saveSettingsButton = (Button) view.findViewById(R.id.details_settings_button);
        deleteButton = (Button) view.findViewById(R.id.delete_page_button);
        notesEditText = (EditText) view.findViewById(R.id.details_notes_et);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Resources resources = getResources();

        setTextFields(resources);

        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePage();
            }
        });

        boolean pageactive = preferences.getBoolean(page.getTitle() + IS_ACTIVE_TAG, true);

        setUpTrackingSwitch(pageactive);
    }

    private void setUpTrackingSwitch(boolean pageactive) {
        trackingSwitch.setChecked(pageactive);
        trackingSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page.setIsActive(!page.isActive());
                trackingSwitch.setChecked(page.isActive());
                preferences.edit().putBoolean(page.getTitle() + IS_ACTIVE_TAG, page.isActive()).apply();
                if (page.isActive()) {
                    realmDatabaseHelper.addToPagesToTrack(page);
                } else {
                    realmDatabaseHelper.removeFromPagesToTrack(page);
                }
                //update this page in db
            }
        });
    }

    private void setTextFields(Resources resources) {
        pageTitle.setText(page.getTitle());
        timeLastUpdatedTV.setText(String.format(resources.getString(R.string.details_last_updated), page.getFormattedTimeOfLastUpdate()));
        urlEditText.setText(String.format(resources.getString(R.string.details_url_tv_text), page.getPageUrl()));
        notesEditText.setText(loadNotes());
    }

    private String loadNotes() {
        return preferences.getString(page.getTitle() + NOTES_TAG, DEFAULT_NOTES);
    }
    
    private void saveSettings(){
        preferences.edit().putString(page.getTitle() + NOTES_TAG, String.valueOf(notesEditText.getText())).apply();
        if(!page.getPageUrl().equals(String.valueOf(urlEditText.getText()))){
            page.setPageUrl(String.valueOf(urlEditText.getText()));
        }
    }

    public void deletePage(){
        if(page.isUpdated()){
            realmDatabaseHelper.removeFromUpdatedPages(page);
        }

        realmDatabaseHelper.removeFromPagesToTrack(page);
        MainActivity.adapter.notifyDataSetChanged();
        getActivity().onBackPressed();

    }
    
}
