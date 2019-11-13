package com.codeapex.simplrpostprod.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.codeapex.simplrpostprod.R;

public class ReferFriendActivity extends AppCompatActivity {
    ImageView imgBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refer_friend);




        imgBackButton = findViewById(R.id.back_press);
        imgBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }


        });
    }


}

