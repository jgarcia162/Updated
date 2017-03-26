package com.example.jose.updated.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jose.updated.R;
import com.example.jose.updated.controller.RealmDatabaseHelper;
import com.example.jose.updated.model.Page;

public class PageViewHolder extends RecyclerView.ViewHolder{

    private TextView pageTitleTextView;
    private TextView pageUrlTextView;
    public TextView updatedStatusTextView;
    private TextView timeOfLastUpdateTextView;
    private RelativeLayout itemLayout;
    private Context context;
    private ImageView imageView;
    private WebView webView;
    private ImageView editPageButton;
    private Page page;
    private RealmDatabaseHelper realmDatabaseHelper;
    public PageViewHolder(View view) {
        super(view);
        updatedStatusTextView = (TextView) view.findViewById(R.id.update_status_text_view);
        pageTitleTextView = (TextView) view.findViewById(R.id.page_title_text_view);
        timeOfLastUpdateTextView = (TextView) view.findViewById(R.id.time_of_last_update_text_view);
        webView = (WebView) view.findViewById(R.id.card_view_webview);
        imageView = (ImageView) view.findViewById(R.id.page_icon);
        editPageButton = (ImageView) view.findViewById(R.id.edit_page_button);
        editPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPageDetails();
            }
        });
        itemLayout = (RelativeLayout) view.findViewById(R.id.card_view_layout);
        context = view.getContext();
        realmDatabaseHelper = new RealmDatabaseHelper();
    }

    public void bind(Page page) {
        this.page = page;
        pageTitleTextView.setText(page.getTitle());
//        Bitmap bitmap = loadFavicon(page);
//        if (bitmap != null) {
//            imageView.setImageBitmap(bitmap);
//            page.setBitmapIcon(bitmap);
//        }

        if (realmDatabaseHelper.getUpdatedPages().contains(page)) {
            updatedStatusTextView.setText(R.string.page_updated);
        } else {
            updatedStatusTextView.setText(R.string.not_updated);
        }

        timeOfLastUpdateTextView.setText(page.getFormattedTimeOfLastUpdate());
    }

    @SuppressLint("SetJavaScriptEnabled")
    private Bitmap loadFavicon(Page page) {
        Log.d("PAGE LOADING", page.getTitle());
        if (page.getBitmapIcon() != null) {
            return page.getBitmapIcon();
        }
        webView.clearCache(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadData(page.getContents(), "text/html", null);
        webView.setActivated(false);
        return webView.getFavicon();
    }

    private Bitmap createBitmap(int id) {
        return BitmapFactory.decodeResource(context.getResources(), id);
    }

    private void openPageDetails() {
        Bundle bundle = new Bundle();
        bundle.putString("page", page.getPageUrl());
        Intent intent = new Intent(context, SecondActivity.class);
        intent.putExtra("page_bundle", bundle);
        context.startActivity(intent);
    }





}