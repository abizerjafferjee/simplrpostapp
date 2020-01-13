package com.codeapex.simplrpostprod.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.codeapex.simplrpostprod.Adapter.AdaterProfileSettings;
import com.codeapex.simplrpostprod.ModelClass.MyList;
import com.codeapex.simplrpostprod.R;

public class ProfileSettingActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView imageView;
    RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);
        recyclerView = findViewById(R.id.recyclerview);
        relativeLayout = findViewById(R.id.toolbar_lay);




        MyList[] myList = new MyList[] {
                new MyList("Change Password",R.drawable.changepassword),
                new MyList("Deactivate Account",R.drawable.deactivate),
                new MyList("Delete Account",R.drawable.delete),
                new MyList("Logout",R.drawable.logoutnew),
        };

//
//        relativeLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ProfileSettingActivity.this,MyAddressActivity.class);
//                startActivity(intent);
//            }
//        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        AdaterProfileSettings custom_adaterFriends = new AdaterProfileSettings(getApplicationContext(),myList);
        recyclerView.setAdapter(custom_adaterFriends);



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
