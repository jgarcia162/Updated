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
import com.example.jose.updated.controller.NotificationService;
import com.example.jose.updated.model.Page;

import java.util.Date;
import java.util.Map;

/**
 * Created by Joe on 9/28/16.
 */
public class AddPageDialogFragment extends DialogFragment {
    Button addPageButton, previewButton;
    WebView pagePreviewWebView;
    TextInputEditText urlInputEditText, titleInputEditText;
    ImageView previewImage;
    Page newPage;
    private Map<String,String> pageHtmlMap;

    public AddPageDialogFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageHtmlMap = MainActivity.pageHtmlMap;

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
        if(newPage == null){
            if(!urlText.equals("")){
                if(!urlText.substring(0,3).equals("http")){
                    urlText = "https://" + urlText;
                }
                if(titleText.equals("")){
                    newPage = new Page("untitled", urlText, new Date().getTime());
                }
                newPage = new Page(titleText, urlText, new Date().getTime());
                NotificationService.addPageToTrack(newPage);
                MainActivity.notifyAdapterDataSetChange();
                titleInputEditText.setText("");
                urlInputEditText.setText("");
                Toast.makeText(getActivity(),newPage.getTitle()+" Added", Toast.LENGTH_SHORT).show();
                pageHtmlMap.put(newPage.getPageUrl(), MainActivity.downloadHtml(newPage));
                newPage = null;
                this.dismiss();
            }else{
                Toast.makeText(getActivity(), "Please enter a URL!", Toast.LENGTH_SHORT).show();
            }
        }else{
            try {
                NotificationService.addPageToTrack(newPage);
                MainActivity.notifyAdapterDataSetChange();
                titleInputEditText.setText("");
                urlInputEditText.setText("");
                pageHtmlMap.put(newPage.getPageUrl(), MainActivity.downloadHtml(newPage));
                Toast.makeText(getActivity(),newPage.getTitle()+" Added", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), "Please enter a URL!", Toast.LENGTH_SHORT).show();
        }
    }

    public void displayPageInPreviewWebView(String url){
        pagePreviewWebView.loadUrl(url);
    }

}
