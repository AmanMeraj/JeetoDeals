package com.deals.jeetodeals.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.deals.jeetodeals.Adapters.AdapterCard2;
import com.deals.jeetodeals.Adapters.CategoryAdapter;
import com.deals.jeetodeals.Fragments.HomeFragment.HomeViewModel;
import com.deals.jeetodeals.Model.AddItems;
import com.deals.jeetodeals.Model.BannerResponse;
import com.deals.jeetodeals.Model.CartResponse;
import com.deals.jeetodeals.Model.Category;
import com.deals.jeetodeals.Model.Items;
import com.deals.jeetodeals.Model.ShopResponse;
import com.deals.jeetodeals.Model.Wishlist;
import com.deals.jeetodeals.Model.WishlistCreationResponse;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.Utils.SharedPref;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.Wishlist.WishlistAddResponse;
import com.deals.jeetodeals.Wishlist.WishlistViewModel;
import com.deals.jeetodeals.databinding.FragmentShopBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class ShopFragment extends Fragment implements AdapterCard2.OnItemClickListener {
    private static final String TAG = "ShopFragment";

    private FragmentShopBinding binding;
    private View selectedRelativeLayout;
    private final SharedPref pref = new SharedPref();
    private final Utility utility = new Utility();
    private int selectedCategoryId = -1; // Default value, indicating no category selected
    private List<ShopResponse> shopItems = new ArrayList<>();

    private CartResponse cartResponse;
    private Handler handler = new Handler();
    private BannerResponse bannerResponse;
    private ArrayList<Category>categories;
    private HomeViewModel viewModel;
    private FragmentsViewModel viewModel2;
    private boolean isPlaying = false;
    private WishlistViewModel wishlistViewModel;
    private WishlistAddResponse addResponse;
    private int currentPage = 1;
    private int productIdd ;
    private final int perPage = 6;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private AdapterCard2 adapter;
    private WishlistCreationResponse responsee;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShopBinding.inflate(getLayoutInflater());
        initializeViewModels();
        initializeRecyclerView();

        setupInitialData();
        setupScrollListener();

        return binding.getRoot();
    }

    private void initializeViewModels() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        viewModel2 = new ViewModelProvider(this).get(FragmentsViewModel.class);
        wishlistViewModel = new ViewModelProvider(this).get(WishlistViewModel.class);
    }

    private void initializeRecyclerView() {
        // Initialize adapter with the non-null list
        adapter = new AdapterCard2(shopItems);
        adapter.setOnItemClickListener(this);
        binding.rcItems.setAdapter(adapter);
    }

    private void setupInitialData() {
        if (utility.isInternetConnected(requireActivity())) {
            setupVideoView();
            getBanner();
            getCategory();
        } else {
            Toast.makeText(requireActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCategory() {
        String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);
        viewModel2.getCategory(auth).observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.isSuccess && response.data != null && !response.data.isEmpty()) {
                // Filter out unwanted categories
                List<Category> filteredCategories = new ArrayList<>();
                for (Category category : response.data) {
                    String categoryName = category.getName();
                    if (!categoryName.equalsIgnoreCase("Ongoing Promotion") &&
                            !categoryName.equalsIgnoreCase("Promotion") &&
                            !categoryName.equalsIgnoreCase("Uncategorized")) {
                        filteredCategories.add(category);
                    }
                }

                if (!filteredCategories.isEmpty()) {
                    categories = (ArrayList<Category>) filteredCategories;
                    setupCategories((ArrayList<Category>) filteredCategories);

                    // Automatically select and fetch products for the first valid category
                    Category firstCategory = categories.get(0);
                    fetchProductsByCategory(firstCategory.getId());

                    // Set UI as selected
                    binding.rcCategory.post(() -> {
                        View firstItemView = binding.rcCategory.getLayoutManager().findViewByPosition(0);
                        if (firstItemView != null) {
                            ImageView firstImageView = firstItemView.findViewById(R.id.image);
                            RelativeLayout firstRelativeLayout = firstItemView.findViewById(R.id.rel_bg);
                            updateCategorySelection(firstRelativeLayout);
                            animateCategorySelection(firstImageView);
                        }
                    });
                } else {
                    handleError("No valid categories found.");
                }
            } else {
                handleError(response != null ? response.message : "Unknown error");
            }
        });
    }



    private void setupCategories(ArrayList<Category> data) {
        CategoryAdapter adapter = new CategoryAdapter(data, this::handleCategoryClick,requireActivity());
        binding.rcCategory.setAdapter(adapter);
    }
    private void setupVideoView() {
        // Automatically start the video when it's loaded
        binding.videoView.setOnPreparedListener(mp -> {
            int videoWidth = mp.getVideoWidth();
            int videoHeight = mp.getVideoHeight();
            float videoProportion = (float) videoWidth / (float) videoHeight;

            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int newHeight = (int) (screenWidth / videoProportion);

            ViewGroup.LayoutParams layoutParams = binding.videoView.getLayoutParams();
            layoutParams.height = newHeight;
            binding.videoView.setLayoutParams(layoutParams);

            // Start the video automatically when it's prepared
            binding.videoView.start();
            binding.playPauseButton.setImageResource(R.drawable.pause_jd); // Set to pause icon
            isPlaying = true; // Set the state to playing
        });

        // Handle play/pause button click
        binding.playPauseButton.setOnClickListener(v -> {
            if (isPlaying) {
                binding.videoView.pause();
                binding.playPauseButton.setImageResource(R.drawable.play_jd); // Set play icon
            } else {
                binding.videoView.start();
                binding.playPauseButton.setImageResource(R.drawable.pause_jd); // Set pause icon
                hidePlayPauseButton();
            }
            isPlaying = !isPlaying;
        });

        // Handle video completion
        binding.videoView.setOnCompletionListener(mp -> {
            isPlaying = false;
            binding.playPauseButton.setImageResource(R.drawable.play_jd); // Reset to play icon
            binding.playPauseButton.setVisibility(View.VISIBLE); // Make the button visible again
        });

        // Handle click on video view to show the button for 2 seconds
        binding.videoView.setOnClickListener(v -> {
            binding.playPauseButton.setVisibility(View.VISIBLE); // Show the play/pause button
            handler.postDelayed(() -> binding.playPauseButton.setVisibility(View.INVISIBLE), 2000); // Hide after 2 seconds
        });
    }

    // Method to hide the play/pause button after 2 seconds of video play
    private void hidePlayPauseButton() {
        handler.postDelayed(() -> {
            if (binding != null) {
                binding.playPauseButton.setVisibility(View.INVISIBLE);
            }
        }, 2000);
    }

    public void getBanner() {
        String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);
        viewModel2.getBanner(auth).observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.isSuccess && response.data != null) {
                bannerResponse = response.data;

                // Prepare a list to hold image URLs (either image or video)
                List<String> imageUrlList = new ArrayList<>();

                // If there is a video URL, handle video logic as before
                if (bannerResponse.getVideo_banner_url() != null && !bannerResponse.getVideo_banner_url().isEmpty()) {
                    Uri videoUri = Uri.parse(bannerResponse.getJeeto_shop_banner_video());
                    binding.videoView.setVideoURI(videoUri);
                    binding.videoView.start();
                }

                // If there's an image URL, add it to the list
                if (bannerResponse.getImage_banner_url() != null && !bannerResponse.getImage_banner_url().isEmpty()) {
                    Glide.with(requireActivity())
                            .asBitmap()  // Load as Bitmap
                            .load(bannerResponse.getImage_banner_url())
                            .into(new CustomTarget<Bitmap>() {

                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                                    Drawable drawable = new BitmapDrawable(requireActivity().getResources(), resource);
                                    binding.bgImageRel.setBackground(drawable); // Set background dynamically
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    // Optional: Handle clearing of background if needed
                                }
                            });

                }


            } else {
                Toast.makeText(requireContext(), response != null ? response.message : "Unknown error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void handleCategoryClick(Category category, ImageView imageView, int id) {
        View parent = (View) imageView.getParent();
        RelativeLayout relativeLayout = parent.findViewById(R.id.rel_bg);

        updateCategorySelection(relativeLayout);
        animateCategorySelection(imageView);

        // Reset pagination state when switching categories
        selectedCategoryId = id;
        currentPage = 1;
        isLastPage = false;
        isLoading = false;
        shopItems.clear();
        adapter.notifyDataSetChanged();

        fetchProductsByCategory(id);
    }
    private void fetchProductsByCategory(int id) {
        if (isLoading || isLastPage) {
            return;
        }

        isLoading = true;
        binding.loader.rlLoader.setVisibility(View.VISIBLE); // Show loading indicator

        String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);

        viewModel.getShop(auth, "simple|variable", id, currentPage, perPage).observe(getViewLifecycleOwner(), response -> {
            isLoading = false;
            binding.loader.rlLoader.setVisibility(View.GONE); // Hide loading indicator

            if (response != null && response.isSuccess && response.data != null) {
                if (response.data.isEmpty()) {
                    isLastPage = true;
                    if (currentPage == 1) {
                        // No items found for this category
                        Toast.makeText(requireContext(), "No items found in this category", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Add new items and notify adapter
                    int startPos = shopItems.size();
                    shopItems.addAll(response.data);
                    adapter.notifyItemRangeInserted(startPos, response.data.size());
                    currentPage++;
                }
            } else {
                handleError(response != null ? response.message : "Unknown error");
            }
        });
    }

    private void setupScrollListener() {
        binding.rcItems.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && dy > 0) { // Scrolling down
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    // Load more when we're 3 items away from the end
                    if (!isLoading && !isLastPage &&
                            (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 3) {
                        if (selectedCategoryId != -1) {
                            Log.d(TAG, "Loading more items. Page: " + currentPage);
                            fetchProductsByCategory(selectedCategoryId);
                        }
                    }
                }
            }
        });
    }





    private void updateCategorySelection(RelativeLayout newSelection) {
        if (selectedRelativeLayout != null && selectedRelativeLayout != newSelection) {
            selectedRelativeLayout.setBackgroundResource(R.drawable.category_bg);
        }
        newSelection.setBackgroundResource(R.drawable.selected_category_bg);
        selectedRelativeLayout = newSelection;
    }

    private void animateCategorySelection(ImageView imageView) {
        AnimatorSet scaleUp = createScaleAnimation(imageView, 1.2f, 300);
        scaleUp.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                createScaleAnimation(imageView, 1.0f, 300).start();
            }
        });
        scaleUp.start();
    }

    private AnimatorSet createScaleAnimation(View view, float scale, long duration) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, scale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, scale);
        scaleX.setDuration(duration);
        scaleY.setDuration(duration);

        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(scaleX, scaleY);
        return animSet;
    }

    @Override
    public void onLikeClick(String productId) {
        binding.loader.rlLoader.setVisibility(View.VISIBLE);
        addToWishlist(productId);
        productIdd=Integer.parseInt(productId);
    }

    private void addToWishlist(String productId) {
        String auth = getAuthToken();
        Wishlist wishlist = new Wishlist();
        wishlist.setProduct_id(Integer.parseInt(productId));
        Log.d(TAG, "addToWishlist: "+wishlist.getProduct_id() +" "+auth);


        Log.d("WISHLIST", "addToWishlist: "+auth);
        Log.d("WISHLIST", "addToWishlist: "+wishlist.getProduct_id());

        wishlistViewModel.addWishlist(auth, wishlist).observe(getViewLifecycleOwner(), response -> {
            binding.loader.rlLoader.setVisibility(View.GONE);
            if (response != null && response.isSuccess && response.data != null) {
                addResponse = response.data;
                Toast.makeText(requireActivity(), response.message, Toast.LENGTH_SHORT).show();
            } else {
                handleError(response != null ? response.message : "Unknown error");
            }
        });
    }

    @Override
    public void onShareClick(String permalink) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, permalink);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    @Override
    public void onAddToCartClick(ShopResponse item) {
        String authToken = getAuthToken();
        String nonce = pref.getPrefString(requireActivity(), pref.nonce);

        // First check current cart state
        checkCartAndAddItem(item, authToken, nonce);
    }

    private void checkCartAndAddItem(ShopResponse item, String authToken, String nonce) {
        viewModel2.getCart(authToken).observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.isSuccess && response.data != null) {
                CartResponse currentCart = response.data;
                logCartState(item, currentCart);

                if (canAddDirectly(item, currentCart)) {
                    addItemToCart(item, authToken, nonce);
                } else {
                    showCartClearDialog(item, authToken, nonce);
                }
            } else {
                handleError(response != null ? response.message : "Failed to fetch cart");
            }
        });
    }

    private boolean canAddDirectly(ShopResponse newItem, CartResponse cart) {
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return true;
        }

        String cartType = cart.getItems().get(0).getType();
        String newItemType = newItem.getType();

        Log.d(TAG, "Cart type: " + cartType + ", New item type: " + newItemType);
        return cartType.equalsIgnoreCase(newItemType);
    }

    private void showCartClearDialog(ShopResponse item, String authToken, String nonce) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cart Conflict")
                .setMessage("Your cart contains items of a different type. Adding this item will clear your cart. Do you want to proceed?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    addItemToCart(item, authToken, nonce);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void addItemToCart(ShopResponse item, String authToken, String nonce) {
        AddItems addItems = new AddItems();
        addItems.setId(item.getId());
        addItems.setQuantity(1);

        viewModel2.AddToCart(authToken, nonce, addItems)
                .observe(getViewLifecycleOwner(), response -> {
                    if (response != null && response.isSuccess && response.data != null) {
                        cartResponse = response.data;
                        Toast.makeText(requireActivity(), "Item added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        handleError(response != null ? response.message : "Failed to add item");
                    }
                });
    }

    private String getAuthToken() {
        return "Bearer " + pref.getPrefString(requireContext(), pref.user_token);
    }

    private void logCartState(ShopResponse newItem, CartResponse cart) {
        Log.d(TAG, "New item type: " + newItem.getType());
        if (cart.getItems() != null) {
            for (Items cartItem : cart.getItems()) {
                Log.d(TAG, "Existing cart item type: " + cartItem.getType());
            }
        }
    }

    private void handleError(String message) {
        Log.e(TAG, "Error: " + message);

        // Convert message to lowercase to make the check case-insensitive
        String lowerCaseMessage = message.toLowerCase();

        // Check if the message contains keywords related to wishlist not found
        if (lowerCaseMessage.contains("wishlist not found") ||
                lowerCaseMessage.contains("does not belong to user")) {

            // Perform a specific action (e.g., show a different message, hide wishlist UI, etc.)
            Toast.makeText(requireContext(), "No items in your wishlist.", Toast.LENGTH_SHORT).show();
             createWishlist();
            // You can add additional logic here, such as hiding a wishlist section
            // binding.wishlistLayout.setVisibility(View.GONE);
        } else {
            // Show the original error message for other cases
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }


    private void createWishlist(){
        String auth="Bearer "+pref.getPrefString(requireActivity(), pref.user_token);
        viewModel2.createWishList(auth).observe(getViewLifecycleOwner(),response->{
            if (response != null && response.isSuccess && response.data != null) {
                responsee = response.data;
                addToWishlist(String.valueOf(productIdd));
            } else {
                handleError(response != null ? response.message : "Failed to add item");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}