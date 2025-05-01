package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deals.jeetodeals.Model.Image;
import com.deals.jeetodeals.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class ZoomImagePagerAdapter extends RecyclerView.Adapter<ZoomImagePagerAdapter.ViewHolder> {

    private List<Image> imageList;
    private Context context;

    public ZoomImagePagerAdapter(Context context, List<Image> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ZoomImagePagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_zoom_image_pager, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ZoomImagePagerAdapter.ViewHolder holder, int position) {
        Glide.with(context)
                .load(imageList.get(position).getSrc())
                .placeholder(R.drawable.no_image)
                .error(R.drawable.error_image)
                .into(holder.photoView);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        PhotoView photoView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.photoView);
        }
    }
}
