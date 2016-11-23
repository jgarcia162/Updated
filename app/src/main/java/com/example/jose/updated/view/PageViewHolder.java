package com.example.jose.updated.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jose.updated.R;
import com.example.jose.updated.model.Page;

public class PageViewHolder extends RecyclerView.ViewHolder {

    private TextView pageTitleTextView;
    private TextView updatedStatusTextView;
    private TextView timeOfLastUpdateTextView;
    private RelativeLayout itemLayout;
    private Context context;
    private Uri pageUri;

    public PageViewHolder(View view) {
        super(view);
        updatedStatusTextView = (TextView) view.findViewById(R.id.update_status_text_view);
        pageTitleTextView = (TextView) view.findViewById(R.id.page_title_text_view);
        timeOfLastUpdateTextView = (TextView) view.findViewById(R.id.time_of_last_update_text_view);
        itemLayout = (RelativeLayout) view.findViewById(R.id.item_layout);
        context = view.getContext();
    }

    public void bind(Page page){
        pageUri = Uri.parse(page.getPageUrl());
        pageTitleTextView.setText(page.getTitle());
        if(page.isUpdated()){
            updatedStatusTextView.setText(R.string.page_updated);
        }
        timeOfLastUpdateTextView.setText(page.getFormattedTimeOfLastUpdate());
        itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO change this to implicit intent
                Intent intent = new Intent(Intent.ACTION_VIEW, pageUri);
                context.startActivity(intent);
            }
        });
    }
}
