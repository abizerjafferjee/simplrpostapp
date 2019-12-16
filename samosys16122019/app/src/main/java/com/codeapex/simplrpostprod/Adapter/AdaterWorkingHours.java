package com.codeapex.simplrpostprod.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.ModelClass.AddPhoneData;
import com.codeapex.simplrpostprod.R;

import java.util.List;

public class AdaterWorkingHours extends RecyclerView.Adapter<AdaterWorkingHours.Holderclass>{
    Context applicationContext;
    private  List<AddPhoneData> myListData;
    private int selectedPosition;




    public AdaterWorkingHours(Context applicationContext, List<AddPhoneData> workingHour) {
        this.applicationContext = applicationContext;
        this.myListData = workingHour;
    }

    @NonNull
    @Override
    public AdaterWorkingHours.Holderclass onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.avialble_card,viewGroup,false);
        AdaterWorkingHours.Holderclass holderclass =new AdaterWorkingHours.Holderclass(view);


        return holderclass;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaterWorkingHours.Holderclass viewHolder, final int position) {
        viewHolder.dayname.setText(myListData.get(position).getDayName());
        String closeTime = myListData.get(position).getCloseTime();
        String opentinme = myListData.get(position).getOpenTime();
        String strworkingHour = opentinme + "-" + closeTime;

        if (myListData.get(position).getIsOpen() == 1){
            viewHolder.workingHour.setText(strworkingHour);

        }else if(myListData.get(position).getIsOpen()== 0){
            viewHolder.workingHour.setText("Closed");


        }




    }


    @Override
    public int getItemCount() {
        return myListData.size();
    }

    public class Holderclass extends RecyclerView.ViewHolder{
        TextView dayname,workingHour; ImageView imageView;
        public Holderclass(@NonNull View itemView)
        {

            super(itemView);
            dayname = (TextView) itemView.findViewById(R.id.dayname);
            workingHour = (TextView) itemView.findViewById(R.id.workingHour);

        }

    }
}




