package com.deals.jeetodeals;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.deals.jeetodeals.Adapters.SmallImagesAdapter;
import com.deals.jeetodeals.Adapters.ZoomImagePagerAdapter;
import com.deals.jeetodeals.Model.Image;
import com.deals.jeetodeals.databinding.ActivityZoomableImageViewBinding;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

public class ActivityZoomableImageView extends AppCompatActivity {
    private ActivityZoomableImageViewBinding binding;
    private ArrayList<Image> imageList;
    private int currentPosition = 0;
    private SmallImagesAdapter thumbnailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityZoomableImageViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get data from intent
        if (getIntent() != null) {
            imageList = (ArrayList<Image>) getIntent().getSerializableExtra("image_list");
            currentPosition = getIntent().getIntExtra("position", 0);
        } else {
            imageList = new ArrayList<>();
        }

        setupUI();
    }

    private void setupUI() {
        binding.close.setOnClickListener(v -> finish());

        // Setup ViewPager
        ZoomImagePagerAdapter pagerAdapter = new ZoomImagePagerAdapter(this, imageList);
        binding.viewPager.setAdapter(pagerAdapter);
        binding.viewPager.setCurrentItem(currentPosition, false);

        // Sync thumbnail on swipe
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                thumbnailAdapter.setSelectedPosition(position);
                binding.rcSmallImages.scrollToPosition(position);
            }
        });

        // Setup thumbnails
        binding.rcSmallImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        thumbnailAdapter = new SmallImagesAdapter(this, imageList, position -> {
            binding.viewPager.setCurrentItem(position, true);
        });
        binding.rcSmallImages.setAdapter(thumbnailAdapter);

        thumbnailAdapter.setSelectedPosition(currentPosition);
        binding.rcSmallImages.scrollToPosition(currentPosition);
    }
}