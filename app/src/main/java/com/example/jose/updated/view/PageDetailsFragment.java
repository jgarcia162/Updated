package com.example.jose.updated.view;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jose.updated.R;
import com.example.jose.updated.model.Page;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageDetailsFragment extends Fragment {
    private TextInputEditText pageTitle;
    private TextView timeLastUpdatedTV;
    private Button openBrowserButton;
    private EditText notesEditText;
    private PackageManager packageManager;
    private Page page;
    private Bundle bundle;
    private static final String PREFS_NAME = "UpdatedPreferencesFile";

    public PageDetailsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(bundle == null){
            bundle = getArguments();
            page = bundle.getParcelable("page");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.page_details_layout, container, false);
        packageManager = getContext().getPackageManager();
        pageTitle = (TextInputEditText) view.findViewById(R.id.details_page_title_tv);
        timeLastUpdatedTV = (TextView) view.findViewById(R.id.details_timelastupdated_tv);
        openBrowserButton = (Button) view.findViewById(R.id.details_open_browser_button);
        notesEditText = (EditText) view.findViewById(R.id.details_notes_et);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pageTitle.setText(page.getTitle());
        timeLastUpdatedTV.setText(page.getFormattedTimeOfLastUpdate());
        openBrowserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInBrowser();
            }
        });
        notesEditText.setText(loadNotes());
    }

    private String loadNotes() {
        SharedPreferences preferences = getContext().getSharedPreferences(PREFS_NAME,0);
        return preferences.getString(page.getTitle() + " notes","No Notes");
    }

    public void openInBrowser(){
        //URL only works with full URL "http..."
        Uri pageUri = Uri.parse(page.getPageUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, pageUri);
        String title = getResources().getString(R.string.chooser_title);
        Intent chooser = Intent.createChooser(intent, title);
        if (intent.resolveActivity(packageManager) != null) {
            getContext().startActivity(chooser);
        }else{
            getContext().startActivity(intent);
        }
    }
}
