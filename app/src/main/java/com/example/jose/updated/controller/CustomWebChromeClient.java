package com.example.jose.updated.controller;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;

import com.example.jose.updated.model.Page;

/**
 * Created by Joe on 2/18/17.
 */

public class CustomWebChromeClient extends WebChromeClient {
    private ImageView imageContainer;
    private Page page;
    public CustomWebChromeClient(View view,Page page) {
        imageContainer = (ImageView)view;
        this.page = page;
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        super.onReceivedIcon(view,icon);
        page.setBitmapIcon(icon);
        imageContainer.setImageBitmap(icon);
    }
}
