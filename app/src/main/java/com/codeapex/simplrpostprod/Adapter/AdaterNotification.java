package com.codeapex.simplrpostprod.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.ModelClass.ModelNotification;
import com.codeapex.simplrpostprod.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class AdaterNotification extends RecyclerView.Adapter<AdaterNotification.Holderclass>{
    FragmentActivity activity;
    List<ModelNotification> models;


    public AdaterNotification(FragmentActivity activity, List<ModelNotification> models) {
        this.activity = activity;
        this.models = models;
    }


    @NonNull
    @Override
    public AdaterNotification.Holderclass onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_notifiaction,viewGroup,false);
        AdaterNotification.Holderclass holderclass =new AdaterNotification.Holderclass(view);


        return holderclass;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaterNotification.Holderclass viewHolder, int i) {
        viewHolder.notiinfo.setText(models.get(i).getNotificationInformation());
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date d = new Date();
        try {
            d = sdf1.parse(models.get(i).getCreateDate());
        } catch (ParseException ex) {

        }
        sdf1 = new SimpleDateFormat("dd MMM,yyyy hh:mm a");

        sdf1.setTimeZone(TimeZone.getDefault());
        String currentDateandTime = sdf1.format(d);
        viewHolder.date.setText(currentDateandTime);


    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class Holderclass extends RecyclerView.ViewHolder{
        TextView notiinfo,date;
        public Holderclass(@NonNull View itemView)
        {

            super(itemView);

            notiinfo = (TextView) itemView.findViewById(R.id.notifiactionInformation);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }
}

