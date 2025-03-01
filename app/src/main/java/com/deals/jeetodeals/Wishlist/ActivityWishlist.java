package com.deals.jeetodeals.Wishlist;

import static android.app.PendingIntent.getActivity;
import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.deals.jeetodeals.Adapters.WishlistAdapter;
import com.deals.jeetodeals.BottomSheet.ShopBottomSheetDialogFragment;
import com.deals.jeetodeals.ContainerActivity.ContainerActivity;
import com.deals.jeetodeals.Fragments.FragmentsRepository;
import com.deals.jeetodeals.Fragments.FragmentsViewModel;
import com.deals.jeetodeals.Model.AddItems;
import com.deals.jeetodeals.Model.CartResponse;
import com.deals.jeetodeals.Model.Items;
import com.deals.jeetodeals.Model.ShopResponse;
import com.deals.jeetodeals.Model.Wishlist;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.databinding.ActivityWishlistBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ActivityWishlist extends Utility {
    private ActivityWishlistBinding binding;
    private WishlistAdapter adapter;
    private WishlistViewModel viewModel;
    private FragmentsViewModel fragmentsViewModel;
    private CartResponse cartResponse;
    ShopResponse shopResponse;
    private final AtomicBoolean isAddToCartCooldown = new AtomicBoolean(false);
    private final AtomicBoolean isLoadingCart = new AtomicBoolean(false);
    private final Handler handler = new Handler();
    private ArrayList<WishlistResponse> responses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWishlistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(WishlistViewModel.class);
        fragmentsViewModel = new ViewModelProvider(this).get(FragmentsViewModel.class);

        binding.backBtn.setOnClickListener(view -> finish());

        if (isInternetConnected(this)) {
            // Generate a fresh nonce at application start
            refreshNonce();
            getWishlist();
            getCart();
        } else {
            showToast("No Internet connection");
        }
    }

    private void refreshNonce() {
        // Generate a fresh nonce and save it
        String freshNonce = FragmentsRepository.getNonce();
        pref.setPrefString(this, pref.nonce, freshNonce);
        Log.d("NONCE_REFRESH", "New nonce generated: " + freshNonce);
    }

    private void getWishlist() {
        binding.loader.rlLoader.setVisibility(View.VISIBLE);
        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);
        viewModel.getWishlist(auth).observe(this, response -> {
            binding.loader.rlLoader.setVisibility(View.GONE);
            if (response != null && response.isSuccess && response.data != null) {
                responses = response.data;
                Log.d("WishlistActivity", "Wishlist Size: " + responses.size());
                setupRecyclerView(responses);
            } else {
                assert response != null;
                showToast(response.message);
            }
        });
    }

    private void setupRecyclerView(ArrayList<WishlistResponse> responses) {
        if (responses == null || responses.isEmpty()) {
            // Hide RecyclerView and Show "No Items" View
            binding.rcWishlist.setVisibility(View.GONE);
            binding.noItem.setVisibility(View.VISIBLE);  // Ensure this view exists in XML
        } else {
            // Show RecyclerView and Hide "No Items" View
            binding.rcWishlist.setVisibility(View.VISIBLE);
            binding.noItem.setVisibility(View.GONE);

            adapter = new WishlistAdapter(this, responses, new WishlistAdapter.OnWishlistItemClickListener() {
                @Override
                public void onDeleteClick(WishlistResponse item, int position) {
                    deleteWishlistItem(item, position);
                    responses.remove(position); // Remove item from list
                    adapter.notifyItemRemoved(position);
                    if (responses.isEmpty()) {
                        // Hide RecyclerView and show "No Items" view
                        binding.rcWishlist.setVisibility(View.GONE);
                        binding.noItem.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAddToCartClick(WishlistResponse item, int position) {
                    if (isAddToCartCooldown.get()) return;
                    isAddToCartCooldown.set(true);
                    binding.loader.rlLoader.setVisibility(View.VISIBLE);
                    handler.postDelayed(() -> isAddToCartCooldown.set(false), 1000);

                    // Refresh nonce before getting shop details
                    refreshNonce();
                    getShopWishlist(item);
                }
            });

            binding.rcWishlist.setAdapter(adapter);
        }
    }


    private void getShopWishlist(WishlistResponse item) {
        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);

        viewModel.getShopWishlist(item.getProduct_id()).observe(this, response -> {
            if (response != null && response.isSuccess) {
                shopResponse = response.data;

                // Refresh nonce before opening bottom sheet
                refreshNonce();
                String currentNonce = pref.getPrefString(this, pref.nonce);

                if (shopResponse.getType().equals("simple")) {
                    ShopBottomSheetDialogFragment bottomSheet = ShopBottomSheetDialogFragment.newInstance(shopResponse);

                    bottomSheet.setAddToCartListener(new ShopBottomSheetDialogFragment.AddToCartListener() {
                        @Override
                        public void onCartItemAdded(ShopResponse item, String size, int quantity) {
                            // Refresh nonce again right before adding to cart
                            refreshNonce();
                            checkCartAndAddItem(item, auth, pref.getPrefString(ActivityWishlist.this, pref.nonce), null, quantity);
                        }
                    });

                    bottomSheet.show(getSupportFragmentManager(), "ProductBottomSheet");
                } else {
                    // Open bottom sheet for variable products
                    ShopBottomSheetDialogFragment bottomSheet = ShopBottomSheetDialogFragment.newInstance(shopResponse);

                    bottomSheet.setAddToCartListener(new ShopBottomSheetDialogFragment.AddToCartListener() {
                        @Override
                        public void onCartItemAdded(ShopResponse item, String size, int quantity) {
                            // Refresh nonce again right before adding to cart
                            refreshNonce();
                            checkCartAndAddItem(item, auth, pref.getPrefString(ActivityWishlist.this, pref.nonce), size, quantity);
                        }
                    });

                    bottomSheet.show(getSupportFragmentManager(), "ProductBottomSheet");
                }
            } else {
                binding.loader.rlLoader.setVisibility(View.GONE);
                assert response != null;
                showToast(response.message);
            }
        });
    }

    private void deleteWishlistItem(WishlistResponse item, int position) {
        if (item == null || item.product_id <= 0) {
            showToast("Invalid product ID");
            return;
        }

        // Refresh nonce before deletion
        refreshNonce();

        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);
        Wishlist wishlist = new Wishlist();
        wishlist.setProduct_id(item.getProduct_id());

        viewModel.deleteWishlist(auth, wishlist).observe(this, response -> {
            if (response != null && response.isSuccess) {
                adapter.removeItem(position);
                showToast(response.message);
            } else {
                assert response != null;
                showToast(response.message);
            }
        });
    }

    private void checkCartAndAddItem(ShopResponse item, String authToken, String nonce, String size, int quantity) {
        if (binding != null) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
        }

        // Refresh nonce before getting cart
        refreshNonce();
        String currentNonce = pref.getPrefString(this, pref.nonce);

        fragmentsViewModel.getCart(authToken).observe(this, response -> {
            if (response != null && response.isSuccess && response.data != null) {
                binding.loader.rlLoader.setVisibility(View.GONE);
                CartResponse currentCart = response.data;
                logCartState(item, currentCart);

                if (currentCart.getItems() == null || currentCart.getItems().isEmpty()) {
                    // Cart is empty, can add directly
                    addItemToCart(item, authToken, currentNonce, size, quantity);
                } else {
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
                        // Type conflict, show dialog
                        showCartClearDialog(item, authToken, currentNonce, size, quantity);
                    } else {
                        // No conflict, can add directly
                        addItemToCart(item, authToken, currentNonce, size, quantity);
                    }

                    if (currentCart.getItems() != null) {
                        int cartCount = currentCart.getItems_count();
                        pref.setPrefInteger(this, pref.cart_count, cartCount);
                    }
                }
            } else {
                binding.loader.rlLoader.setVisibility(View.GONE);
                handleError(response != null ? response.message : "Failed to fetch cart");
            }
        });
    }

    private void logCartState(ShopResponse newItem, CartResponse cart) {
        Log.d("CART_DEBUG", "New item type: " + newItem.getType());
        if (cart.getItems() != null) {
            Log.d("CART_DEBUG", "Cart has " + cart.getItems().size() + " items");
            for (Items item : cart.getItems()) {
                Log.d("CART_DEBUG", "Cart item: " + item.getName() + ", type: " + item.getType());
            }
        }
    }

    private void showCartClearDialog(ShopResponse item, String authToken, String nonce, String size, int quantity) {
        new MaterialAlertDialogBuilder(ActivityWishlist.this)
                .setTitle("Cart Conflict")
                .setMessage("Your cart contains items of a different type. Adding this item will clear your cart. Do you want to proceed?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    // Refresh nonce before adding to cart
                    refreshNonce();
                    addItemToCart(item, authToken, pref.getPrefString(this, pref.nonce), size, quantity);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void addItemToCart(ShopResponse item, String authToken, String nonce, String selectedSize, int quantity) {
        if (binding != null) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
        }

        // Always use a fresh nonce for the add to cart operation
        refreshNonce();
        String currentNonce = pref.getPrefString(this, pref.nonce);
        Log.d("ADD_TO_CART", "Using nonce: " + currentNonce);

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

        // Send request with the fresh nonce
        fragmentsViewModel.AddToCart(authToken, currentNonce, addItems)
                .observe(this, response -> {
                    binding.loader.rlLoader.setVisibility(View.GONE);

                    if (response != null && response.isSuccess && response.data != null) {
                        Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show();
                        // Refresh nonce after successful cart addition
                        refreshNonce();
                        getCart();
                    } else {
                        if (response != null && response.message != null &&
                                response.message.toLowerCase().contains("invalid nonce")) {
                            Log.e(TAG, "Invalid nonce detected. Retrying with fresh nonce...");
                            // If invalid nonce, retry once with a fresh nonce
                            refreshNonce();
                            String retryNonce = pref.getPrefString(this, pref.nonce);
                            retryAddToCart(item, authToken, retryNonce, selectedSize, quantity);
                        } else {
                            handleError(response != null ? response.message : "Failed to add item");
                        }
                    }
                });
    }

    private void retryAddToCart(ShopResponse item, String authToken, String nonce, String selectedSize, int quantity) {
        if (binding != null) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
        }

        AddItems addItems = new AddItems();
        addItems.setId(item.getId());
        addItems.setQuantity(quantity);

        boolean isVariable = "variable".equalsIgnoreCase(item.getType());

        if (isVariable && selectedSize != null && !selectedSize.isEmpty()) {
            List<Map<String, String>> variationList = new ArrayList<>();
            Map<String, String> variationMap = new HashMap<>();
            variationMap.put("attribute", "Size");
            variationMap.put("value", selectedSize);
            variationList.add(variationMap);

            addItems.setVariation(variationList);
        }

        Log.d("RETRY_ADD_TO_CART", "Retrying with nonce: " + nonce);

        // Retry with new nonce
        fragmentsViewModel.AddToCart(authToken, nonce, addItems)
                .observe(this, response -> {
                    binding.loader.rlLoader.setVisibility(View.GONE);

                    if (response != null && response.isSuccess && response.data != null) {
                        Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show();
                        getCart();
                    } else {
                        handleError(response != null ? response.message : "Failed to add item after retry");
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void getCart() {
        // Show loader when API call starts
        if (binding != null) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
        }

        isLoadingCart.set(true);
        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);

        // Refresh nonce before getting cart
        refreshNonce();

        fragmentsViewModel.getCart(auth).observe(this, response -> {
            isLoadingCart.set(false);
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response != null) {
                if (response.isSuccess && response.data != null) {
                    cartResponse = response.data;

                    // Always refresh nonce after cart update
                    refreshNonce();

                    // Update cart badge count
                    if (cartResponse.getItems() != null) {
                        int cartCount = cartResponse.getItems_count();
                        pref.setPrefInteger(this, pref.cart_count, cartCount);
                    }
                } else {
                    Log.w(TAG, "Cart retrieval failed: " + response.message);
                }
            } else {
                showToast("Something went wrong!");
            }
        });
    }

    private void handleError(String message) {
        Log.e(TAG, "Error: " + message);

        // Convert message to lowercase to make the check case-insensitive
        String lowerCaseMessage = message.toLowerCase();

        // Check if the message contains keywords related to wishlist not found
        if (lowerCaseMessage.contains("wishlist not found") ||
                lowerCaseMessage.contains("does not belong to user")) {
            // Perform a specific action (e.g., show a different message, hide wishlist UI, etc.)
            Toast.makeText(this, "No items in your wishlist.", Toast.LENGTH_SHORT).show();
        } else if (lowerCaseMessage.contains("invalid nonce")) {
            Toast.makeText(this, "Session expired. Please try again.", Toast.LENGTH_SHORT).show();
            // Refresh nonce when invalid nonce is detected
            refreshNonce();
        } else {
            // Show the original error message for other cases
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}