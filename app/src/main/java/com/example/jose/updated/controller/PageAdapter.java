package com.example.jose.updated.controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jose.updated.R;
import com.example.jose.updated.model.Page;

import java.util.List;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.ViewHolder>{



    private List<Page> listOfPages;
    private Context context;

    public PageAdapter(final Context c, final List<Page> listOfPages){
        this.listOfPages = listOfPages;
        this.context = c;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
           TextView pageTitleTextView;
           TextView updatedStatusTextView;
           TextView timeOfLastUpdateTextView;
           RelativeLayout itemLayout;

        public ViewHolder(View view) {
            super(view);
            updatedStatusTextView = (TextView) view.findViewById(R.id.update_status_text_view);
            pageTitleTextView = (TextView) view.findViewById(R.id.page_title_text_view);
            timeOfLastUpdateTextView = (TextView) view.findViewById(R.id.time_of_last_update_text_view);
            itemLayout = (RelativeLayout) view.findViewById(R.id.item_layout);
        }
    }

    @Override
    public PageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_list_item_layout,parent,false);
        return new PageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Page page = listOfPages.get(position);
        holder.pageTitleTextView.setText(page.getTitle());
        holder.timeOfLastUpdateTextView.setText(page.getFormattedTimeOfLastUpdate());
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(page.getPageUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOfPages.size();
    }
}
