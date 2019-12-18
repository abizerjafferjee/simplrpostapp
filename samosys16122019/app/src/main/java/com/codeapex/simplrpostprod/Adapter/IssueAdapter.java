package com.codeapex.simplrpostprod.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.ModelClass.IssueModel;
import com.codeapex.simplrpostprod.R;

import java.util.List;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.Holder> {
    public static Context context;
    private List<IssueModel> model;

    public interface OnItemClickListener {
        void onItemClick(IssueModel item, int position);
    }

    private final OnItemClickListener listener;

    public IssueAdapter(Context context, List<IssueModel> model, OnItemClickListener listener) {
        this.model = model;
        this.context = context;
        this.listener = listener;
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.issuelayout, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int i) {
        holder.bind(model.get(i), listener);
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView txtIssue;
        ConstraintLayout open;

        public Holder(View v) {
            super(v);

            txtIssue = v.findViewById(R.id.txtIssue);
            open = v.findViewById(R.id.open);

        }

        public void bind(final IssueModel issueModel, final OnItemClickListener listener) {
            txtIssue.setText(issueModel.getIssue());
            open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    issueModel.setType("clicked");
                    listener.onItemClick(issueModel, getAdapterPosition());
                }
            });

        }
    }
}
