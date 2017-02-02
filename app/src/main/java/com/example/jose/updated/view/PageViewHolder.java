package com.example.jose.updated.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
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

    private TextView pageTitleTextView,pageUrlTextView,updatedStatusTextView,timeOfLastUpdateTextView;
    private CardView itemLayout;
    private Context context;
    private Uri pageUri;
    private ImageView imageView;
    private WebView webView;
    PackageManager packageManager;

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
        packageManager = context.getPackageManager();

    }

    public void bind(final Page page){
        pageUri = Uri.parse(page.getPageUrl());
        pageTitleTextView.setText(page.getTitle());
        pageUrlTextView.setText(page.getPageUrl());
        if(page.getBitmapIcon() == null){
            page.setBitmapIcon(loadFavicon(page));
        }
        imageView.setImageBitmap(page.getBitmapIcon());


        if(PagesHolder.getInstance().getUpdatedPages().contains(page)){
            updatedStatusTextView.setText(R.string.page_updated);
            page.setUpdated(true);
        }else{
            updatedStatusTextView.setText(R.string.not_updated);
            page.setUpdated(false);
        }

        timeOfLastUpdateTextView.setText(page.getFormattedTimeOfLastUpdate());
        itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(page.isUpdated()){
                    updatedStatusTextView.setText(R.string.not_updated);
                    page.setUpdated(false);
                    PagesHolder.getInstance().removeFromUpdatedPages(page);
                }
                //URL only works with full URL "http..."
                Intent intent = new Intent(Intent.ACTION_VIEW, pageUri);
                String title = v.getResources().getString(R.string.chooser_title);
                Intent chooser = Intent.createChooser(intent, title);
                if (intent.resolveActivity(packageManager) != null) {
                    context.startActivity(chooser);
                }else{
                    context.startActivity(intent);
                }
            }
        });


    }

    public Bitmap loadFavicon(Page page){
        webView.loadData(page.getContents(),"text/html",null);
        webView.setActivated(false);
        return webView.getFavicon();
    }
}
