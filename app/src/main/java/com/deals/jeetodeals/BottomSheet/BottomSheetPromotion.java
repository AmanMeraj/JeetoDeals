package com.deals.jeetodeals.BottomSheet;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.deals.jeetodeals.Adapters.BottomImagePagerAdapter;
import com.deals.jeetodeals.Fragments.HomeFragment.HomeResponse;
import com.deals.jeetodeals.Model.Image;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.Utils.SharedPref;
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
    private RelativeLayout addToCartButton;
    SharedPref pref= new SharedPref();
    private Context mContext;

    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;

    // Interface for callback to HomeFragment
    public interface AddToCartListener {
        void onAddToCart(HomeResponse item);
    }

    private AddToCartListener addToCartListener;

    public static BottomSheetPromotion newInstance(HomeResponse homeResponse) {
        BottomSheetPromotion fragment = new BottomSheetPromotion();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROMOTION, homeResponse); // Pass HomeResponse object
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Try to find the listener by traversing the fragment hierarchy
        addToCartListener = findAddToCartListener();
        mContext = context;
    }

    /**
     * Searches for an AddToCartListener implementation in parent fragments and activity
     */
    private AddToCartListener findAddToCartListener() {
        // First check direct parent fragment
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof AddToCartListener) {
            return (AddToCartListener) parentFragment;
        }

        // Then check all fragments in the activity
        if (getActivity() != null) {
            List<Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
            for (Fragment fragment : fragments) {
                if (fragment instanceof AddToCartListener) {
                    return (AddToCartListener) fragment;
                }
            }

            // Check if activity implements it
            if (getActivity() instanceof AddToCartListener) {
                return (AddToCartListener) getActivity();
            }
        }

        // Return null instead of throwing an exception
        return null;
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
        addToCartButton = view.findViewById(R.id.add_cart_btn); // Make sure this ID exists in your layout

        // Set up the ViewPager2 and the CircleIndicator
        setupViewPager(viewPager, indicator);

        // Find and set up TextViews
        TextView heading = view.findViewById(R.id.tv_iphone_name);
        TextView drawDate = view.findViewById(R.id.tv_desc);
        TextView desc = view.findViewById(R.id.tv_draw_date);
        TextView vouchers = view.findViewById(R.id.tv_vouchers);
        TextView sold = view.findViewById(R.id.sold_tv);

        if (homeResponse != null && homeResponse.getExtensions() != null &&
                homeResponse.getExtensions().getCustom_lottery_data() != null) {
            String endDate = homeResponse.getExtensions().getCustom_lottery_data().getLottery_dates_to(); // e.g., "2025-05-31 00:00"
            String formattedDate = formatDate(endDate);

            heading.setText(homeResponse.getName());
            desc.setText(Html.fromHtml(homeResponse.getDescription(), Html.FROM_HTML_MODE_LEGACY).toString());
            try {
                String price = homeResponse.getPrices().getPrice();
                int voucherRate = pref.getPrefInteger(getActivity(), pref.voucher_rate);

                if (voucherRate != 0) {
                    int calculatedVouchers = (int) (Float.parseFloat(price) / (float) voucherRate);
                    String fullText = calculatedVouchers + " Vouchers";

                    vouchers.setText(fullText);
                } else {
                    vouchers.setText("Invalid Rate");
                    Log.e("Adapter", "Voucher rate is zero or invalid");
                }
            } catch (NumberFormatException e) {
                Log.e("Adapter", "Error parsing price: " + homeResponse.getPrices().getPrice(), e);
                vouchers.setText("Invalid Price");
            }


            drawDate.setText("Draw date: " + formattedDate + " or earlier based on the time passed.");
            sold.setText(homeResponse.getExtensions().getCustom_lottery_data().getTotal_sales()+" sold out of "+homeResponse.getExtensions().getCustom_lottery_data().getMax_tickets());
        }

        // Set up the Add to Cart button click listener
        setupAddToCartButton();

        return view;
    }

    private void setupAddToCartButton() {
        if (addToCartButton != null) {
            addToCartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (homeResponse != null) {
                        if (addToCartListener != null) {
                            // Call the same method used in HomeFragment
                            addToCartListener.onAddToCart(homeResponse);
                            // Optionally dismiss the bottom sheet after adding to cart
                            dismiss();
                        } else {
                            // No listener found, provide a fallback mechanism
                            // For example, use a broadcast or directly call a method in your cart manager
                            Toast.makeText(requireContext(), "Adding to cart...", Toast.LENGTH_SHORT).show();
                            addToCartDirectly(homeResponse);
                            dismiss();
                        }
                    }
                }
            });
        }
    }

    /**
     * Fallback method to add item to cart directly when no listener is available
     */
    private void addToCartDirectly(HomeResponse item) {
        // Implement a direct way to add the item to cart
        // This could use a singleton cart manager, shared preferences, or broadcast
        // Example using broadcast:
        if (getActivity() != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("item", item);
            getActivity().getSupportFragmentManager().setFragmentResult("ADD_TO_CART_REQUEST", bundle);
        }
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