package com.deals.jeetodeals.Fragments;

import static android.content.ContentValues.TAG;

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
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.deals.jeetodeals.Adapters.AdapterCard2;
import com.deals.jeetodeals.Adapters.CategoryAdapter;
import com.deals.jeetodeals.BottomSheet.ShopBottomSheetDialogFragment;
import com.deals.jeetodeals.ContainerActivity.ContainerActivity;
import com.deals.jeetodeals.Fragments.HomeFragment.HomeViewModel;
import com.deals.jeetodeals.Model.AddItems;
import com.deals.jeetodeals.Model.Attribute;
import com.deals.jeetodeals.Model.BannerResponse;
import com.deals.jeetodeals.Model.CartResponse;
import com.deals.jeetodeals.Model.Category;
import com.deals.jeetodeals.Model.Items;
import com.deals.jeetodeals.Model.ShopResponse;
import com.deals.jeetodeals.Model.Variations;
import com.deals.jeetodeals.Model.Wishlist;
import com.deals.jeetodeals.Model.WishlistCreationResponse;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.SignInScreen.SignInActivity;
import com.deals.jeetodeals.Utils.SharedPref;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.Wishlist.WishlistAddResponse;
import com.deals.jeetodeals.Wishlist.WishlistViewModel;
import com.deals.jeetodeals.databinding.FragmentShopBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.Attributes;

public class ShopFragment extends Fragment implements AdapterCard2.OnItemClickListener,CategoryAdapter.OnCategoryClickListener {
    private static final String TAG = "ShopFragment";

