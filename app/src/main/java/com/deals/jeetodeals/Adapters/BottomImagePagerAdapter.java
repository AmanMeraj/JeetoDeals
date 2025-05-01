package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deals.jeetodeals.ActivityZoomableImageView;
import com.deals.jeetodeals.Model.Image;
import com.deals.jeetodeals.R;

import java.util.ArrayList;
import java.util.List;

public class BottomImagePagerAdapter extends RecyclerView.Adapter<BottomImagePagerAdapter.ImageViewHolder> {

    private Context context;
    private List<String> imageUrls;
    private ArrayList<Image> imageList; // Add this to store the complete Image objects

    public BottomImagePagerAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    // Add a setter method to set the complete image list
    public void setFullImageList(ArrayList<Image> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        // Ensure we have a valid image URL before loading
        String imageUrl = null;
        if (imageUrls != null && position < imageUrls.size()) {
            imageUrl = imageUrls.get(position);
        }

        // Load image using Glide with null checking
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.no_image) // Add a placeholder image resource
                    .error(R.drawable.error_image) // Add an error image resource
                    .into(holder.imageView);
        } else {
            // Set a default image if URL is null or empty
            holder.imageView.setImageResource(R.drawable.no_image); // Add a placeholder image resource
        }

        // Set click listener on the image
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current position with getBindingAdapterPosition to avoid potential bugs
                int currentPos = holder.getBindingAdapterPosition();
                if (currentPos != RecyclerView.NO_POSITION && imageList != null) {
                    // Launch the zoomable image activity
                    Intent intent = new Intent(context, ActivityZoomableImageView.class);
                    intent.putExtra("image_list", imageList);
                    intent.putExtra("position", currentPos);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}