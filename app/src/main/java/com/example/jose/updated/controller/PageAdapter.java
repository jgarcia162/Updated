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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;

import com.davidecirillo.multichoicerecyclerview.MultiChoiceAdapter;
import com.example.jose.updated.R;
import com.example.jose.updated.model.Page;
import com.example.jose.updated.view.PageViewHolder;

import java.util.ArrayList;
import java.util.List;

public class PageAdapter extends MultiChoiceAdapter<PageViewHolder>{
    private DatabaseHelper databaseHelper;
    private List<Page> listOfPages = new ArrayList<>();
    private int lastPosition;
    private Context context;
    private ButtonListener listener;

    public PageAdapter(Context context, ButtonListener listener,DatabaseHelper databaseHelper) {
        this.context = context;
        this.listener = listener;
        this.databaseHelper = databaseHelper;
        lastPosition = -1;
    }

    @Override
    public PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_layout, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PageViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Page page = listOfPages.get(position);
        holder.bind(page);
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return listOfPages.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom);
            animation.setDuration(500);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
    

    @Override
    public void setActive(@NonNull View view, boolean state) {
        CardView card = (CardView) view.findViewById(R.id.item_layout);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.check_box);
        TypedValue elevationTypedValue = new TypedValue();
        TypedValue translationtypedValue = new TypedValue();
        if (state) {
            if (getSelectedItemCount() > 0) {
                listener.showButtons();
            }
            checkBox.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context.getResources().getValue(R.dimen.selected_card_elevation_float,elevationTypedValue,true);
                context.getResources().getValue(R.dimen.selected_card_translationz_float,translationtypedValue,true);
                float elevation = elevationTypedValue.getFloat();
                float translation = translationtypedValue.getFloat();
                card.setCardElevation(elevation);
                card.setTranslationZ(translation);
            }
        } else {
            if (getSelectedItemCount() < 1) {
                listener.hideButtons();
            }
            checkBox.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context.getResources().getValue(R.dimen.selected_card_elevation_float,elevationTypedValue,true);
                context.getResources().getValue(R.dimen.selected_card_translationz_float,translationtypedValue,true);
                float elevation = elevationTypedValue.getFloat();
                float translation = translationtypedValue.getFloat();
                card.setCardElevation(elevation);
                card.setTranslationZ(-translation);
            }
        }
    }

    @Override
    protected View.OnClickListener defaultItemViewClickListener(final PageViewHolder holder,final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageClicked(holder,position);
            }
        };
    }

    private void openInBrowser(Page page, PageViewHolder holder) {
        Uri pageUri = Uri.parse(page.getPageUrl());
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setCloseButtonIcon(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ic_arrow_back_white_24dp));
        builder.addDefaultShareMenuItem();
        builder.setStartAnimations(context, R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));
        // set toolbar color and/or setting custom actions before invoking build()
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (page.isUpdated()) {
            holder.updatedStatusTextView.setText(R.string.not_updated);
            databaseHelper.removeFromUpdatedPages(page);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            customTabsIntent.intent.putExtra(Intent.EXTRA_REFERRER,
                    Uri.parse(Intent.URI_ANDROID_APP_SCHEME + "//" + context.getPackageName()));
        }
        customTabsIntent.launchUrl(context, pageUri);
    }

    private void pageClicked(PageViewHolder holder,int position) {
        Page page = listOfPages.get(position);
        openInBrowser(page, holder);
    }

    public void setListOfPages(List<Page> listOfPages) {
        this.listOfPages = listOfPages;
    }
}
