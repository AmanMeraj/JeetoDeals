package com.deals.jeetodeals.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.deals.jeetodeals.Adapters.AdapterCard2;
import com.deals.jeetodeals.Adapters.CategoryAdapter;
import com.deals.jeetodeals.Model.AllItems;
import com.deals.jeetodeals.Model.Category;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.databinding.FragmentShopBinding;

import java.util.Arrays;
import java.util.List;

public class ShopFragment extends Fragment {
    FragmentShopBinding binding;
    private View selectedRelativeLayout;
    private View selectedView; // To track the currently selected view

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShopBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();

        List<Category> categories = Arrays.asList(
                new Category("Men's", R.drawable.mens),
                new Category("Women's", R.drawable.womens),
                new Category("Kids", R.drawable.kids),
                new Category("Accessories", R.drawable.accessories)
        );

        // Set up the adapter
        CategoryAdapter adapter = new CategoryAdapter(categories, this::handleCategoryClick);

        // Set up RecyclerView
        binding.rcCategory.setAdapter(adapter);

        List<AllItems> promotions = Arrays.asList(
                new AllItems("Tracking 658", "Draw date: 31 December 2024", R.drawable.promotion_image),
                new AllItems("Tracking 300", "Draw date: 1 January 2025", R.drawable.promotion_image),
                new AllItems("Tracking 100", "Draw date: 15 December 2024", R.drawable.promotion_image)
        );

        AdapterCard2 adapter2 = new AdapterCard2(promotions);
        binding.rcItems.setAdapter(adapter2);

        return v;
    }

    private void handleCategoryClick(Category category, ImageView imageView) {
        View parent = (View) imageView.getParent(); // Get the parent view of the clicked image
        RelativeLayout relativeLayout = parent.findViewById(R.id.rel_bg); // Target the specific RelativeLayout

        // Reset background of previously selected RelativeLayout
        if (selectedRelativeLayout != null && selectedRelativeLayout != relativeLayout) {
            selectedRelativeLayout.setBackgroundResource(R.drawable.category_bg); // Reset to default background
        }

        // Update background of the currently selected RelativeLayout
        relativeLayout.setBackgroundResource(R.drawable.selected_category_bg);

        // Save the current RelativeLayout as the selected one
        selectedRelativeLayout = relativeLayout;

        // Scale up animation
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(imageView, View.SCALE_X, 1.2f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(imageView, View.SCALE_Y, 1.2f);
        scaleUpX.setDuration(300);
        scaleUpY.setDuration(300);

        AnimatorSet scaleUp = new AnimatorSet();
        scaleUp.playTogether(scaleUpX, scaleUpY);
        scaleUp.start();

        // Scale down after scaling up (to avoid perpetual scaling)
        scaleUp.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(imageView, View.SCALE_X, 1f);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(imageView, View.SCALE_Y, 1f);
                scaleDownX.setDuration(300);
                scaleDownY.setDuration(300);

                AnimatorSet scaleDown = new AnimatorSet();
                scaleDown.playTogether(scaleDownX, scaleDownY);
                scaleDown.start();
            }
        });
    }
}
