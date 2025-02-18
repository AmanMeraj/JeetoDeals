package com.deals.jeetodeals.BottomSheet;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.deals.jeetodeals.Adapters.BottomImagePagerAdapter;
import com.deals.jeetodeals.Fragments.HomeFragment.HomeResponse;
import com.deals.jeetodeals.Model.Image;
import com.deals.jeetodeals.Model.Promotion;
import com.deals.jeetodeals.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.relex.circleindicator.CircleIndicator3;

public class BottomSheetPromotion extends BottomSheetDialogFragment {

    private static final String ARG_PROMOTION = "promotion";
    private HomeResponse homeResponse;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;

    public static BottomSheetPromotion newInstance(HomeResponse homeResponse) {
        BottomSheetPromotion fragment = new BottomSheetPromotion();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROMOTION, homeResponse); // Pass HomeResponse object
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            homeResponse = (HomeResponse) getArguments().getSerializable(ARG_PROMOTION); // Retrieve HomeResponse object
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

        // Set up the ViewPager2 and the CircleIndicator
        setupViewPager(viewPager, indicator);

        // Find and set up TextViews
        TextView heading = view.findViewById(R.id.tv_iphone_name);
        TextView drawDate = view.findViewById(R.id.tv_desc);
        TextView desc = view.findViewById(R.id.tv_draw_date);
        TextView vouchers = view.findViewById(R.id.tv_vouchers);
        TextView sold = view.findViewById(R.id.sold_tv);
        String endDate = homeResponse.getExtensions().getCustom_lottery_data().getLottery_dates_to(); // e.g., "2025-05-31 00:00"
        String formattedDate = formatDate(endDate);

        if (homeResponse != null) {
            heading.setText(homeResponse.getName());
            desc.setText(Html.fromHtml(homeResponse.getDescription(), Html.FROM_HTML_MODE_LEGACY).toString());
            vouchers.setText(homeResponse.getPrices().getPrice()+" Vouchers");
            drawDate.setText("Draw date: " + formattedDate + " or earlier based on the time passed.");
            sold.setText(homeResponse.getExtensions().getCustom_lottery_data().getTotal_sales()+" sold out of "+homeResponse.getExtensions().getCustom_lottery_data().getMax_tickets());
//            winText.setText(promotion.getWinText());
        }

        return view;
    }
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            return (date != null) ? outputFormat.format(date) : "Invalid date";
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid date";
        }
    }

    private void setupViewPager(ViewPager2 viewPager, CircleIndicator3 indicator) {
        List<String> imageUrls = new ArrayList<>();
        // Sample images for testing
        if (homeResponse != null && homeResponse.getImages() != null) {

            for (Image image : homeResponse.getImages()) {
                imageUrls.add(image.getSrc());  // Get image URL from the Promotion object
            }
        }

        // Set adapter
        BottomImagePagerAdapter adapter = new BottomImagePagerAdapter(requireContext(), imageUrls);
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
