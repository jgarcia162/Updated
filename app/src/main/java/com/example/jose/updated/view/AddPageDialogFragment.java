package com.example.jose.updated.view;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.example.jose.updated.R;
import com.example.jose.updated.controller.PageAdapter;
import com.example.jose.updated.controller.RealmDatabaseHelper;
import com.example.jose.updated.controller.UpdatedCallback;
import com.example.jose.updated.model.Page;

import java.util.Date;

public class AddPageDialogFragment extends DialogFragment {
    private Button addPageButton, previewButton;
    private WebView pagePreviewWebView;
    private TextInputEditText urlInputEditText, titleInputEditText;
    private Page newPage;
    private RealmDatabaseHelper realmDatabaseHelper;
    private String urlText;
    private String titleText;
    private PageAdapter adapter;
    private boolean previewButtonClicked;
    private UpdatedCallback callback;

    public AddPageDialogFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realmDatabaseHelper = new RealmDatabaseHelper();
        previewButtonClicked = false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_page_dialog_fragment_layout, container, false);
        initializeViews(view);
        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urlText = urlInputEditText.getText().toString();
                titleText = titleInputEditText.getText().toString();
                onClickPreviewButton(urlText, titleText);
            }
        });

        addPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urlText = urlInputEditText.getText().toString();
                titleText = titleInputEditText.getText().toString();
                try {
                    onClickAddButton(urlText, titleText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    private void initializeViews(View view) {
        addPageButton = (Button) view.findViewById(R.id.track_page_button);
        pagePreviewWebView = (WebView) view.findViewById(R.id.preview_web_view);
        pagePreviewWebView.setWebViewClient(new WebViewClient());
        titleInputEditText = (TextInputEditText) view.findViewById(R.id.title_input_edit_text);
        urlInputEditText = (TextInputEditText) view.findViewById(R.id.url_input_edit_text);
        previewButton = (Button) view.findViewById(R.id.preview_button);
    }

    private void onClickAddButton(String urlText, String titleText) throws Exception {
        if (!previewButtonClicked) {
            if (!TextUtils.isEmpty(urlText)) {
                if (!URLUtil.isValidUrl(urlText)) {
                    Toast.makeText(getActivity(), R.string.invalid_url_string, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(titleText)) {
                    titleText = getString(R.string.untitled_page_text);
                }
                realmDatabaseHelper.createPage(titleText, urlText, new Date().getTime());
                newPage = null;
                resetTextFields();
                this.dismiss();
            } else {
                Toast.makeText(getActivity(), R.string.enter_url_toast, Toast.LENGTH_SHORT).show();
            }
        } else { //this means user has previewed page
            if (!URLUtil.isValidUrl(newPage.getPageUrl())) {
                Toast.makeText(getActivity(), R.string.invalid_url_string, Toast.LENGTH_SHORT).show();
                return;
            }
            realmDatabaseHelper.createPage(newPage.getTitle(), newPage.getPageUrl(), newPage.getTimeOfLastUpdateInMilliSec());
            newPage = null;
            resetTextFields();
            this.dismiss();
        }
        callback.onItemInserted();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        resetTextFields();
        super.onDismiss(dialog);
    }

    private void resetTextFields() {
        titleInputEditText.setText(null);
        urlInputEditText.setText(R.string.url_input_et_default_text);
    }

    private void onClickPreviewButton(String urlText, String titleText) {
        previewButtonClicked = true;
        if (!TextUtils.isEmpty(urlText)) {
            if (!URLUtil.isValidUrl(urlText)) {
                Toast.makeText(getActivity(), R.string.invalid_url_string, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!TextUtils.isEmpty(titleText)) {
                newPage = new Page(titleText, urlText, new Date().getTime());
                displayPageInPreviewWebView(newPage.getPageUrl());
            } else {
                newPage = new Page(getString(R.string.untitled_page_text), urlText, new Date().getTime());
                displayPageInPreviewWebView(newPage.getPageUrl());
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.enter_url_toast), Toast.LENGTH_SHORT).show();
        }

    }

    public void displayPageInPreviewWebView(String url) {
        pagePreviewWebView.setVisibility(View.VISIBLE);
        pagePreviewWebView.loadUrl(url);
    }

    public void setCallback(UpdatedCallback callback) {
        this.callback = callback;
    }
}
