package com.example.jose.updated.controller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jose.updated.R;
import com.example.jose.updated.model.Page;

import java.util.List;

/**
 * Created by Joe on 9/17/16.
 */
public class PageAdapter extends RecyclerView.Adapter<PageAdapter.ViewHolder> {



    private List<Page> listOfPages;

    public PageAdapter(List<Page> listOfPages){
        this.listOfPages = listOfPages;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView pageTitleTextView;
        TextView updatedStatusTextView;
        TextView timeOfLastUpdateTextView;

        public ViewHolder(View view) {
            super(view);
            pageTitleTextView = (TextView) view.findViewById(R.id.page_title_text_view);
            updatedStatusTextView = (TextView) view.findViewById(R.id.update_status_text_view);
            timeOfLastUpdateTextView = (TextView) view.findViewById(R.id.time_of_last_update_text_view);
        }
    }

    @Override
    public PageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_list_item_layout,parent,false);
        return new PageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Page page = listOfPages.get(position);
        holder.pageTitleTextView.setText(page.getTitle());
        holder.timeOfLastUpdateTextView.setText(page.getFormattedTimeOfLastUpdate());
    }

    @Override
    public int getItemCount() {
        return listOfPages.size();
    }
}
