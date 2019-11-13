package com.codeapex.simplrpostprod.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;

public class AddAddressOptionActivity extends AppCompatActivity {

    CardView crdPublicLocation, crdPrivateLocation;
    ImageView imgBack;
    ProgressBar Loader;
    ConstraintLayout clytRoot;

    @Override
    protected void onResume() {
        super.onResume();
        UtilityClass.hideLoading(Loader,AddAddressOptionActivity.this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address_options);
        crdPublicLocation = findViewById(R.id.crdPublicLocation);
        crdPrivateLocation = findViewById(R.id.crdPrivateLocation);
        imgBack = findViewById(R.id.back_press);
        Loader = findViewById(R.id.Loader);
        clytRoot = findViewById(R.id.root);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        crdPublicLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UtilityClass.isNetworkConnected(AddAddressOptionActivity.this)) {
                    UtilityClass.showLoading(Loader,AddAddressOptionActivity.this);
                    Intent intent = new Intent(getApplicationContext(), AddPublicAddress.class);
                    startActivityForResult(intent, 1098);
                }
                else {
                    new Message().showSnack(clytRoot, Constants.noInternetMessage);
                }
            }
        });

        crdPrivateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Code to open the app private address screen
                if (UtilityClass.isNetworkConnected(AddAddressOptionActivity.this)) {
                    UtilityClass.showLoading(Loader,AddAddressOptionActivity.this);
                    Intent intent = new Intent(getApplicationContext(), AddPrivateAddress.class);
                    startActivityForResult(intent, 1098);
                }
                else {
                    new Message().showSnack(clytRoot, Constants.noInternetMessage);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1098 && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK,intent);
            finish();
        }
    }
}
