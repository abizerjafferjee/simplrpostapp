package com.codeapex.simplrpostprod.AlertViews;

import android.content.Context;
import android.view.View;

import com.codeapex.simplrpostprod.R;
import com.google.android.material.snackbar.Snackbar;

public class Message {


        Context context;

        public void showSnack(View rootView, String msg) {
            Snackbar snackbar = Snackbar.make(rootView, msg, Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(rootView.getResources().getColor(R.color.colorPrimaryDark));
            snackbar.show();
        }

    }


