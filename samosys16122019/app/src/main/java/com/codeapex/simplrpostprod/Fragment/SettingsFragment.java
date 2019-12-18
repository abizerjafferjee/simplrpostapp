package com.codeapex.simplrpostprod.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.Adapter.AdapterSettings;

import com.codeapex.simplrpostprod.BuildConfig;
import com.codeapex.simplrpostprod.ModelClass.MyListData;
import com.codeapex.simplrpostprod.R;


public class SettingsFragment extends Fragment {
    RecyclerView recyclerView;
    ImageView imageView;
    RelativeLayout relativeLayout;
    TextView txtVersion;
    //ArrayList personNames = new ArrayList<>(Arrays.asList("Profile Settings", "About Us", "Terms & Conditions", "Privacy Policy", "FAQActivity", "Feedback", "Contact Us","Rate Us","Refer a Friend"));
   // ArrayList personImages = new ArrayList<>(Arrays.asList(R.drawable.profilesetting, R.drawable.aboutus, R.drawable.term, R.drawable.privacypolicy, R.drawable.faq, R.drawable.feedback, R.drawable.contactusicon,R.drawable.rateus, R.drawable.refefri));
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_setting, container, false);
        txtVersion = view.findViewById(R.id.version);

        txtVersion.setText(BuildConfig.VERSION_NAME);


        MyListData[] myListData = new MyListData[] {
                new MyListData("Profile Settings",R.drawable.profilesettingnew),
                new MyListData("About Us",R.drawable.aboutus),
                new MyListData("Terms & Conditions",R.drawable.term),
                new MyListData("Privacy Policy",R.drawable.privacypolicy),
                new MyListData("FAQ",R.drawable.faq),
                new MyListData("Feedback",R.drawable.feedback),
//                new MyListData("Contact Us",R.drawable.contactusicon),
                new MyListData("Rate Us",R.drawable.rateus),
//                new MyListData("Refer a Friend",R.drawable.refefri),

        };



        relativeLayout = view.findViewById(R.id.toolbar_lay);

//        relativeLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(),ProfileSettingActivity.class);
//                startActivity(intent);
//            }
//        });
        recyclerView = view.findViewById(R.id.recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        AdapterSettings custom_adaterFriends = new AdapterSettings(getActivity(),myListData);
        recyclerView.setAdapter(custom_adaterFriends);



        imageView = view.findViewById(R.id.back_press);


        return view;
    }


}
