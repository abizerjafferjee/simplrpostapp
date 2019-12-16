package com.codeapex.simplrpostprod.AlertViews;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.codeapex.simplrpostprod.R;

public class AlertMessage {

    Context context;

    public AlertMessage(Context context) {
        this.context = context;
    }

    public void Alert(String msg) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("SimplerPost");
        alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
        alertDialogBuilder.setMessage(msg).setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}


