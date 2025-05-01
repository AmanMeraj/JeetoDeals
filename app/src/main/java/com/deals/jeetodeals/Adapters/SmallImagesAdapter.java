package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deals.jeetodeals.Model.Image;
import com.deals.jeetodeals.R;

import java.util.ArrayList;

public class SmallImagesAdapter extends RecyclerView.Adapter<SmallImagesAdapter.ImageViewHolder> {

    private Context context;
    private ArrayList<Image> imageList;
    private OnImageClickListener listener;
    private int selectedPosition = 0;

    public interface OnImageClickListener {
        void onImageClick(int position);
    }

    public SmallImagesAdapter(Context context, ArrayList<Image> imageList, OnImageClickListener listener) {
        this.context = context;
        this.imageList = imageList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_zoom_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        // Load image with Glide
        String imageUrl = imageList.get(position).getSrc();
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.no_image)
                .error(R.drawable.error_image)
                .into(holder.imageView);

        // Set selected border/background
        if (position == selectedPosition) {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.grey)); // or use a custom selector/border
        } else {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onImageClick(selectedPosition);
            }
        });
    }


    @Override
    public int getItemCount() {
        return imageList != null ? imageList.size() : 0;
    }

    public void setSelectedPosition(int position) {
        if (position >= 0 && position < getItemCount()) {
            int previousSelected = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
        }
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CardView cardView;


        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img);
            cardView = itemView.findViewById(R.id.cardView);

        }
    }
}
