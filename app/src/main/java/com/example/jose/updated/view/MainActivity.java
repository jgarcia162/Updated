package com.example.jose.updated.view;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jose.updated.R;
import com.example.jose.updated.controller.NotificationService;
import com.example.jose.updated.controller.PageAdapter;
import com.example.jose.updated.controller.UpdateRefresher;
import com.example.jose.updated.model.Page;
import com.example.jose.updated.model.PagesHolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {
    private FragmentManager fragmentManager;
    private EditText urlInputEditText;
    private Button updateButton;
    private Page newPage;
    public static PageAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public static  List<Page> pagesToTrack;
    private AddPageDialogFragment addPageDialogFragment;
    private PagesHolder pagesHolder = PagesHolder.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity_main);

        fragmentManager = getFragmentManager();
        updateButton = (Button) findViewById(R.id.track_page_button);
        urlInputEditText = (EditText) findViewById(R.id.url_input_edit_text);
        pagesToTrack = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        addPageDialogFragment = new AddPageDialogFragment();
        pagesHolder = PagesHolder.getInstance();


        adapter = new PageAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        Page nike = new Page("Nike","https://www.nike.com/us/en_us/", new Date().getTime());
        Page twitter = new Page("Twitter","https://twitter.com/AyoJoanks", new Date().getTime());
        Page inward = new Page("Inward","https://inwardmovement.wordpress.com",new Date().getTime());
        Page adidas = new Page("Adidas","https://www.adidas.com",new Date().getTime());
        Page espn = new Page("ESPN","https://www.espn.com",new Date().getTime());

        //for testing
        pagesToTrack.add(twitter);
        pagesToTrack.add(inward);
        pagesToTrack.add(nike);
        pagesToTrack.add(adidas);
        pagesToTrack.add(espn);


    }

    @Override
    protected void onResume() {
        super.onResume();
        for(Page page: pagesToTrack){
            try {
                UpdateRefresher.downloadHtml(page);
                pagesHolder.addToPagesToTrack(page);
                pagesHolder.addPageHtmlToMap(page);
                Log.d("MAP SIZE", pagesHolder.getPageHtmlMap().size()+"");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Intent serviceIntent = new Intent(this,NotificationService.class);
        startService(serviceIntent);
    }

    public void refresh(View view) throws Exception {
        Toast.makeText(this, "WTF", Toast.LENGTH_SHORT).show();
        UpdateRefresher.refreshUpdate();
    }

    public void showAddPageDialog(View view) {
        addPageDialogFragment.show(fragmentManager,"addPageFragment");
    }

    public static void notifyAdapterDataSetChange(){
        adapter.notifyDataSetChanged();
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return super.registerReceiver(receiver, filter);
    }
}

