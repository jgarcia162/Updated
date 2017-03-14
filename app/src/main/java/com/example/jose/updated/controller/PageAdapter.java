package com.example.jose.updated.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
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
    private String TAG = this.getClass().getSimpleName();

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
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Page page = listOfPages.get(position);
                openInBrowser(page,holder);
            }
        };
    }

    public void openInBrowser(Page page,PageViewHolder holder) {
        Uri pageUri = Uri.parse(page.getPageUrl());
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setCloseButtonIcon(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ic_arrow_back_white_24dp));
        builder.addDefaultShareMenuItem();
        builder.setStartAnimations(context,R.anim.slide_in_bottom,R.anim.slide_out_bottom);
        builder.enableUrlBarHiding();
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));
        // set toolbar color and/or setting custom actions before invoking build()
        CustomTabsIntent customTabsIntent = builder.build();
        if (page.isUpdated()) {
            holder.updatedStatusTextView.setText(R.string.not_updated);
            realmDatabaseHelper.removeFromUpdatedPages(page);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            customTabsIntent.intent.putExtra(Intent.EXTRA_REFERRER,
                    Uri.parse(Intent.URI_ANDROID_APP_SCHEME + "//" + context.getPackageName()));
        }
        customTabsIntent.launchUrl(context, pageUri);
    }
}
