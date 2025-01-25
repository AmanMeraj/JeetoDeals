package com.deals.jeetodeals.BottomSheet;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.deals.jeetodeals.Adapters.BottomImagePagerAdapter;
import com.deals.jeetodeals.Model.Promotion;
import com.deals.jeetodeals.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

public class BottomSheetPromotion extends BottomSheetDialogFragment {

    private static final String ARG_PROMOTION = "promotion";
    private Promotion promotion;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;

    public static BottomSheetPromotion newInstance(Promotion promotion) {
        BottomSheetPromotion fragment = new BottomSheetPromotion();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROMOTION, promotion);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            promotion = (Promotion) getArguments().getSerializable(ARG_PROMOTION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.voucher_detail_bottom_sheet, container, false);

        // Initialize views
        viewPager = view.findViewById(R.id.vp_images);
        CircleIndicator3 indicator = view.findViewById(R.id.circle_indicator);

        indicator.setBackgroundColor(getResources().getColor(R.color.grey));
        indicator.setBackgroundColor(getResources().getColor(R.color.orange));

        // Set up the ViewPager2 and the CircleIndicator
        setupViewPager(viewPager, indicator);

        // Find and set up TextViews
        TextView heading = view.findViewById(R.id.tv_iphone_name);
        TextView description = view.findViewById(R.id.tv_desc);
        TextView winText = view.findViewById(R.id.tv_draw_date);

        if (promotion != null) {
            heading.setText(promotion.getHeading());
            description.setText(promotion.getDescription());
            winText.setText(promotion.getWinText());
        }

        return view;
    }

    private void setupViewPager(ViewPager2 viewPager, CircleIndicator3 indicator) {
        // Sample images for testing
        List<Integer> sampleImages = new ArrayList<>();
        sampleImages.add(R.drawable.promotion_image);
        sampleImages.add(R.drawable.promotion_image);
        sampleImages.add(R.drawable.promotion_image);

        // Set adapter
        BottomImagePagerAdapter adapter = new BottomImagePagerAdapter(requireContext(), sampleImages);
        viewPager.setAdapter(adapter);

        // Link CircleIndicator3 with ViewPager2
        indicator.setViewPager(viewPager);

        // Enable auto-scroll for the ViewPager2
        enableAutoScroll(viewPager);
    }

    private void enableAutoScroll(ViewPager2 viewPager) {
        autoScrollHandler = new Handler();
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (viewPager.getAdapter() != null && viewPager.getAdapter().getItemCount() > 0) {
                    int nextItem = (viewPager.getCurrentItem() + 1) % viewPager.getAdapter().getItemCount();
                    viewPager.setCurrentItem(nextItem, true);
                    autoScrollHandler.postDelayed(this, 3000); // Scroll every 3 seconds
                }
            }
        };
        autoScrollHandler.postDelayed(autoScrollRunnable, 3000);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (autoScrollHandler != null && autoScrollRunnable != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
        }
    }
}
