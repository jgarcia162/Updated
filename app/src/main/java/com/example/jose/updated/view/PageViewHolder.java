package com.example.jose.updated.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jose.updated.R;
import com.example.jose.updated.controller.RealmDatabaseHelper;
import com.example.jose.updated.model.Page;

public class PageViewHolder extends RecyclerView.ViewHolder{

    private TextView pageTitleTextView;
    public TextView updatedStatusTextView;
    private TextView timeOfLastUpdateTextView;
    private Context context;
    private Page page;
    private RealmDatabaseHelper realmDatabaseHelper;
    private CardView card;
    public PageViewHolder(View view) {
        super(view);
        card = (CardView) view;
        updatedStatusTextView = (TextView) view.findViewById(R.id.update_status_text_view);
        pageTitleTextView = (TextView) view.findViewById(R.id.page_title_text_view);
        timeOfLastUpdateTextView = (TextView) view.findViewById(R.id.time_of_last_update_text_view);
        ImageView editPageButton = (ImageView) view.findViewById(R.id.edit_page_button);
        editPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPageDetails(getAdapterPosition());
            }
        });
        context = view.getContext();
        realmDatabaseHelper = new RealmDatabaseHelper();
    }

    public void bind(Page page) {
        this.page = page;
        pageTitleTextView.setText(page.getTitle());

        if (realmDatabaseHelper.getUpdatedPages().contains(page)) {
            updatedStatusTextView.setText(R.string.page_updated);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                updatedStatusTextView.setTextColor(context.getResources().getColor(R.color.colorAccent,null));
            }else{
                updatedStatusTextView.setTextColor(context.getResources().getColor(R.color.colorAccent));
            }
        } else {
            updatedStatusTextView.setTextColor(Color.BLACK);
            updatedStatusTextView.setText(R.string.not_updated);
        }
        timeOfLastUpdateTextView.setText(page.getFormattedTimeOfLastUpdate());
    }

    private void openPageDetails(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("page", page.getPageUrl());
        bundle.putInt("page_position",position);
        Intent intent = new Intent(context, SecondActivity.class);
        intent.putExtra("page_bundle", bundle);
        context.startActivity(intent);
    }





}