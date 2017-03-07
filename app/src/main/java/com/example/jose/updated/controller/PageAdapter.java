package com.example.jose.updated.controller;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.davidecirillo.multichoicerecyclerview.MultiChoiceAdapter;
import com.example.jose.updated.R;
import com.example.jose.updated.model.Page;
import com.example.jose.updated.view.PageViewHolder;

import java.util.List;

public class PageAdapter extends MultiChoiceAdapter<PageViewHolder> {
    private List<Page> listOfPages;
    private int lastPosition;
    private Context context;
    private PageViewHolder holderForOnClick;
    private int holderPosition;
    private RealmDatabaseHelper realmDatabaseHelper = new RealmDatabaseHelper();

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

        super.onBindViewHolder(holder,position);
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

    @Override
    public void setActive(@NonNull View view, boolean state) {

        CardView card = (CardView) view.findViewById(R.id.item_layout);
        if (state) {
            card.setAlpha(0.25f);
        } else {
            card.setAlpha(1f);
        }
    }

    @Override
    protected View.OnClickListener defaultItemViewClickListener(final PageViewHolder holder, final int position) {
        Log.d("PAGEADAPTER", "defaultItemViewClickListener: "+ position);
        //TODO this thing
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInBrowser(listOfPages.get(position),holder);

            }
        };
    }

    public void openInBrowser(Page page,PageViewHolder holder) {
        Uri pageUri = Uri.parse(page.getPageUrl());
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.addDefaultShareMenuItem();
        builder.setToolbarColor(Color.BLUE);
        // set toolbar color and/or setting custom actions before invoking build()
        CustomTabsIntent customTabsIntent = builder.build();
        if (page.isUpdated()) {
            holder.updatedStatusTextView.setText(R.string.not_updated);
            realmDatabaseHelper.removeFromUpdatedPages(page);
        }
        customTabsIntent.launchUrl(context, pageUri);
    }


}
