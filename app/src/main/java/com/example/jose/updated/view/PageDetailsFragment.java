package com.example.jose.updated.view;


import android.content.Intent;
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
    private PackageManager packageManager;
    private Page page;

    public PageDetailsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle page = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_details_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        packageManager = getContext().getPackageManager();
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
