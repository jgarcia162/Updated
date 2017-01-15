package com.example.jose.updated.controller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jose.updated.R;
import com.example.jose.updated.model.Page;
import com.example.jose.updated.model.PagesHolder;
import com.example.jose.updated.view.PageViewHolder;

import java.util.List;

public class PageAdapter extends RecyclerView.Adapter<PageViewHolder>{
    private List<Page> listOfPages;

    public PageAdapter(){
        PagesHolder pagesHolder = PagesHolder.getInstance();
        this.listOfPages = pagesHolder.getPagesToTrack();
    }

    @Override
    public PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_list_item_layout,parent,false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PageViewHolder holder, int position) {
        Page page = listOfPages.get(position);
        holder.bind(page);
    }

    @Override
    public int getItemCount() {
        return listOfPages.size();
    }
}
