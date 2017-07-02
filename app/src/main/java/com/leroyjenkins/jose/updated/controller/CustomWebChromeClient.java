package com.leroyjenkins.jose.updated.controller;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;

import com.leroyjenkins.jose.updated.model.Page;

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
        imageContainer.setImageBitmap(icon);
    }
}
