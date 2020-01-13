package com.codeapex.simplrpostprod.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codeapex.simplrpostprod.R;

public class FAQDetailActivity extends AppCompatActivity {

    ImageView back_press;
    TextView txtQuestion,txtAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        back_press = findViewById(R.id.back_press);
        back_press.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FAQDetailActivity.super.onBackPressed();
            }
        });
        txtAnswer = findViewById(R.id.txtAnswer);
        txtQuestion = findViewById(R.id.txtQuestion);
        txtQuestion.setText(getIntent().getStringExtra("qustion"));
        txtAnswer.setText(getIntent().getStringExtra("answer"));
    }
}
