package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deals.jeetodeals.Fragments.HomeFragment.HomeResponse;
import com.deals.jeetodeals.Model.DrawResponse;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.Utils.SharedPref;
import com.deals.jeetodeals.databinding.RowCategory2Binding;
import com.deals.jeetodeals.databinding.RowDrawsBinding;

import java.util.ArrayList;

public class AdapterDraw extends RecyclerView.Adapter<AdapterDraw.ViewHolder> {

    public AdapterDraw(Context context, ArrayList<DrawResponse> itemList, OnButtonClickListner listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }

    private final Context context;
    SharedPref pref=new SharedPref();
    private final ArrayList<DrawResponse> itemList;
    private final OnButtonClickListner listener;

    public interface OnButtonClickListner {
        void onButtonClicked(DrawResponse item);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowDrawsBinding binding = RowDrawsBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DrawResponse item=itemList.get(position);
        Glide.with(context).load(item.getImage()).placeholder(R.drawable.no_image).into(holder.binding.image);
        if(item.getDraw_button_link()!=null){
            holder.binding.name.setVisibility(View.VISIBLE);
        }else {
            holder.binding.name.setVisibility(View.INVISIBLE);
        }
        holder.binding.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.binding.name.setEnabled(false);
                holder.binding.name.setText("Loading...");
                if (listener != null) {
                    listener.onButtonClicked(item);
                }
                new Handler().postDelayed(() -> {
                    holder.binding.name.setEnabled(true);
                    holder.binding.name.setText("View Draw");
                }, 1000);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public  class  ViewHolder extends RecyclerView.ViewHolder {
        RowDrawsBinding binding;
        public ViewHolder(@NonNull RowDrawsBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
