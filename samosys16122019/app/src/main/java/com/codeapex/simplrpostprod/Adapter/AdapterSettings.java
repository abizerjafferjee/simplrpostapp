package com.codeapex.simplrpostprod.Adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.Activity.AboutActivity;
import com.codeapex.simplrpostprod.Activity.FAQActivity;
import com.codeapex.simplrpostprod.Activity.FeedbackActivity;
import com.codeapex.simplrpostprod.Activity.PrivacyPolicyActivity;
import com.codeapex.simplrpostprod.Activity.ReferFriendActivity;
import com.codeapex.simplrpostprod.Activity.TermActivity;
import com.codeapex.simplrpostprod.ModelClass.MyListData;
import com.codeapex.simplrpostprod.Activity.ProfileSettingActivity;
import com.codeapex.simplrpostprod.R;

public class AdapterSettings extends RecyclerView.Adapter<AdapterSettings.Holderclass> {
    Context applicationContext;
    private MyListData[] listdata;
    private int selectedPosition;


    public AdapterSettings(Context applicationContext, MyListData[] myListData) {
        this.applicationContext = applicationContext;
        this.listdata = myListData;

    }

    @NonNull
    @Override
    public AdapterSettings.Holderclass onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.setting_card,viewGroup,false);
        AdapterSettings.Holderclass holderclass =new AdapterSettings.Holderclass(view);


        return holderclass;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterSettings.Holderclass viewHolder, final int position) {
        final MyListData myListData = listdata[position];
        viewHolder.text.setText(listdata[position].getDescription());

        viewHolder.imageView.setImageResource(listdata[position].getImgId());
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                switch (position)
                {
                    case 0 :
                        Intent intent = new Intent(applicationContext, ProfileSettingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        applicationContext.startActivity(intent);
                        break;

                    case 1 :
                        Intent intentt = new Intent(applicationContext, AboutActivity.class);
                        intentt.putExtra("toolbar_heading", "About Us");
                       // intentt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        applicationContext.startActivity(intentt);
                        break;
                    case 2 :
                        Intent abourt = new Intent(applicationContext, TermActivity.class);
                        abourt.putExtra("toolbar_heading", "Privacy Policy");
                       // abourt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        applicationContext.startActivity(abourt);
                        break;

                    case 3 :
                        Intent privacy = new Intent(applicationContext, PrivacyPolicyActivity.class);
                        privacy.putExtra("toolbar_heading", "Terms & Conditions");
                       // privacy.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        applicationContext.startActivity(privacy);
                        break;

                    case 4:
                        Intent Faq = new Intent(applicationContext, FAQActivity.class);
                        Faq.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        applicationContext.startActivity(Faq);
                        break;

                    case 5 :
                        Intent feedback = new Intent(applicationContext, FeedbackActivity.class);
                        feedback.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        applicationContext.startActivity(feedback);
                        break;

                   // case 6 :
//                        Intent contact = new Intent(applicationContext,PrivacyPolicyActivity.class);
//                        contact.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        applicationContext.startActivity(contact);
//                        break;
                    case 7 :
                        Log.e("packageName",applicationContext.getPackageName());
                        Uri uri = Uri.parse("market://details?id=" + applicationContext.getPackageName());
                        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        try
                        {
                            applicationContext.startActivity(myAppLinkToMarket);
                        }
                        catch (ActivityNotFoundException e)
                        {
                            Toast.makeText(applicationContext, " Sorry, Not able to open!", Toast.LENGTH_SHORT).show();
                        }
                        break;
//                    case 8:
//                        Intent refer = new Intent(applicationContext, ReferFriendActivity.class);
//                        refer.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        applicationContext.startActivity(refer);
//                        break;


                }


            }
        });
    }


    @Override
    public int getItemCount() {
        return listdata.length;
    }

    public class Holderclass extends RecyclerView.ViewHolder{
        TextView text;
        CardView cardView;
        ImageView imageView;
        public Holderclass(@NonNull View itemView)
        {

            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardview);
            text = (TextView) itemView.findViewById(R.id.textView2);
            imageView = (ImageView) itemView.findViewById(R.id.imageView2);
        }

    }
}

