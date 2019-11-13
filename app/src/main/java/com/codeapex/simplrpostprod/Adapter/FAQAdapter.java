package com.codeapex.simplrpostprod.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.ModelClass.FAQModel;
import com.codeapex.simplrpostprod.R;

import java.util.List;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.Holder> {
    public static Context context;
    private List<FAQModel> model;

    public interface OnItemClickListener {
        void onItemClick(FAQModel item, int position);
    }
    private final OnItemClickListener listener;

    public FAQAdapter(Context context, List<FAQModel> model, OnItemClickListener listener) {
        this.model = model;
        this.context = context;
        this.listener = listener;
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.faqlayout, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder( final Holder holder, final int i) {
        holder.bind(model.get(i), listener);
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView txtQuestion;
        CardView crdOpen;

        public Holder(View v) {
            super(v);

            txtQuestion = v.findViewById(R.id.txtQuestion);
            crdOpen = v.findViewById(R.id.crdOpen);

        }

        public void bind(final FAQModel faqModel, final OnItemClickListener listener) {
            txtQuestion.setText(faqModel.getQuestion());
            crdOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    faqModel.setType("clicked");
                    listener.onItemClick(faqModel,getAdapterPosition());
                }
            });

        }
    }

}