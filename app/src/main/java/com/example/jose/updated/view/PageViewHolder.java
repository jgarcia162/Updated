package com.example.jose.updated.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jose.updated.R;
import com.example.jose.updated.model.Page;
import com.example.jose.updated.model.PagesHolder;

public class PageViewHolder extends RecyclerView.ViewHolder {

    private TextView pageTitleTextView, pageUrlTextView, updatedStatusTextView, timeOfLastUpdateTextView;
    private CardView itemLayout;
    private Context context;
    private ImageView imageView;
    private WebView webView;

    public PageViewHolder(View view) {
        super(view);
        updatedStatusTextView = (TextView) view.findViewById(R.id.update_status_text_view);
        pageTitleTextView = (TextView) view.findViewById(R.id.page_title_text_view);
        pageUrlTextView = (TextView) view.findViewById(R.id.page_url_text_view);
        timeOfLastUpdateTextView = (TextView) view.findViewById(R.id.time_of_last_update_text_view);
        webView = (WebView) view.findViewById(R.id.card_view_webview);
        imageView = (ImageView) view.findViewById(R.id.page_icon);
        itemLayout = (CardView) view.findViewById(R.id.item_layout);
        context = view.getContext();

    }

    public void bind(final Page page) {
        pageTitleTextView.setText(page.getTitle());
        pageUrlTextView.setText(page.getPageUrl());
        if (page.getBitmapIcon() == null) {
            page.setBitmapIcon(loadFavicon(page));
        }
        imageView.setImageBitmap(page.getBitmapIcon());


        if (PagesHolder.getInstance().getUpdatedPages().contains(page)) {
            updatedStatusTextView.setText(R.string.page_updated);
            page.setUpdated(true);
        } else {
            updatedStatusTextView.setText(R.string.not_updated);
            page.setUpdated(false);
        }

        timeOfLastUpdateTextView.setText(page.getFormattedTimeOfLastUpdate());
        itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page.isUpdated()) {
                    updatedStatusTextView.setText(R.string.not_updated);
                    page.setUpdated(false);
                    PagesHolder.getInstance().removeFromUpdatedPages(page);
                }
                Bundle bundle = new Bundle();
                bundle.putParcelable("page", page);
                Intent intent = new Intent(context, SecondActivity.class);
                intent.putExtra("page_bundle", bundle);
                context.startActivity(intent);
            }
        });


    }

    private Bitmap loadFavicon(Page page) {
        webView.loadData(page.getContents(), "text/html", null);
        webView.setActivated(false);
        return webView.getFavicon();
    }


}
