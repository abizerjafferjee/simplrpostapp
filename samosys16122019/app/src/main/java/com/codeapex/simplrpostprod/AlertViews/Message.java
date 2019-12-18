package com.codeapex.simplrpostprod.AlertViews;

import android.view.View;

import com.codeapex.simplrpostprod.R;
import com.google.android.material.snackbar.Snackbar;

public class Message {

    public void showSnack(View rootView, String msg) {
        Snackbar snackbar = Snackbar.make(rootView, msg, Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(rootView.getResources().getColor(R.color.colorRed));
        snackbar.show();
    }

    public void showSnackGreen(View rootView, String msg) {
        Snackbar snackbar = Snackbar.make(rootView, msg, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(rootView.getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

}


