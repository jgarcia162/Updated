package com.leroyjenkins.jose.updated.view;


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
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.leroyjenkins.jose.updated.R;
import com.leroyjenkins.jose.updated.controller.DatabaseHelper;
import com.leroyjenkins.jose.updated.model.Page;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageDetailsFragment extends Fragment {
    private TextInputEditText pageTitle;
    private TextView timeLastUpdatedTV;
    private TextView urlTextView;
    private EditText notesEditText;
    private Button saveSettingsButton;
    private Button deleteButton;
    private Switch trackingSwitch;
    private ProgressBar progressBar;
    private Page page;
    private Bundle bundle;
    private DatabaseHelper databaseHelper;

    public PageDetailsFragment() {
        databaseHelper = new DatabaseHelper();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (bundle == null) {
            bundle = getArguments();
            String pageUrl = bundle.getString("page");
            page = databaseHelper.getPageFromUrl(pageUrl);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_details_layout, container, false);
        findViews(view);
        return view;
    }

    private void findViews(View view) {
        pageTitle = (TextInputEditText) view.findViewById(R.id.details_page_title_tv);
        timeLastUpdatedTV = (TextView) view.findViewById(R.id.details_timelastupdated_tv);
        urlTextView = (TextView) view.findViewById(R.id.details_url_tv);
        trackingSwitch = (Switch) view.findViewById(R.id.page_active_switch);
        saveSettingsButton = (Button) view.findViewById(R.id.details_save_settings_button);
        deleteButton = (Button) view.findViewById(R.id.delete_page_button);
        notesEditText = (EditText) view.findViewById(R.id.details_notes_et);
        progressBar = (ProgressBar) view.findViewById(R.id.details_page_progressbar);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Resources resources = getResources();

        setTextFields(resources);
        trackingSwitch.setChecked(page.isActive());

        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
                Toast.makeText(getActivity(), R.string.details_saved_text, Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePage();
            }
        });
    }

    private void setTextFields(Resources resources) {
        pageTitle.setText(page.getTitle());
        timeLastUpdatedTV.setText(String.format(resources.getString(R.string.details_last_updated), page.getFormattedTimeOfLastUpdate()));
        urlTextView.setText(String.format(resources.getString(R.string.details_url_tv_text), page.getPageUrl()));
        notesEditText.setText(page.getNotes());
    }

    private void saveSettings() {
        String title = String.valueOf(pageTitle.getText());
        boolean isActive = trackingSwitch.isChecked();
        String notes = String.valueOf(notesEditText.getText());
        progressBar.setVisibility(View.VISIBLE);
        databaseHelper.savePageSettings(page, title, notes, isActive);
        progressBar.setVisibility(View.GONE);
        getActivity().onBackPressed();
    }

    public void deletePage() {
        databaseHelper.deletePage(page);
        getActivity().onBackPressed();
    }

}
