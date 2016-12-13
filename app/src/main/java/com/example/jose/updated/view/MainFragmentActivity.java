package com.example.jose.updated.view;

import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.jose.updated.R;
import com.example.jose.updated.controller.DownloadTask;
import com.example.jose.updated.controller.NotificationService;
import com.example.jose.updated.controller.PageAdapter;
import com.example.jose.updated.controller.UpdateRefresher;
import com.example.jose.updated.model.Page;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFragmentActivity extends FragmentActivity {
    private FragmentManager fragmentManager;
    private EditText urlInputEditText;
    private Button updateButton;
    private Page newPage;
    public static PageAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public static  List<Page> pagesToTrack,updatedPages;
    public static Map<String,String> pageHtmlMap;
    private Page nike,twitter,inward;
    private AddPageDialogFragment addPageDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity_main);

        fragmentManager = getFragmentManager();
        updateButton = (Button) findViewById(R.id.track_page_button);
        urlInputEditText = (EditText) findViewById(R.id.url_input_edit_text);
        pagesToTrack = new ArrayList<>();
        updatedPages = new ArrayList<>();
        //updatedPages = NotificationService.updatedPages;
        pageHtmlMap = new HashMap<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        addPageDialogFragment = new AddPageDialogFragment();


        adapter = new PageAdapter(pagesToTrack);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        nike = new Page("Nike","https://www.nike.com/us/en_us/", new Date().getTime());
        twitter = new Page("Twitter","https://twitter.com/AyoJoanks", new Date().getTime());
        inward = new Page("Inward","https://inwardmovement.wordpress.com",new Date().getTime());

        //for testing
        pagesToTrack.add(twitter);
        pagesToTrack.add(inward);
        pagesToTrack.add(nike);

        Intent serviceIntent = new Intent(this,NotificationService.class);
        serviceIntent.putExtra("pages to track", (ArrayList<? extends Parcelable>) pagesToTrack);
        startService(serviceIntent);

    }

    public void refresh(View view){
        UpdateRefresher.refreshUpdate(pagesToTrack,updatedPages);
    }

    private boolean isPageUpdated(Page page) throws Exception{
        String hmtlToCheck = downloadHtml(page);
        return hmtlToCheck.equals(pageHtmlMap.get(page.getPageUrl()));
    }

    public static String downloadHtml(Page page) throws Exception {
        DownloadTask task = new DownloadTask();
        task.execute(page.getPageUrl());
        page.setContents(task.get());
        return task.get();
    }

    public void showAddPageDialog(View view) {
        addPageDialogFragment.show(fragmentManager,"addPageFragment");
    }

    public void createNotification(String namesOfUpdatedPages) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.default_photo).setContentTitle(getString(R.string.notification_title)).setContentText(namesOfUpdatedPages);
        notificationBuilder.setAutoCancel(true);
        Intent notificationIntent = new Intent(this,MainFragmentActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);
    }
}

