package com.example.jose.updated.view;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jose.updated.R;
import com.example.jose.updated.controller.CustomPageOnItemClickListener;
import com.example.jose.updated.controller.PageAdapter;
import com.example.jose.updated.model.Page;

import java.util.ArrayList;
import java.util.List;

public class PageListFragment extends Fragment {
    PageAdapter adapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    static  List<Page> pagesToTrack;
    FragmentManager fragmentManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getFragmentManager();
        pagesToTrack = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PageAdapter(pagesToTrack);
        recyclerView.setAdapter(adapter);
        recyclerView.hasFixedSize();

        recyclerView.setOnClickListener(new CustomPageOnItemClickListener(getActivity().getApplicationContext(), recyclerView, new CustomPageOnItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //TODO pass info from item to webview fragment
            }



            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_list_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        return view;
    }

}
