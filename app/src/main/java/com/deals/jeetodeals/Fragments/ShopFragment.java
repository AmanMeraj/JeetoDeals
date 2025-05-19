package com.deals.jeetodeals.Fragments;

import static android.content.ContentValues.TAG;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.deals.jeetodeals.ActivityCategory;
import com.deals.jeetodeals.Adapters.AdapterCard2;
import com.deals.jeetodeals.Adapters.CategoryAdapter;
import com.deals.jeetodeals.Adapters.HierarchicalCategoryAdapter;
import com.deals.jeetodeals.Adapters.SortOption;
import com.deals.jeetodeals.Adapters.SortOptionsAdapter;
import com.deals.jeetodeals.BottomSheet.BottomSheetPromotion;
import com.deals.jeetodeals.BottomSheet.ShopBottomSheetDialogFragment;
import com.deals.jeetodeals.BottomSheet.SortBottomSheetFragment;
import com.deals.jeetodeals.CategoryTreeHelper;
import com.deals.jeetodeals.ContainerActivity.ContainerActivity;
import com.deals.jeetodeals.Fragments.HomeFragment.HomeViewModel;
import com.deals.jeetodeals.Model.AddItems;
import com.deals.jeetodeals.Model.Attribute;
import com.deals.jeetodeals.Model.BannerResponse;
import com.deals.jeetodeals.Model.CartResponse;
import com.deals.jeetodeals.Model.Category;
import com.deals.jeetodeals.Model.CategoryTree;
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
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.Attributes;

public class ShopFragment extends Fragment implements AdapterCard2.OnItemClickListener,CategoryAdapter.OnCategoryClickListener{
    private static final String TAG = "ShopFragment";
    private static final int REQUEST_CATEGORY_SELECTION = 101;
    private FragmentShopBinding binding;
    private SortOptionsAdapter sortAdapter;
    private String currentOrder = null;
    private String currentOrderBy = null;
    private View selectedRelativeLayout;
    private CartResponse cartResponse;
    boolean isLoggedIn=false;
    private final SharedPref pref = new SharedPref();
    private final Utility utility = new Utility();
    private int selectedCategoryId = 0; // Default value, indicating no category selected
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

