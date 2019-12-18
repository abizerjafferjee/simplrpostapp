package com.codeapex.simplrpostprod.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImagePreviewActivity extends AppCompatActivity {
    ImageView imgShow,imgCross;
    ProgressBar Loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        Loader = findViewById(R.id.Loader);
        imgShow = findViewById(R.id.imgShow);
        UtilityClass.showLoading(Loader,ImagePreviewActivity.this);
        Picasso.with(ImagePreviewActivity.this)
                .load(getIntent().getStringExtra("image"))
                .into(imgShow, new Callback() {
                    @Override
                    public void onSuccess() {
                        UtilityClass.hideLoading(Loader,ImagePreviewActivity.this);
                    }

                    @Override
                    public void onError() {
                        UtilityClass.hideLoading(Loader,ImagePreviewActivity.this);
                    }
                });
        imgShow.setOnTouchListener(new ImageMatrixTouchHandler(getApplicationContext()));
        imgCross = findViewById(R.id.imgCross);
        imgCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
