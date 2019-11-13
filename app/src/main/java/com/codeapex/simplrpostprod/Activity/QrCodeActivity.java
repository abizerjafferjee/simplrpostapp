package com.codeapex.simplrpostprod.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.codeapex.simplrpostprod.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class QrCodeActivity extends AppCompatActivity {
    ImageView imageView,forgotimage;
    TextView shortnameqr,location,pluscodeqr;
    String address,shortName,qrCodeURL,plusCode;
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        shortnameqr = findViewById(R.id.shortnameqr);
        forgotimage = findViewById(R.id.forgotimage);
        location = findViewById(R.id.location);
        pluscodeqr = findViewById(R.id.pluscodeqr);


        address = getIntent().getExtras().getString("address");
        shortName = getIntent().getExtras().getString("shortName");
        plusCode = getIntent().getExtras().getString("plusCode");
        qrCodeURL = getIntent().getExtras().getString("qrCodeURL");
        Log.d(TAG, "onCreate: "+qrCodeURL);

        shortnameqr.setText(shortName);
        location.setText(address);
        pluscodeqr.setText(plusCode);



        try {
            Picasso.with(getApplicationContext()).load(qrCodeURL).placeholder(R.drawable.qrcode).into(forgotimage, new Callback() {
                @Override
                public void onSuccess() {
                    forgotimage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent in = new Intent(getApplicationContext(), ImagePreviewActivity.class);
                            in.putExtra("image", qrCodeURL);
                            startActivity(in);
                        }
                    });
                }

                @Override
                public void onError() {

                }
            });

        } catch (Exception e) {
            Log.e("exeption", e.toString());
        }



        imageView = findViewById(R.id.back_press);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }


        });
    }

    @Override
    public void onBackPressed() {
        //Execute your code here
        finish();

    }

}





