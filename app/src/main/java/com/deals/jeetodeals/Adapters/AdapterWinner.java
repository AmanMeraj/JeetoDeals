package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deals.jeetodeals.Model.DrawResponse;
import com.deals.jeetodeals.Model.WinnerResponse;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.Utils.SharedPref;
import com.deals.jeetodeals.databinding.RowDrawsBinding;
import com.deals.jeetodeals.databinding.RowWinnersBinding;

import java.util.ArrayList;

public class AdapterWinner extends RecyclerView.Adapter<AdapterWinner.ViewHolder> {

    public AdapterWinner(Context context, ArrayList<WinnerResponse> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    private final Context context;
    SharedPref pref=new SharedPref();
    private final ArrayList<WinnerResponse> itemList;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowWinnersBinding binding = RowWinnersBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WinnerResponse item=itemList.get(position);
        Glide.with(context).load(item.getImage()).placeholder(R.drawable.no_image).into(holder.binding.image);
        holder.binding.name.setText(item.winner_name);
        holder.binding.desc2.setText(item.prize);
        holder.binding.ticket.setText(item.title);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public  class  ViewHolder extends RecyclerView.ViewHolder {
        RowWinnersBinding binding;
        public ViewHolder(@NonNull RowWinnersBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}

