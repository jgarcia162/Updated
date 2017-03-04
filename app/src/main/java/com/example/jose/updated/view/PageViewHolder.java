package com.example.jose.updated.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jose.updated.R;
import com.example.jose.updated.controller.RealmDatabaseHelper;
import com.example.jose.updated.model.Page;

public class PageViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnLongClickListener,View.OnClickListener{

    private TextView pageTitleTextView;
    private TextView pageUrlTextView;
    public TextView updatedStatusTextView;
    private TextView timeOfLastUpdateTextView;
    private CardView itemLayout;
    private Context context;
    private ImageView imageView;
    private WebView webView;
    private ImageView editPageButton;
    private Page page;
    private RealmDatabaseHelper realmDatabaseHelper = new RealmDatabaseHelper();
//TODO default click listener
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
        itemLayout = (CardView) view.findViewById(R.id.item_layout);
        itemLayout.setOnLongClickListener(this);
        itemLayout.setOnClickListener(this);
        context = view.getContext();

    }

    public void bind(Page page) {
        this.page = page;
        pageTitleTextView.setText(page.getTitle());
        Bitmap bitmap = loadFavicon(page);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            page.setBitmapIcon(bitmap);
        }

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

//    @Override
//    public void onClick(View v) {
//        openInBrowser();
//    }

    private void openPageDetails() {
        Bundle bundle = new Bundle();
        bundle.putString("page", page.getPageUrl());
        Intent intent = new Intent(context, SecondActivity.class);
        intent.putExtra("page_bundle", bundle);
        context.startActivity(intent);
    }

//    @Override
//    public boolean onLongClick(View v) {
//        //TODO add long click editable
//        Toast.makeText(context,"LONG CLICK", Toast.LENGTH_SHORT).show();
//        return true;
//    }

    public void openInBrowser() {
        Uri pageUri = Uri.parse(page.getPageUrl());
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.addDefaultShareMenuItem();
        builder.setToolbarColor(Color.BLUE);
        // set toolbar color and/or setting custom actions before invoking build()
        CustomTabsIntent customTabsIntent = builder.build();
        if (page.isUpdated()) {
            updatedStatusTextView.setText(R.string.not_updated);
            realmDatabaseHelper.removeFromUpdatedPages(page);
        }
        customTabsIntent.launchUrl(context, pageUri);
    }


    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(context,"LONG CLICK", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onClick(View v) {
        openInBrowser();
    }
}