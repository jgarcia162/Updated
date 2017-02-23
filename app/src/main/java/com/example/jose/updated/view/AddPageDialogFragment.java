package com.example.jose.updated.view;

import android.app.DialogFragment;
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
import com.example.jose.updated.controller.UpdateRefresher;
import com.example.jose.updated.model.Page;
import com.example.jose.updated.model.PagesHolder;

import java.util.Date;

public class AddPageDialogFragment extends DialogFragment {
    //TODO add persistence for storage when clicking add button
    private Button addPageButton, previewButton;
    private WebView pagePreviewWebView;
    private TextInputEditText urlInputEditText, titleInputEditText;
    private Page newPage;
    private PagesHolder pagesHolder;
    private String urlText;
    private String titleText;

    public AddPageDialogFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_page_dialog_fragment_layout, container, false);
        initializeViews(view);
        pagesHolder = PagesHolder.getInstance();

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
        PagesHolder pagesHolder = PagesHolder.getInstance();
        if (newPage == null) {
            if (!TextUtils.isEmpty(urlText)) {
                if (!URLUtil.isValidUrl(urlText)) {
                    Toast.makeText(getActivity(), R.string.invalid_url_string, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(titleText)) {
                    titleText = "untitled";
                }
                newPage = new Page(titleText, urlText, new Date().getTime());
                titleInputEditText.setText("");
                urlInputEditText.setText("");
                newPage.setContents(UpdateRefresher.downloadHtml(newPage));
                pagesHolder.addPageHtmlToMap(newPage);
                newPage.setTimeOfLastUpdateInMilliSec(new Date().getTime());
                newPage.setIsActive(true);
                pagesHolder.addToPagesToTrack(newPage);
                Toast.makeText(getActivity(), newPage.getTitle() + getString(R.string.added_page_string), Toast.LENGTH_SHORT).show();
                //add to db
                newPage = null;
                resetTextFields();
                this.dismiss();
            } else {
                Toast.makeText(getActivity(), R.string.enter_url_toast, Toast.LENGTH_SHORT).show();
            }
        }
        if (newPage != null) { //this means user has previewed page
            if(!URLUtil.isValidUrl(newPage.getPageUrl())){
                Toast.makeText(getActivity(), R.string.invalid_url_string, Toast.LENGTH_SHORT).show();
                return;
            }
            newPage.setContents(UpdateRefresher.downloadHtml(newPage));
            pagesHolder.addPageHtmlToMap(newPage);
            newPage.setTimeOfLastUpdateInMilliSec(new Date().getTime());
            newPage.setIsActive(true);
            pagesHolder.addToPagesToTrack(newPage);
            //add to db
            Toast.makeText(getActivity(), newPage.getTitle() + getString(R.string.added_url_toast), Toast.LENGTH_SHORT).show();
            newPage = null;
            resetTextFields();
            this.dismiss();
        }
        MainActivity.notifyAdapterDataSetChange(getActivity());
    }

    private void resetTextFields() {
        titleInputEditText.setText(null);
        urlInputEditText.setText(null);
    }

    private void onClickPreviewButton(String urlText, String titleText) {
        if (!TextUtils.isEmpty(urlText)) {
            if (!URLUtil.isValidUrl(urlText)) {
                Toast.makeText(getActivity(), R.string.invalid_url_string, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!TextUtils.isEmpty(titleText)) {
                newPage = new Page(titleText, urlText, new Date().getTime());
                displayPageInPreviewWebView(newPage.getPageUrl());
            } else {
                newPage = new Page(getString(R.string.untitled_page_string), urlText, new Date().getTime());
                displayPageInPreviewWebView(newPage.getPageUrl());
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.enter_url_toast), Toast.LENGTH_SHORT).show();
        }
    }

    public void displayPageInPreviewWebView(String url) {
        pagePreviewWebView.loadUrl(url);
    }

}
