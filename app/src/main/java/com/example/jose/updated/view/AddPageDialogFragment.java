package com.example.jose.updated.view;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
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
    private ImageView previewImage;
    private Page newPage;
    private PagesHolder pagesHolder = PagesHolder.getInstance();

    public AddPageDialogFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_page_dialog_fragment_layout,container,false);
        initializeViews(view);

        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPreviewButton();
            }
        });

        addPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlText = urlInputEditText.getText().toString();
                String titleText = titleInputEditText.getText().toString();
                try {
                    onClickAddButton(urlText,titleText);
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
        previewImage = (ImageView) view.findViewById(R.id.web_preview_image_view);
        previewButton = (Button) view.findViewById(R.id.preview_button);
    }

    private void onClickAddButton(String urlText, String titleText) throws Exception {
        PagesHolder pagesHolder = PagesHolder.getInstance();
        if(newPage == null){
            if(!urlText.equals("")){
                if(!urlText.substring(0,3).equals("http")){
                    urlText = "https://" + urlText;
                }
                if(titleText.equals("")){
                    newPage = new Page("untitled", urlText, new Date().getTime());
                }
                newPage = new Page(titleText, urlText, new Date().getTime());
                titleInputEditText.setText("");
                urlInputEditText.setText("");
                Toast.makeText(getActivity(),newPage.getTitle()+" Added", Toast.LENGTH_SHORT).show();
                newPage.setContents(UpdateRefresher.downloadHtml(newPage));
                pagesHolder.addPageHtmlToMap(newPage);
                newPage.setTimeOfLastUpdateInMilliSec(new Date().getTime());
                newPage.setIsActive(true);
                pagesHolder.addToPagesToTrack(newPage);
                //add to db
                MainActivity.notifyAdapterDataSetChange(getActivity());
                newPage = null;
                this.dismiss();
            }else{
                Toast.makeText(getActivity(), R.string.enter_url_toast, Toast.LENGTH_SHORT).show();
            }
        }else{
            try {
                titleInputEditText.setText("");
                urlInputEditText.setText("");
                newPage.setContents(UpdateRefresher.downloadHtml(newPage));
                pagesHolder.addPageHtmlToMap(newPage);
                newPage.setTimeOfLastUpdateInMilliSec(new Date().getTime());
                newPage.setIsActive(true);
                pagesHolder.addToPagesToTrack(newPage);
                //add to db
                MainActivity.notifyAdapterDataSetChange(getActivity());
                Toast.makeText(getActivity(),newPage.getTitle()+getString(R.string.added_url_toast), Toast.LENGTH_SHORT).show();
                newPage = null;
                this.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void onClickPreviewButton() {
        if(!urlInputEditText.getText().toString().equals("")){
            if(!titleInputEditText.getText().toString().equals("")){
                newPage = new Page(titleInputEditText.getText().toString(), urlInputEditText.getText().toString(), new Date().getTime());
                displayPageInPreviewWebView(newPage.getPageUrl());
                previewImage.setVisibility(View.INVISIBLE);
            }else{
                newPage = new Page("untitled", urlInputEditText.getText().toString(), new Date().getTime());
                displayPageInPreviewWebView(newPage.getPageUrl());
                previewImage.setVisibility(View.INVISIBLE);
            }
        }else{
            Toast.makeText(getActivity(), getString(R.string.enter_url_toast), Toast.LENGTH_SHORT).show();
        }
    }

    public void displayPageInPreviewWebView(String url){
        pagePreviewWebView.loadUrl(url);
    }

}
