package com.example.jose.updated.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jose.updated.R;
import com.example.jose.updated.model.Page;
import com.example.jose.updated.model.RealmDatabaseHelper;

public class PageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

    private TextView pageTitleTextView, pageUrlTextView, updatedStatusTextView, timeOfLastUpdateTextView;
    private CardView itemLayout;
    private Context context;
    private ImageView imageView;
    private WebView webView;
    private ImageView editPageButton;
    private Page page;



    public PageViewHolder(View view) {
        super(view);
        updatedStatusTextView = (TextView) view.findViewById(R.id.update_status_text_view);
        pageTitleTextView = (TextView) view.findViewById(R.id.page_title_text_view);
        timeOfLastUpdateTextView = (TextView) view.findViewById(R.id.time_of_last_update_text_view);
        webView = (WebView) view.findViewById(R.id.card_view_webview);
        imageView = (ImageView) view.findViewById(R.id.page_icon);
        editPageButton = (ImageView) view.findViewById(R.id.edit_page_button);
        editPageButton.setOnClickListener(this);
        itemLayout = (CardView) view.findViewById(R.id.item_layout);
        itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInBrowser();
            }
        });
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

        if (RealmDatabaseHelper.getInstance().getUpdatedPages().contains(page)) {
            updatedStatusTextView.setText(R.string.page_updated);
            page.setUpdated(true);
        } else {
            updatedStatusTextView.setText(R.string.not_updated);
            page.setUpdated(false);
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

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("page", page);
        Intent intent = new Intent(context, SecondActivity.class);
        intent.putExtra("page_bundle", bundle);
        context.startActivity(intent);
    }

    @Override
    public boolean onLongClick(View v) {
        //TODO add long click editable
        RealmDatabaseHelper.getInstance().removeFromPagesToTrack(page);
        return true;
    }

    public void openInBrowser() {
        Uri pageUri = Uri.parse(page.getPageUrl());
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.addDefaultShareMenuItem();
        // set toolbar color and/or setting custom actions before invoking build()
        CustomTabsIntent customTabsIntent = builder.build();
        if (page.isUpdated()) {
            updatedStatusTextView.setText(R.string.not_updated);
            page.setUpdated(false);
            RealmDatabaseHelper.getInstance().removeFromUpdatedPages(page);
        }
        customTabsIntent.launchUrl(context,pageUri);
    }
}