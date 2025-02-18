package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deals.jeetodeals.R;

import java.util.List;

public class BottomImagePagerAdapter extends RecyclerView.Adapter<BottomImagePagerAdapter.ViewHolder> {

    private List<String> imageList;
    private Context context;

    public BottomImagePagerAdapter(Context context, List<String> imageList) {
        this.imageList = imageList;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl = imageList.get(position);

        Glide.with(context)
                .load(imageUrl)  // Load the image URL
                .placeholder(R.drawable.no_image)  // Placeholder image while loading
                .error(R.drawable.error_image)  // Image to display in case of an error
                .into(holder.imageView);  // Set the image to the ImageView
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}
