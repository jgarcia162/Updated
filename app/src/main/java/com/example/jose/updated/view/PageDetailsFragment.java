package com.example.jose.updated.view;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import static com.example.jose.updated.model.UpdatedConstants.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageDetailsFragment extends Fragment {
    //TODO add toggle to activate/deactivate refreshing of page
    private TextInputEditText pageTitle;
    private TextView timeLastUpdatedTV;
    private TextView urlTV;
    private Button openBrowserButton;
    private Button saveNotesButton;
    private EditText notesEditText;
    private PackageManager packageManager;
    private Page page;
    private Bundle bundle;
    private SharedPreferences preferences;
    public PageDetailsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(bundle == null){
            bundle = getArguments();
            page = bundle.getParcelable("page");
        }
        preferences = getContext().getSharedPreferences(PREFS_NAME,0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.page_details_layout, container, false);
        packageManager = getContext().getPackageManager();
        pageTitle = (TextInputEditText) view.findViewById(R.id.details_page_title_tv);
        timeLastUpdatedTV = (TextView) view.findViewById(R.id.details_timelastupdated_tv);
        urlTV = (TextView) view.findViewById(R.id.details_url_tv);
        openBrowserButton = (Button) view.findViewById(R.id.details_open_browser_button);
        notesEditText = (EditText) view.findViewById(R.id.details_notes_et);
        saveNotesButton = (Button) view.findViewById(R.id.details_save_notes_button);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Resources resources = getResources();
        pageTitle.setText(page.getTitle());
        timeLastUpdatedTV.setText(String.format(resources.getString(R.string.details_last_updated),page.getFormattedTimeOfLastUpdate()));
        //TODO add button to edit URL
        urlTV.setText(String.format(resources.getString(R.string.details_url_tv_text),page.getPageUrl()));
        openBrowserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInBrowser();
            }
        });
        notesEditText.setText(loadNotes());
        saveNotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().putString(page.getTitle()+"_notes", String.valueOf(notesEditText.getText())).apply();
            }
        });
    }

    private String loadNotes() {
        return preferences.getString(page.getTitle() + "_notes",DEFAULT_NOTES);
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
