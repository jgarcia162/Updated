package com.example.jose.updated.controller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.jose.updated.R;
import com.example.jose.updated.model.Page;
import com.example.jose.updated.model.RealmDatabaseHelper;
import com.example.jose.updated.view.PageViewHolder;

import java.util.List;

public class PageAdapter extends RecyclerView.Adapter<PageViewHolder>{
    private List<Page> listOfPages;
    private int lastPosition;
    private Context context;

    public PageAdapter(Context context){
        RealmDatabaseHelper realmDatabaseHelper = new RealmDatabaseHelper();
        listOfPages = realmDatabaseHelper.getAllPages();
        this.context = context;
        lastPosition = -1;

    }

    @Override
    public PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_layout,parent,false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PageViewHolder holder, int position) {
        Page page = listOfPages.get(position);
        holder.bind(page);
        setAnimation(holder.itemView,position);
    }

    @Override
    public int getItemCount() {
        return listOfPages.size();
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom);
            animation.setDuration(1000);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