    private FragmentShopBinding binding;
    private View selectedRelativeLayout;
    private CartResponse cartResponse;
    boolean isLoggedIn=false;
    private final SharedPref pref = new SharedPref();
    private final Utility utility = new Utility();
    private int selectedCategoryId = -1; // Default value, indicating no category selected
    private List<ShopResponse> shopItems = new ArrayList<>();
    private Handler handler = new Handler();
    CategoryAdapter categoryAdapter;
    private BannerResponse bannerResponse;
    private ArrayList<Category>categories;
    private HomeViewModel viewModel;
    private FragmentsViewModel viewModel2;
    private boolean isPlaying = false;
    private WishlistViewModel wishlistViewModel;
    private WishlistAddResponse addResponse;
    private int currentPage = 1;
    private int productIdd;
    private int perPage = 20; // Initial value, will be increased by 10 each time
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private boolean initialLoadComplete = false;
    private AdapterCard2 adapter;
    private WishlistCreationResponse responsee;
    private AtomicBoolean isLoadingCart = new AtomicBoolean(false);

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
            getCategory();
        } else {
            Toast.makeText(requireActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCategory() {
        viewModel2.getCategory().observe(getViewLifecycleOwner(), response -> {
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
                    fetchProductsByCategory(firstCategory.getId(), true);

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
        // Use the parameter data instead of referencing categories field
        categories = data; // Assuming you have a class member variable called categories

        // Create adapter with the correct data
        categoryAdapter = new CategoryAdapter(categories, this, requireContext());
        binding.rcCategory.setAdapter(categoryAdapter);
        binding.rcCategory.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        // Pre-select the first item and update its visual state
        if (!categories.isEmpty()) {
            // Set the first category as selected in the adapter
            categoryAdapter.setSelectedPosition(0); // This will handle the visual selection

            // Set the selected category ID and fetch products
            selectedCategoryId = categories.get(0).getId();
            fetchProductsByCategory(selectedCategoryId, true);

            // Optional: store the adapter as a class member if you need to access it later
        }
    }

    private void fetchProductsByCategory(int id, boolean isInitialLoad) {
        if (isLoading || (isLastPage && !isInitialLoad)) {
            Log.d(TAG, "Skipping fetch - isLoading: " + isLoading + ", isLastPage: " + isLastPage);
            return;
        }

        isLoading = true;

        if (isInitialLoad) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
            showBottomLoader(false);
            initialLoadComplete = false;
        } else {
            showBottomLoader(true);
        }

        String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);

        if (isInitialLoad) {
            currentPage = 1; // Reset to page 1 for initial load
            shopItems.clear();
            adapter.notifyDataSetChanged();
            isLastPage = false;
        }

        viewModel.getShop( "simple|variable", id, currentPage, perPage).observe(getViewLifecycleOwner(), response -> {
            isLoading = false;
            binding.loader.rlLoader.setVisibility(View.GONE);
            showBottomLoader(false);

            if (isInitialLoad) {
                initialLoadComplete = true;
                Log.d(TAG, "Initial load complete - Setting initialLoadComplete=true");
            }

            if (response != null && response.isSuccess && response.data != null) {
                int itemsReceived = response.data.size();
                Log.d(TAG, "Received " + itemsReceived + " items for page " + currentPage);

                if (itemsReceived > 0) {
                    int startPosition = shopItems.size();
                    shopItems.addAll(response.data);
                    adapter.notifyItemRangeInserted(startPosition, itemsReceived);

                    if (itemsReceived < perPage) {
                        isLastPage = true;
                        Log.d(TAG, "Setting isLastPage=true - Fewer items than requested");
                    } else {
                        currentPage++;
                        Log.d(TAG, "Incremented currentPage to: " + currentPage);
                    }
                } else {
                    isLastPage = true;
                    Log.d(TAG, "Setting isLastPage=true - Empty response");
                }
            } else {
                Log.e(TAG, "API error: " + (response != null ? response.message : "Unknown error"));
                handleError(response != null ? response.message : "Unknown error");
            }
        });
    }
    private void setupScrollListener() {
        binding.rcItems.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled called - dx: " + dx + ", dy: " + dy);

                if (dy <= 0) {
                    Log.d(TAG, "Scrolling up or no vertical scroll - skipping pagination");
                    return;
                }

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) {
                    Log.e(TAG, "LayoutManager is null - cannot check scroll position");
                    return;
                }

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                Log.d(TAG, "Scroll positions - visible items: " + visibleItemCount +
                        ", total items: " + totalItemCount +
                        ", last visible: " + lastVisibleItemPosition);

                if (!isLoading && !isLastPage && initialLoadComplete) {
                    if ((lastVisibleItemPosition + 5) >= totalItemCount) {
                        Log.d(TAG, "PAGINATION TRIGGERED - Loading page: " + currentPage +
                                ", selectedCategoryId: " + selectedCategoryId);
                        fetchProductsByCategory(selectedCategoryId, false);
                    }
                }
            }
        });
    }
    // Add a method to show/hide a loader at the bottom of the list
    private void showBottomLoader(boolean show) {
        if (show) {
            Log.d(TAG, "Showing bottom loader for pagination");
            binding.loaderBottom.setVisibility(View.VISIBLE);
        } else {
            binding.loaderBottom.setVisibility(View.GONE);
        }
    }


    private void updateCategorySelection(RelativeLayout newSelection) {
        if (selectedRelativeLayout != null && selectedRelativeLayout != newSelection) {
            selectedRelativeLayout.setBackgroundResource(R.drawable.category_bg);
        }
        newSelection.setBackgroundResource(R.drawable.selected_category_bg);
        selectedRelativeLayout = newSelection;
    }

    private void animateCategorySelection(ImageView imageView) {
        if (imageView == null) {
            Log.e("ShopFragment", "View is null, cannot animate");
            return; // Prevent crash
        }
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
        isLoggedIn = pref.getPrefBoolean(requireContext(), pref.login_status);

        if (!isLoggedIn) {
            // User is not logged in, redirect to login screen
            Intent intent = new Intent(requireActivity(), SignInActivity.class);
            startActivity(intent);
            return;
        }

        String auth = getAuthToken();
        Wishlist wishlist = new Wishlist();
        wishlist.setProduct_id(Integer.parseInt(productId));
        Log.d(TAG, "addToWishlist: "+wishlist.getProduct_id() +" "+auth);

        wishlistViewModel.addWishlist(auth, wishlist).observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.isSuccess && response.data != null) {
                binding.loader.rlLoader.setVisibility(View.GONE);
                addResponse = response.data;
                Toast.makeText(requireActivity(), response.message, Toast.LENGTH_SHORT).show();
            } else {
                binding.loader.rlLoader.setVisibility(View.GONE);
                handleError(response != null ? response.message : "Unknown error");
            }
        });
    }

    @Override
    public void onShareClick(String permalink) {

        isLoggedIn = pref.getPrefBoolean(requireContext(), pref.login_status);

        if (!isLoggedIn) {
            // User is not logged in, redirect to login screen
            Intent intent = new Intent(requireActivity(), SignInActivity.class);
            startActivity(intent);
            return;
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, permalink);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    @Override
    public void onAddToCartClick(ShopResponse item) {
        String authToken = getAuthToken();
        String nonce = pref.getPrefString(requireActivity(), pref.nonce);

        if (item.getType().equals("simple")) {
            ShopBottomSheetDialogFragment bottomSheet = ShopBottomSheetDialogFragment.newInstance(item);

            bottomSheet.setAddToCartListener(new ShopBottomSheetDialogFragment.AddToCartListener() {
                @Override
                public void onCartItemAdded(ShopResponse item, String size, int quantity) {
                    checkCartAndAddItem(item, authToken, nonce, null, quantity);
                }
            });

            bottomSheet.show(requireActivity().getSupportFragmentManager(), "ProductBottomSheet");
            // Directly add to cart without opening the bottom sheet

        } else {
            // Open bottom sheet for variable products
            ShopBottomSheetDialogFragment bottomSheet = ShopBottomSheetDialogFragment.newInstance(item);

            bottomSheet.setAddToCartListener(new ShopBottomSheetDialogFragment.AddToCartListener() {
                @Override
                public void onCartItemAdded(ShopResponse item, String size, int quantity) {
                    checkCartAndAddItem(item, authToken, nonce, size, quantity);
                }
            });

            bottomSheet.show(requireActivity().getSupportFragmentManager(), "ProductBottomSheet");
        }
    }

    private void checkCartAndAddItem(ShopResponse item, String authToken, String nonce, String size, int quantity) {
        if (binding != null) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
        }

        isLoggedIn = pref.getPrefBoolean(requireContext(), pref.login_status);

        if (!isLoggedIn) {
            // User is not logged in, redirect to login screen
            Intent intent = new Intent(requireActivity(), SignInActivity.class);
            startActivity(intent);
            return;
        }
        viewModel2.getCart(authToken).observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.isSuccess && response.data != null) {
                binding.loader.rlLoader.setVisibility(View.GONE);
                CartResponse currentCart = response.data;
                logCartState(item, currentCart);

                if (currentCart.getItems() == null || currentCart.getItems().isEmpty()) {
                    binding.loader.rlLoader.setVisibility(View.GONE);
                    // Cart is empty, can add directly
                    addItemToCart(item, authToken, nonce, size, quantity);
                } else {
                    binding.loader.rlLoader.setVisibility(View.GONE);
                    // Check if current items are lottery type and new item is not lottery
                    boolean isCartLottery = false;
                    boolean isCartSimpleOrVariable = false;

                    // Determine what's in the cart currently
                    for (Items cartItem : currentCart.getItems()) {
                        String cartItemType = cartItem.getType().toLowerCase();
                        if (cartItemType.equals("lottery")) {
                            isCartLottery = true;
                        } else if (cartItemType.equals("simple") || cartItemType.equals("variable")) {
                            isCartSimpleOrVariable = true;
                        }
                    }

                    // Is the new item a lottery?
                    boolean isNewItemLottery = "lottery".equalsIgnoreCase(item.getType());

                    // Check if there's a type conflict (lottery vs simple/variable)
                    if ((isCartLottery && !isNewItemLottery) || (isCartSimpleOrVariable && isNewItemLottery)) {
                        binding.loader.rlLoader.setVisibility(View.GONE);
                        // Type conflict, show dialog
                        showCartClearDialog(item, authToken, nonce, size, quantity);
                    } else {
                        binding.loader.rlLoader.setVisibility(View.GONE);
                        // No conflict, can add directly
                        addItemToCart(item, authToken, nonce, size, quantity);
                    }
                    if (currentCart.getItems() != null) {
                        int cartCount = currentCart.getItems_count();
                        pref.setPrefInteger(requireActivity(), pref.cart_count, cartCount);

                        // Update badge in ContainerActivity
                        if (getActivity() instanceof ContainerActivity) {
                            ((ContainerActivity) getActivity()).updateCartBadge(cartCount);
                        }
                    }
                }
            } else {
                binding.loader.rlLoader.setVisibility(View.GONE);
                handleError(response != null ? response.message : "Failed to fetch cart");
            }
        });
    }

    private void showCartClearDialog(ShopResponse item, String authToken, String nonce, String size, int quantity) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cart Conflict")
                .setMessage("Your cart contains items of a different type. Adding this item will clear your cart. Do you want to proceed?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    addItemToCart(item, authToken, nonce, size, quantity);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void addItemToCart(ShopResponse item, String authToken, String nonce, String selectedSize, int quantity) {
        if (binding != null) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
        }
        AddItems addItems = new AddItems();
        addItems.setId(item.getId());
        addItems.setQuantity(quantity);

        boolean isVariable = "variable".equalsIgnoreCase(item.getType());

        if (isVariable && selectedSize != null && !selectedSize.isEmpty()) {
            // Create the variation structure in the format the API expects
            List<Map<String, String>> variationList = new ArrayList<>();
            Map<String, String> variationMap = new HashMap<>();
            variationMap.put("attribute", "Size");
            variationMap.put("value", selectedSize);
            variationList.add(variationMap);

            addItems.setVariation(variationList);
        }

        // Send request
        viewModel2.AddToCart(authToken, nonce, addItems)
                .observe(getViewLifecycleOwner(), response -> {
                    if (response != null && response.isSuccess && response.data != null) {
                        binding.loader.rlLoader.setVisibility(View.GONE);
                        Toast.makeText(requireActivity(), "Item added successfully", Toast.LENGTH_SHORT).show();
                        getCart();
                    } else {
                        binding.loader.rlLoader.setVisibility(View.GONE);
                        handleError(response != null ? response.message : "Failed to add item");
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        getCategory();
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
        if (binding != null) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
        }
        String auth="Bearer "+pref.getPrefString(requireActivity(), pref.user_token);
        viewModel2.createWishList(auth).observe(getViewLifecycleOwner(),response->{
            if (response != null && response.isSuccess && response.data != null) {
                binding.loader.rlLoader.setVisibility(View.GONE);
                responsee = response.data;
                addToWishlist(String.valueOf(productIdd));
            } else {
                binding.loader.rlLoader.setVisibility(View.GONE);
                handleError(response != null ? response.message : "Failed to add item");
            }
        });
    }
    public void getCart() {
        if (isLoadingCart.get() || !isAdded()) return;

        isLoadingCart.set(true);
        if (binding != null) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
        }
        String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);

        viewModel2.getCart(auth).observe(getViewLifecycleOwner(), response -> {
            isLoadingCart.set(false);

            if (!isAdded()) return;

            if (response != null) {
                if (response.isSuccess && response.data != null) {
                    binding.loader.rlLoader.setVisibility(View.GONE);
                    cartResponse = response.data;
                    String nonce = FragmentsRepository.getNonce();
                    pref.setPrefString(requireActivity(), pref.nonce, nonce);
                    Log.d("GET CART NONCE", "getCart: " + nonce);

                    // Update cart badge count
                    if (cartResponse.getItems() != null) {
                        int cartCount = cartResponse.getItems_count();
                        pref.setPrefInteger(requireActivity(), pref.cart_count, cartCount);

                        // Update badge in ContainerActivity
                        if (getActivity() instanceof ContainerActivity) {
                            ((ContainerActivity) getActivity()).updateCartBadge(cartCount);
                        }
                    }
                } else {
                    binding.loader.rlLoader.setVisibility(View.GONE);
                    Log.w(TAG, "Cart retrieval failed: " + response.message);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCategoryClick(Category category, ImageView imageView, int categoryId) {
        // Your click handling logic here
        if (imageView != null) {
            animateCategorySelection(imageView);
        }

        selectedCategoryId = categoryId;
        isLastPage = false;
        isLoading = false;
        initialLoadComplete = false; // Reset this flag too

        fetchProductsByCategory(categoryId, true);
    }
}