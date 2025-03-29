package com.deals.jeetodeals.BottomSheet;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.deals.jeetodeals.Adapters.BottomImagePagerAdapter;
import com.deals.jeetodeals.Adapters.SizeSelectorAdapter;
import com.deals.jeetodeals.Model.Attribute;
import com.deals.jeetodeals.Model.Image;
import com.deals.jeetodeals.Model.ShopResponse;
import com.deals.jeetodeals.Model.Variations;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.VerticalTextView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import me.relex.circleindicator.CircleIndicator3;
import java.util.ArrayList;
import java.util.List;

public class ShopBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private ViewPager2 viewPager;
    private VerticalTextView cointxt;
    private CircleIndicator3 circleIndicator;
    private RecyclerView sizeRecyclerView;
    private TextView quantityText,buttonTxt;
    private ImageView minusButton;
    private ImageView plusButton,closeButton;
    private View addToCartButton;
    private TextView productNameTextView;
    private TextView drawDateTextView;
    private BottomImagePagerAdapter imageAdapter;
    private ArrayList<Image> imageList;
    private ShopResponse shopItem;
    private String selectedSize = "";
    private int currentQuantity = 1;
    private AddToCartListener cartListener;

    public interface AddToCartListener {
        void onCartItemAdded(ShopResponse item, String size, int quantity);
    }

    public void setAddToCartListener(AddToCartListener listener) {
        this.cartListener = listener;
    }

    public static ShopBottomSheetDialogFragment newInstance(ShopResponse shopItem) {
        ShopBottomSheetDialogFragment fragment = new ShopBottomSheetDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("shop_item", shopItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            shopItem = (ShopResponse) getArguments().getSerializable("shop_item");
            if (shopItem != null && shopItem.getImages() != null) {
                imageList = shopItem.getImages();
            } else {
                imageList = new ArrayList<>();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.shop_bottom, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupData();
        setupViewPager();
        setupSizeSelector();
        setupQuantityControls();
        setupAddToCart();
        SetupCloseButton();

    }

    private void initViews(View view) {
        viewPager = view.findViewById(R.id.vp_images);
        circleIndicator = view.findViewById(R.id.circle_indicator);
        sizeRecyclerView = view.findViewById(R.id.rc_size);
        quantityText = view.findViewById(R.id.text_quantity);
        minusButton = view.findViewById(R.id.minus_btn);
        plusButton = view.findViewById(R.id.plus_btn);
        addToCartButton = view.findViewById(R.id.button_rel);
        buttonTxt=view.findViewById(R.id.add);
        productNameTextView = view.findViewById(R.id.tv_iphone_name);
        drawDateTextView = view.findViewById(R.id.tv_desc);
        closeButton=view.findViewById(R.id.close_img);
        cointxt=view.findViewById(R.id.coins_tv);

    }

    private void setupData() {
        if (shopItem != null) {
            productNameTextView.setText(shopItem.getName());
            cointxt.setText(shopItem.getPrices().getPrice()+" "+shopItem.getPrices().getCurrency_prefix());
            drawDateTextView.setText(Html.fromHtml(shopItem.getDescription(), Html.FROM_HTML_MODE_LEGACY).toString());

        }
    }

    private void setupViewPager() {
        // Make sure we have a valid imageList
        if (imageList == null) {
            imageList = new ArrayList<>();
        }

        // Convert your ArrayList<Image> to the format expected by your adapter
        List<String> imageUrls = new ArrayList<>();

        // Extract the image URLs from your Image objects
        for (Image image : imageList) {
            // Assuming your Image class has a getSrc() method to get the URL
            String imageUrl = image.getSrc();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                imageUrls.add(imageUrl);
            }
        }

        // Initialize the adapter with the list of image URLs
        imageAdapter = new BottomImagePagerAdapter(requireContext(), imageUrls);
        viewPager.setAdapter(imageAdapter);
        circleIndicator.setViewPager(viewPager);
    }

    private void setupSizeSelector() {
        List<String> sizes = new ArrayList<>();
        View sizeSection = getView().findViewById(R.id.rel_size); // Container for size selector
        TextView selectSizeTextView = getView().findViewById(R.id.tv_select_size); // "Select Size" TextView

        if (shopItem != null && shopItem.getVariations() != null && !shopItem.getVariations().isEmpty()) {
            for (Variations variation : shopItem.getVariations()) {
                if (variation.getAttributes() != null) {
                    for (Attribute attr : variation.getAttributes()) {
                        if ("Size".equals(attr.getName())) {
                            sizes.add(attr.getValue());
                            break; // Stop checking once size is found
                        }
                    }
                }
            }

            // Set up the size selector adapter
            SizeSelectorAdapter sizeAdapter = new SizeSelectorAdapter(sizes, new SizeSelectorAdapter.OnSizeSelectedListener() {
                @Override
                public void onSizeSelected(String size) {
                    selectedSize = size;
                }
            });
            sizeRecyclerView.setAdapter(sizeAdapter);

            // Show size section and "Select Size" TextView
            if (sizeSection != null) sizeSection.setVisibility(View.VISIBLE);
            if (selectSizeTextView != null) selectSizeTextView.setVisibility(View.VISIBLE);

        } else {
            // Hide size section and "Select Size" TextView
            if (sizeSection != null) sizeSection.setVisibility(View.INVISIBLE);
            if (selectSizeTextView != null) selectSizeTextView.setVisibility(View.INVISIBLE);

            // Set a default value for selected size
            selectedSize = "";
        }
    }


    private void setupQuantityControls() {
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuantity > 1) {
                    currentQuantity--;
                    updateQuantityDisplay();
                }
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentQuantity++;
                updateQuantityDisplay();
            }
        });
    }
    public void SetupCloseButton(){
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void updateQuantityDisplay() {
        quantityText.setText(String.valueOf(currentQuantity));
    }

    private void setupAddToCart() {
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    addToCartButton.setEnabled(false);
                    buttonTxt.setText("Adding...");
                    if (cartListener != null) {
                        cartListener.onCartItemAdded(shopItem, selectedSize, currentQuantity);
                    }
                    new Handler().postDelayed(() -> {
                        addToCartButton.setEnabled(true);
                        buttonTxt.setText("Add to Cart");
                    }, 1000);
                    dismiss();
                }
            }
        });
    }

    private boolean validateInputs() {
        boolean isVariable = "variable".equalsIgnoreCase(shopItem.getType());

        if (isVariable && selectedSize.isEmpty()) {
            Toast.makeText(requireContext(), "Please select a size", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}

// Size Selector Adapter (updated for ShopResponse)
