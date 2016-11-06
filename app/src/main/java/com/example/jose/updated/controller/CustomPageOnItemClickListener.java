package com.example.jose.updated.controller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class CustomPageOnItemClickListener implements View.OnClickListener{
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onLongItemClick(View view, int position);
    }

    public CustomPageOnItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    @Override
    public void onClick(View view) {

    }
}