    private ActivityResultLauncher<Intent> categorySelectionLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            Log.d("JWD", ": m here new");
            // handle the result here
        }
    }
    );

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
        setupSort();
        setupCategory();
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
                        Log.d(TAG, "PAGINATION TRIGGERED - Loading page: " + currentPage);

                        // Check if we're in "All Categories" mode or a specific category
                        if (selectedCategoryId == -1 || selectedCategoryId == 0) {
                            // For "All Categories" mode
                            loadMoreProductsWithoutCategory();
                        } else {
                            // For specific category
                            fetchProductsByCategory(selectedCategoryId, false);
                        }
                    }
                }
            }
        });
    }
    // Add a method to show/hide a loader at the bottom of the list
    private boolean isBindingValid() {
        return binding != null && binding.loader != null && binding.loader.rlLoader != null;
    }

    // 6. Improve the showBottomLoader method to handle null bindings
    private void showBottomLoader(boolean show) {
        if (binding != null && binding.loaderBottom != null) {
            if (show) {
                Log.d(TAG, "Showing bottom loader for pagination");
                binding.loaderBottom.setVisibility(View.VISIBLE);
            } else {
                binding.loaderBottom.setVisibility(View.GONE);
            }
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

    @Override
    public void onItemClick(ShopResponse item) {
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
        // Log the selection
        Log.d("JWD", "onCategoryClick: Category=" + (category != null ? category.getName() : "null") +
                ", ID=" + categoryId);

        // Handle UI animations
        if (imageView != null) {
            animateCategorySelection(imageView);
        }

        RelativeLayout relativeLayout = null;
        if (imageView != null && imageView.getParent() instanceof RelativeLayout) {
            relativeLayout = (RelativeLayout) imageView.getParent();
            updateCategorySelection(relativeLayout);
        }

        // Update selected category ID
        selectedCategoryId = categoryId;
        isLastPage = false;
        isLoading = false;
        initialLoadComplete = false;
        currentPage = 1; // Reset pagination

        // Clear existing items
        if (shopItems != null) {
            shopItems.clear();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }

        // Show loading indicator
        if (binding != null && binding.loader != null && binding.loader.rlLoader != null) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
        }

        // Fetch appropriate products
        if (category != null && category.getName() != null && category.getName().equals(" All ")) {
            // "All" category selected
            fetchProductsWithoutCategory();
        } else if (categoryId > 0) {
            // Specific category selected
            fetchProductsByCategory(categoryId, true);
        } else {
            // Handle invalid category
            Log.e(TAG, "Invalid category selection");
            if (binding != null && binding.loader != null && binding.loader.rlLoader != null) {
                binding.loader.rlLoader.setVisibility(View.GONE);
            }
            Toast.makeText(requireActivity(), "Unable to load category", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Fetch products without filtering by category
     */
    private void fetchProductsWithoutCategory() {
        if (isLoading) {
            Log.d(TAG, "Already loading products, skipping fetch");
            return;
        }

        // Reset pagination state
        currentPage = 1;
        isLastPage = false;
        isLoading = true;
        initialLoadComplete = false;

        // Clear existing items
        shopItems.clear();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        // Show loader
        binding.loader.rlLoader.setVisibility(View.VISIBLE);
        showBottomLoader(false);

        // Additional debugging
        Log.d(TAG, "Fetching All Categories with order: " + currentOrder + ", orderBy: " + currentOrderBy);

        // Call the ViewModel method for fetching without category
        viewModel.getShopWithoutCategory("simple", currentPage, perPage,
                        currentOrder, currentOrderBy)
                .observe(getViewLifecycleOwner(), response -> {
                    isLoading = false;
                    binding.loader.rlLoader.setVisibility(View.GONE);
                    showBottomLoader(false);
                    initialLoadComplete = true;

                    if (response != null) {
                        // Log full response for debugging
                        Log.d(TAG, "API Response: " + response);

                        if (response.isSuccess && response.data != null) {
                            int itemsReceived = response.data.size();
                            Log.d(TAG, "Received " + itemsReceived + " items for page " + currentPage + " (All Categories)");

                            if (itemsReceived > 0) {
                                shopItems.addAll(response.data);
                                adapter.notifyDataSetChanged();

                                // Check if RecyclerView is empty after update
                                if (adapter.getItemCount() == 0) {
                                    Log.e(TAG, "Adapter item count is 0 after adding data");
                                    // Show empty state
                                    showEmptyState(true);
                                } else {
                                    showEmptyState(false);
                                }

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
                                // Show empty state
                                showEmptyState(true);
                            }
                        } else {
                            Log.e(TAG, "API error: " + (response != null ? response.message : "Unknown error"));
                            handleError(response != null ? response.message : "Unknown error");
                            // Show empty state
                            showEmptyState(true);
                        }
                    } else {
                        Log.e(TAG, "Null response received");
                        handleError("Network error. Please try again.");
                        showEmptyState(true);
                    }
                });
    }

    // Add this helper method if it doesn't exist already
    private void showEmptyState(boolean show) {
        if (binding.noItem != null) {
            binding.noItem.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        binding.rcItems.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    /**
     * Load more products for infinite scrolling when "All" category is selected
     */
    private void loadMoreProductsWithoutCategory() {
        if (isLoading || isLastPage) {
            Log.d(TAG, "Skipping fetch - isLoading: " + isLoading + ", isLastPage: " + isLastPage);
            return;
        }

        isLoading = true;
        showBottomLoader(true);

        // Call the ViewModel method for fetching without category
        viewModel.getShopWithoutCategory("simple", currentPage, perPage,
                        currentOrder, currentOrderBy)
                .observe(getViewLifecycleOwner(), response -> {
                    isLoading = false;
                    showBottomLoader(false);

                    if (response != null && response.isSuccess && response.data != null) {
                        int itemsReceived = response.data.size();
                        Log.d(TAG, "Received " + itemsReceived + " additional items for page " + currentPage + " (All Categories)");

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

    // Add this method to handle resetting and refetching products
    private void resetAndRefetchProducts() {
        // Reset pagination state
        currentPage = 1;
        isLastPage = false;
        isLoading = false;
        initialLoadComplete = false;

        // Clear existing items
        shopItems.clear();
        adapter.notifyDataSetChanged();

        // Show loader
        binding.loader.rlLoader.setVisibility(View.VISIBLE);

        // Based on current category selection, fetch appropriate data
        if (selectedCategoryId == 0) {
            // For "All" category
            fetchProductsWithoutCategory();
        } else {
            // For specific category
            fetchProductsWithSort();
        }
    }

    // Modify your fetchProductsByCategory method to use this new metho
    private void setupSort() {
        // Set up click listener for the sort button
        binding.sortByLayout.setOnClickListener(v -> {
            showSortBottomSheet();
        });
    }

    // 2. Add this new method to show the bottom sheet
    private void showSortBottomSheet() {
        SortBottomSheetFragment bottomSheet = SortBottomSheetFragment.newInstance();

        // If you're tracking the current sort option position
        int currentSortPosition = getCurrentSortPosition();
        bottomSheet.setSelectedPosition(currentSortPosition);

        bottomSheet.setSortOptionListener(new SortBottomSheetFragment.SortOptionListener() {
            @Override
            public void onSortOptionSelected(String orderBy, String order, String displayName, int position) {
                // Update sort parameters
                currentOrderBy = orderBy;
                currentOrder = order;

                // Store the selected position if needed
                saveSortPosition(position);

                // Update UI to show current sort option
                updateSortIndicator(displayName);

                // Reset and reload products with new sort
                resetAndRefetchProducts();
            }
        });

        bottomSheet.show(requireActivity().getSupportFragmentManager(), "SortBottomSheet");
    }

    // 3. Add these helper methods for managing sort state
    private int getCurrentSortPosition() {
        // Default to 0 (Default sorting)
        if (currentOrderBy == null || currentOrder == null) {
            return 0;
        }

        // Map current orderBy and order values to position
        if (currentOrderBy.equals("menu_order") && currentOrder.equals("asc")) {
            return 0; // Default sorting
        } else if (currentOrderBy.equals("popularity") && currentOrder.equals("desc")) {
            return 1; // Sort by popularity
        } else if (currentOrderBy.equals("rating") && currentOrder.equals("desc")) {
            return 2; // Sort by average rating
        } else if (currentOrderBy.equals("date") && currentOrder.equals("desc")) {
            return 3; // Sort by latest
        } else if (currentOrderBy.equals("price") && currentOrder.equals("asc")) {
            return 4; // Sort by price: low to high
        } else if (currentOrderBy.equals("price") && currentOrder.equals("desc")) {
            return 5; // Sort by price: high to low
        }

        return 0; // Default
    }

    private void saveSortPosition(int position) {
        // You could store this in SharedPreferences if needed
        // This is optional - just keeps track of current position
    }

    private void updateSortIndicator(String displayName) {
        // Update the sort text in the toolbar if needed
        TextView sortText = binding.sort;
        if (sortText != null) {
            sortText.setText("Sort: " + displayName);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        Log.d("JWD", "I AM HERE: ");
        if (1==1) {
            return;
        }

        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_CATEGORY_SELECTION && resultCode == ActivityCategory.RESULT_CATEGORY_SELECTED && data != null) {
            int categoryId = data.getIntExtra(ActivityCategory.EXTRA_CATEGORY_ID, -1);
            String categoryName = data.getStringExtra(ActivityCategory.EXTRA_CATEGORY_NAME);

            Log.d(TAG, "Category selected from ActivityCategory: " + categoryName + ", ID: " + categoryId);

            // Update the UI to show the selected category
            if (binding != null && binding.category != null) {
                binding.category.setText(categoryName != null ? categoryName : "All Categories");
            }

            // Update the selected category and reset pagination
            selectedCategoryId = categoryId;
            isLastPage = false;
            isLoading = false;
            initialLoadComplete = false;
            currentPage = 1; // Reset page to 1

            // Clear existing items to prepare for new data
            shopItems.clear();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }

            // Show loader before fetch
            if (binding != null && binding.loader != null && binding.loader.rlLoader != null) {
                binding.loader.rlLoader.setVisibility(View.VISIBLE);
            }

            // Use different fetch methods based on category selection
            if (categoryId <= 0) {
                // If "All" category is selected or invalid category
                fetchProductsWithoutCategory();
            } else {
                // For other categories
                fetchProductsByCategory(categoryId, true);
            }
        }
    }

    // Enhanced fetchProductsByCategory method with better error handling
    private void fetchProductsByCategory(int id, boolean isInitialLoad) {

        if (id <= 0) {
            Log.e(TAG, "Invalid category ID: " + id);
            if (binding != null && binding.loader != null && binding.loader.rlLoader != null) {
                binding.loader.rlLoader.setVisibility(View.GONE);
            }
            return;
        }

        selectedCategoryId = id;
        Log.d(TAG, "fetchProductsByCategory: ID=" + id + ", isInitialLoad=" + isInitialLoad);

        if (isInitialLoad) {
            currentPage = 1;
            isLastPage = false;
            isLoading = true; // Set to true to prevent duplicate calls
            initialLoadComplete = false;

            if (shopItems != null) {
                shopItems.clear();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        }

        if (utility != null && utility.isInternetConnected(requireActivity())) {
            fetchProductsWithSort();
        } else {
            isLoading = false;
            if (binding != null && binding.loader != null && binding.loader.rlLoader != null) {
                binding.loader.rlLoader.setVisibility(View.GONE);
            }
            Toast.makeText(requireActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    // Modified fetchProductsWithSort with improved error handling and logging
    private void fetchProductsWithSort() {
        if (!isAdded()) {
            Log.d(TAG, "Fragment not attached, skipping fetch");
            isLoading = false;
            return;
        }

        if (isLoading && currentPage > 1 && isLastPage) {
            Log.d(TAG, "Skipping fetch - isLoading: " + isLoading + ", isLastPage: " + isLastPage);
            return;
        }

        isLoading = true;
        Log.d(TAG, "Starting fetch for category ID: " + selectedCategoryId + ", page: " + currentPage);

        if (currentPage == 1) {
            if (binding != null && binding.loader != null && binding.loader.rlLoader != null) {
                binding.loader.rlLoader.setVisibility(View.VISIBLE);
            }
            showBottomLoader(false);
            initialLoadComplete = false;
        } else {
            showBottomLoader(true);
        }

        String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);

        // Use the ViewModel's method with sort parameters
        viewModel.getShop("simple|variable", selectedCategoryId, currentPage, perPage,
                        currentOrder, currentOrderBy)
                .observe(getViewLifecycleOwner(), response -> {
                    if (!isAdded()) return; // Check if fragment is still attached

                    isLoading = false;
                    if (binding != null && binding.loader != null && binding.loader.rlLoader != null) {
                        binding.loader.rlLoader.setVisibility(View.GONE);
                    }
                    showBottomLoader(false);

                    if (currentPage == 1) {
                        initialLoadComplete = true;
                    }

                    if (response != null && response.isSuccess && response.data != null) {
                        int itemsReceived = response.data.size();
                        Log.d(TAG, "Received " + itemsReceived + " items for categoryID: " + selectedCategoryId + ", page: " + currentPage);

                        if (itemsReceived > 0) {
                            if (shopItems != null) {
                                int startPosition = shopItems.size();
                                shopItems.addAll(response.data);

                                if (adapter != null) {
                                    if (currentPage == 1) {
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        adapter.notifyItemRangeInserted(startPosition, itemsReceived);
                                    }
                                }
                            }

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

                            // Show a message if no products are found in this category
                            if (currentPage == 1 && (shopItems == null || shopItems.isEmpty())) {
                                Toast.makeText(requireActivity(), "No products found in this category", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        String errorMsg = (response != null ? response.message : "Unknown error");
                        Log.e(TAG, "API error: " + errorMsg);
                        handleError(errorMsg);
                    }
                });
    }

    // Add a null check in setupCategory to avoid potential NPE
    private void setupCategory() {
        // Set up click listener for the category layout
        if (binding != null && binding.categoryLayout != null) {
            binding.categoryLayout.setOnClickListener(v -> {


                Intent intent = new Intent(requireActivity(), ActivityCategory.class);
                categorySelectionLauncher.launch(intent);
               // startActivityForResult(intent, REQUEST_CATEGORY_SELECTION);
            });
        }
    }

    // Fix the getCategory method to handle null binding
    private void getCategory() {
        if (binding == null) {
            Log.e(TAG, "getCategory: binding is null");
            return;
        }

        // Set default text if no category is selected
        if (binding.category != null) {
            if (selectedCategoryId == -1 || selectedCategoryId == 0) {
                binding.category.setText("All Categories");
            }
        }

        // Fetch products based on current category selection
        if (selectedCategoryId == -1 || selectedCategoryId == 0) {
            fetchProductsWithoutCategory();
        } else {
            fetchProductsByCategory(selectedCategoryId, true);
        }
    }

}