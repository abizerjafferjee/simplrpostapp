package com.codeapex.simplrpostprod.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.Activity.Activity_Share;
import com.codeapex.simplrpostprod.ModelClass.shareItemsModel;
import com.codeapex.simplrpostprod.ModelClass.shareItemsModel;
import com.codeapex.simplrpostprod.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShareExternalAdapter extends RecyclerView.Adapter<ShareExternalAdapter.ViewHolder> {

    Context context;
    private List<shareItemsModel> originalData = null;


    public ShareExternalAdapter(Context context, List<shareItemsModel> arrayList) {
        this.originalData = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.external_app_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final shareItemsModel shareItemsModel = originalData.get(position);
        holder.txt_appName.setText(shareItemsModel.app);

        if (shareItemsModel.icon!=null){
            holder.appImage.setImageDrawable(shareItemsModel.icon);
        }

        holder.appImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                Log.e("APP PAckaage :","PAckaage"+originalData.get(position).packageName);
                shareIntent.setPackage(originalData.get(position).packageName);
                //shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey, check out my address on Simplr post at https://code-apex.com/simplrPost/PRI-" + Activity_Share.address_id);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey, check out my address on Simplr post at "+ Activity_Share.unique_link);
                try {
                    context.startActivity(shareIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(context,"App that you select have not been installed.",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return originalData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_appName;
        private ImageView appImage;

        public ViewHolder(View itemView) {
            super(itemView);
            txt_appName = itemView.findViewById(R.id.txt_appName);
            appImage = itemView.findViewById(R.id.appImage);
        }
    }

}
