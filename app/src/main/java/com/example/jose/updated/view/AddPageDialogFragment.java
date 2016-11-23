package com.example.jose.updated.view;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jose.updated.R;
import com.example.jose.updated.model.Page;

/**
 * Created by Joe on 9/28/16.
 */
public class AddPageDialogFragment extends DialogFragment {
    Button addPageButton;
    WebView pagePreviewWebView;
    TextInputEditText urlInputEditText, titleInputEditText;
    ImageView previewImage;

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
        addPageButton = (Button) view.findViewById(R.id.add_page_button);
        pagePreviewWebView = (WebView) view.findViewById(R.id.preview_web_view);
        urlInputEditText = (TextInputEditText) view.findViewById(R.id.url_input_edit_text);
        previewImage = (ImageView) view.findViewById(R.id.web_preview_image_view);
        addPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Page newPage = new Page(urlInputEditText.getText().toString(), urlInputEditText.getText().toString());
                MainFragmentActivity.pagesToTrack.add(newPage);
                previewImage.setVisibility(View.INVISIBLE);
                displayPageInPreviewWebView(newPage.getPageUrl());
                Toast.makeText(getActivity(),"Page Added", Toast.LENGTH_SHORT).show();
                urlInputEditText.setText("");

            }
        });
        return view;
    }

    public void displayPageInPreviewWebView(String url){
        pagePreviewWebView.loadUrl(url);
    }

}
