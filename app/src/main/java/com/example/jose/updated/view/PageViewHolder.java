package com.example.jose.updated.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jose.updated.R;
import com.example.jose.updated.model.Page;

public class PageViewHolder extends RecyclerView.ViewHolder {

    private TextView pageTitleTextView,pageUrlTextView,updatedStatusTextView,timeOfLastUpdateTextView;
    private RelativeLayout itemLayout;
    private Context context;
    private Uri pageUri;
    PackageManager packageManager;

    public PageViewHolder(View view) {
        super(view);
        updatedStatusTextView = (TextView) view.findViewById(R.id.update_status_text_view);
        pageTitleTextView = (TextView) view.findViewById(R.id.page_title_text_view);
        pageUrlTextView = (TextView) view.findViewById(R.id.page_url_text_view);
        timeOfLastUpdateTextView = (TextView) view.findViewById(R.id.time_of_last_update_text_view);
        itemLayout = (RelativeLayout) view.findViewById(R.id.item_layout);
        context = view.getContext();
        packageManager = context.getPackageManager();
    }

    public void bind(final Page page){
        pageUri = Uri.parse(page.getPageUrl());
        pageTitleTextView.setText(page.getTitle());
        pageUrlTextView.setText(page.getPageUrl());

        if(MainFragmentActivity.updatedPages.contains(page)){
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
                    MainFragmentActivity.updatedPages.remove(page);
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
}
