package com.example.jose.updated.view;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.example.jose.updated.R;

/**
 * Created by Joe on 3/5/17.
 */

public class ExceptionDialogBox {
    private Context context;

    public ExceptionDialogBox(){

    }

    public ExceptionDialogBox(Context context) {
        this.context = context;
    }

    public void buildAlertDialogWithContext(Context context){
        this.context = context;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Whoops!");
        builder.setMessage("Something's broken =[");
        builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    public void buildAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.custom_dialog);
        builder.setTitle("Whoops!");
        builder.setMessage("Something's broken =[");
        builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MainActivity.swipeRefreshLayout.setRefreshing(false);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
