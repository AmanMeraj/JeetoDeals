package com.deals.jeetodeals.Fragments.HomeFragment;

import static android.content.ContentValues.TAG;

import static androidx.core.util.TypedValueCompat.dpToPx;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.deals.jeetodeals.Adapters.AdapterDraw;
import com.deals.jeetodeals.Adapters.AdapterPromotion1;
import com.deals.jeetodeals.Adapters.AdapterPromotion2;
import com.deals.jeetodeals.Adapters.AdapterWinner;
import com.deals.jeetodeals.Adapters.ImagePagerAdapter;
import com.deals.jeetodeals.BottomSheet.BottomSheetPromotion;
import com.deals.jeetodeals.ContainerActivity.ContainerActivity;
import com.deals.jeetodeals.Fragments.FragmentsRepository;
import com.deals.jeetodeals.Fragments.FragmentsViewModel;
import com.deals.jeetodeals.Model.AddItems;
import com.deals.jeetodeals.Model.BannerResponse;
import com.deals.jeetodeals.Model.CartResponse;
import com.deals.jeetodeals.Model.DrawResponse;
import com.deals.jeetodeals.Model.FcmResponse;
import com.deals.jeetodeals.Model.Items;
import com.deals.jeetodeals.Model.User;
import com.deals.jeetodeals.Model.WinnerResponse;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.SignInScreen.SignInActivity;
import com.deals.jeetodeals.Utils.SharedPref;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.databinding.FragmentHomeBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class HomeFragment extends Fragment implements AdapterPromotion1.OnItemClickListener, AdapterPromotion2.OnItemClickListener2, BottomSheetPromotion.AddToCartListener ,AdapterDraw.OnButtonClickListner{
    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private FragmentsViewModel fragmentsViewModel;
    ArrayList<DrawResponse> drawResponse;
    boolean isLoggedIn=false;
    private ArrayList<HomeResponse> responsee;
    private ArrayList<WinnerResponse> winnerResponse;
    private BannerResponse bannerResponse;
    private static final String TOPIC = "jeetodeals";
    private CartResponse cartResponse;
    private Utility utility = new Utility();
    private SharedPref pref = new SharedPref();
    private boolean isPlaying = false;
    private Handler handler = new Handler();
    String token;
    FragmentsRepository.ApiResponse<FcmResponse> fcmResponse;

    // Track active API requests to prevent duplicates
    private AtomicBoolean isLoadingHomeData = new AtomicBoolean(false);
    private AtomicBoolean isLoadingHomeData2 = new AtomicBoolean(false);
    private AtomicBoolean isLoadingBanner = new AtomicBoolean(false);
    private AtomicBoolean isLoadingBalance = new AtomicBoolean(false);
    private AtomicBoolean isLoadingCart = new AtomicBoolean(false);
    private AtomicBoolean isPostingToken = new AtomicBoolean(false);

    // Add to cart button cooldown flag
    private AtomicBoolean isAddToCartCooldown = new AtomicBoolean(false);

    // Track if session expired dialog is showing
    private static AtomicBoolean isSessionDialogShowing = new AtomicBoolean(false);

    // Track if data is already loaded
    private boolean initialDataLoaded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the binding and ensure it's properly initialized
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        fragmentsViewModel = new ViewModelProvider(this).get(FragmentsViewModel.class);

        // Safe Firebase initialization
        Context context = getContext();
        if (context != null && FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context);
        }

        Log.d("JWD", "onCreateView: ");


        getParentFragmentManager().setFragmentResultListener("ADD_TO_CART_REQUEST", this,
                (requestKey, result) -> {
                    if (result.containsKey("item")) {
                        HomeResponse item = (HomeResponse) result.getSerializable("item");
                        if (item != null) {
                            handleAddToCartAction(item);
                        }
                    }
                });



        // Safe FCM token retrieval
        if (isAdded()) {
            retrieveFcmToken();

            // Check for internet connection before fetching data
            if (getActivity() != null && utility.isInternetConnected(getActivity())) {
                loadInitialData();
            } else if (getContext() != null) {
                showToast("No internet connection!");
            }
        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                .addOnCompleteListener(task -> {
                    String msg = "Subscribed to topic: " + TOPIC;
                    if (!task.isSuccessful()) {
                        msg = "Topic subscription failed";
                    }
                    Log.d(TAG, msg);
                });


        // Video view setup
        setupVideoView();
        hidePlayPauseButton();
        return binding.getRoot();
    }

    private void loadInitialData() {
        if (!initialDataLoaded) {
            fetchHomeData();
            fetchHomeData2();
            fetchDrawData();
            fetchWinnerList();
            getBanner();
            getBalance();
            getCart();
            initialDataLoaded = true;
        }
    }

    private void retrieveFcmToken() {
        if (isPostingToken.get() || !isAdded()) return;

        isPostingToken.set(true);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    isPostingToken.set(false);

                    if (!isAdded()) return;

                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    token = task.getResult();

                    isLoggedIn = pref.getPrefBoolean(requireContext(), pref.login_status);
                    if (isAdded()&&isLoggedIn) {
                        postToken(token);
                    }
                    Log.d(TAG, "FCM Token: " + token);
                });
    }

    private void postToken(String token) {
        if (!isAdded() || isPostingToken.get()) return;

        isPostingToken.set(true);

        String auth = "Bearer " + pref.getPrefString(requireContext(), pref.user_token);
        User user = new User();
        user.setFcm_token(token);
        Log.d("GET TOKEN", "postToken: " + user.getFcm_token());

        fragmentsViewModel.postFcm(auth, user).observe(getViewLifecycleOwner(), apiResponse -> {
            isPostingToken.set(false);

            if (!isAdded()) return;

            if (apiResponse != null) {
                if (apiResponse.isSuccess) {
                    fcmResponse = apiResponse;
                    Log.d("TOKEN", "postToken: Success" + this.token);
                } else if ("Unauthorized".equals(apiResponse.message)) {
                    handleSessionExpiry();
                } else {
                    // Avoid showing toast for token failures
                    Log.w(TAG, "FCM token posting failed: " + apiResponse.message);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks

        // Reset all loading flags
        isLoadingHomeData.set(false);
        isLoadingHomeData2.set(false);
        isLoadingBanner.set(false);
        isLoadingBalance.set(false);
        isLoadingCart.set(false);
        isPostingToken.set(false);
        isAddToCartCooldown.set(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Only refresh data if needed and not already loading
        if (isAdded() && utility.isInternetConnected(requireActivity()) &&
                !isLoadingHomeData.get() && !isLoadingHomeData2.get() &&
                !isLoadingBanner.get() && !isLoadingBalance.get() && !isLoadingCart.get()) {

            // Only refresh token if not already in progress
            if (!isPostingToken.get()) {
                retrieveFcmToken();
            }

            // Only refresh cart on resume (most critical for fresh data)
            getCart();
        }
    }

    private void setupVideoView() {
        // Get screen width
        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        // Calculate height based on 3:2 aspect ratio
        int videoHeight = (screenWidth * 2) / 3;

        // Set the calculated height
        ViewGroup.LayoutParams layoutParams = binding.videoView.getLayoutParams();
        layoutParams.height = videoHeight;
        binding.videoView.setLayoutParams(layoutParams);

        binding.videoView.setOnPreparedListener(mp -> {
            binding.videoView.start();
            binding.playPauseButton.setVisibility(View.GONE);
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
            binding.videoView.start();
        });

        // Handle click on video view to show the button for 2 seconds
        binding.videoView.setOnClickListener(v -> {
            if (binding != null) {
                binding.playPauseButton.setVisibility(View.VISIBLE); // Show the play/pause button

                handler.postDelayed(() -> {
                    if (binding != null) { // Ensure binding is not null before accessing
                        binding.playPauseButton.setVisibility(View.INVISIBLE);
                    }
                }, 2000); // Hide after 2 seconds
            }
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

    public void getBalance() {

        isLoggedIn = pref.getPrefBoolean(requireContext(), pref.login_status);
        if (!isLoggedIn){
            return;
        }

        if (isLoadingBalance.get() || !isAdded()) return;

        isLoadingBalance.set(true);
        String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);

        fragmentsViewModel.getCurrentBalance(auth).observe(getViewLifecycleOwner(), apiResponse -> {
            isLoadingBalance.set(false);

            if (!isAdded()) return;

            if (apiResponse != null) {
                if (apiResponse.isSuccess) {
                    pref.setPrefString(requireActivity(), pref.main_balance, String.valueOf(apiResponse.data));
                } else if ("Unauthorized".equals(apiResponse.message)) {
                    handleSessionExpiry();
                } else {
                    Log.w(TAG, "Balance retrieval failed: " + apiResponse.message);
                }
            }
        });
    }

    private void fetchHomeData() {

        Log.d("JWD", "fetchHomeData: ");
        // Show loader when API call starts
        if (binding != null) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
        }

        if (isLoadingHomeData.get() || !isAdded()) return;

        isLoadingHomeData.set(true);
//        String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);
        String type = "lottery";

        viewModel.getHome( type, 52).observe(getViewLifecycleOwner(), response -> {
            isLoadingHomeData.set(false);

            Log.d("JWD", "fetchHomeData: RECEIVED ");
            if (!isAdded() || binding == null) return;

            // Hide loader regardless of response
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response != null) {
                if (response.isSuccess && response.data != null) {
                    responsee = response.data;
                    setupRecyclerViews(responsee);
                } else if ("Session expired".equals(response.message)) {
                    handleSessionExpiry();
                } else {
                    showToast(response.message != null ? response.message : "Unknown error");
                }
            } else {
                showToast("Something went wrong!");
            }
        });
    }
    private void fetchDrawData() {
        Log.d("fetchDrawData", "Method called");

        // Show loader when API call starts
        if (binding != null) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
        }

        Log.d("fetchDrawData", "Fetching data from API...");

        viewModel.getDraw().observe(getViewLifecycleOwner(), response -> {
            isLoadingHomeData.set(false);

            if (!isAdded() || binding == null) {
                Log.d("fetchDrawData", "Fragment not added or binding is null");
                return;
            }

            // Hide loader regardless of response
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response != null) {
                Log.d("fetchDrawData", "API Response received: " + response.toString());
                if (response.isSuccess && response.data != null) {
                    drawResponse = response.data;
                    Log.d("fetchDrawData", "Data loaded successfully, setting up RecyclerView");
                    setupRecyclerViews3(drawResponse);
                } else {
                    Log.e("fetchDrawData", "API Response error: " + response.message);
                    showToast(response.message != null ? response.message : "Unknown error");
                }
            } else {
                Log.e("fetchDrawData", "API response is null");
                showToast("Something went wrong!");
            }
        });
    }
    private void fetchWinnerList() {
        Log.d("fetchWinnerList", "Method called");

        // Show loader when API call starts
        if (binding != null) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
        }

        Log.d("fetchWinnerList", "Fetching data from API...");

        viewModel.getWinner().observe(getViewLifecycleOwner(), response -> {
            isLoadingHomeData.set(false);

            if (!isAdded() || binding == null) {
                Log.d("fetchWinnerList", "Fragment not added or binding is null");
                return;
            }

            // Hide loader regardless of response
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response != null) {
                Log.d("fetchWinnerList", "API Response received: " + response.toString());
                if (response.isSuccess && response.data != null) {
                    winnerResponse = response.data;
                    Log.d("fetchWinnerList", "Data loaded successfully, setting up RecyclerView");
                    setupRecyclerViews4(winnerResponse);
                } else {
                    Log.e("fetchWinnerList", "API Response error: " + response.message);
                    showToast(response.message != null ? response.message : "Unknown error");
                }
            } else {
                Log.e("fetchWinnerList", "API response is null");
                showToast("Something went wrong!");
            }
        });
    }

    private void setupRecyclerViews3(ArrayList<DrawResponse> drawResponse) {
        Log.d("setupRecyclerViews3", "Setting up RecyclerView with data: " + drawResponse);

        if (binding != null && drawResponse != null) {
            AdapterDraw adapter = new AdapterDraw(requireActivity(), drawResponse, this);
            binding.rcDraws.setAdapter(adapter);
            Log.d("setupRecyclerViews3", "Adapter set successfully.");
        } else {
            Log.e("setupRecyclerViews3", "Binding or data is null");
        }
    }
    private void setupRecyclerViews4(ArrayList<WinnerResponse> winnerResponse) {
        Log.d("setupRecyclerViews3", "Setting up RecyclerView with data: " + drawResponse);

        if (binding != null && winnerResponse != null) {
            AdapterWinner adapter = new AdapterWinner(requireActivity(), winnerResponse);
            binding.rcWinners.setAdapter(adapter);
            Log.d("setupRecyclerViews3", "Adapter set successfully.");
        } else {
            Log.e("setupRecyclerViews3", "Binding or data is null");
        }
    }


    private void fetchHomeData2() {
        // Show loader when API call starts
        if (binding != null) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
        }

        if (isLoadingHomeData2.get() || !isAdded()) return;

        isLoadingHomeData2.set(true);
        String type = "lottery";

        viewModel.getHome( type, 21).observe(getViewLifecycleOwner(), response -> {
            isLoadingHomeData2.set(false);

            if (!isAdded() || binding == null) return;

            // Hide loader regardless of response
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response != null) {
                if (response.isSuccess && response.data != null) {
                    responsee = response.data;
                    setupRecyclerViews2(responsee);
                } else if ("Session expired".equals(response.message)) {
                    handleSessionExpiry();
                } else {
                    showToast(response.message != null ? response.message : "Unknown error");
                }
            } else {
                showToast("Something went wrong!");
            }
        });
    }

    private void handleSessionExpiry() {
        // Only show dialog if not already showing
        if (!isSessionDialogShowing.getAndSet(true)) {
            showSessionExpiredDialog();
        }
    }

    private void showSessionExpiredDialog() {
        if (!isAdded()) return;

        // Reset session handling flag in HomeRepository first
        HomeRepository.resetSessionExpiryFlag();

        new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.MaterialAlertDialog_Material3)
                .setTitle("Session Expired")
                .setMessage("Your login has expired. Please log in again.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    pref.setPrefBoolean(requireActivity(), pref.login_status, false);

                    Intent intent = new Intent(requireActivity(), SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();

                    // Reset the flag when dialog is dismissed
                    isSessionDialogShowing.set(false);
                })
                .setOnDismissListener(dialog -> isSessionDialogShowing.set(false))
                .show();
    }

    public void getBanner() {
        // Show loader when API call starts
        if (binding != null) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
        }

        if (isLoadingBanner.get() || !isAdded()) return;

        isLoadingBanner.set(true);

        fragmentsViewModel.getBanner().observe(getViewLifecycleOwner(), response -> {
            isLoadingBanner.set(false);

            if (!isAdded() || binding == null) return;

            // Hide loader regardless of response
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response != null) {
                if (response.isSuccess && response.data != null) {
                    bannerResponse = response.data;

                    List<String> imageUrlList = new ArrayList<>();

                    if (bannerResponse.getVideo_banner_url() != null && !bannerResponse.getVideo_banner_url().isEmpty()) {
                        try {
                            // Properly handle HTTP URLs for VideoView - without controller
                            binding.videoView.setMediaController(null); // No media controls
                            binding.videoView.setVideoPath(bannerResponse.getVideo_banner_url());

                            // Add error listener
                            binding.videoView.setOnErrorListener((mp, what, extra) -> {
                                Log.e(TAG, "VideoView error: what=" + what + ", extra=" + extra);
                                return false; // Let the VideoView display its own error message
                            });

                            binding.videoView.setOnPreparedListener(mp -> {
                                // When video is ready to play
                                mp.setLooping(true); // Optional: loop the video
                                binding.videoView.start();
                            });

                            binding.videoView.requestFocus();
                        } catch (Exception e) {
                            Log.e(TAG, "Error setting video URL: " + e.getMessage(), e);
                            // Handle the error gracefully - perhaps show an image instead
                            if (bannerResponse.getImage_banner_url() != null && !bannerResponse.getImage_banner_url().isEmpty()) {
                                imageUrlList.add(bannerResponse.getImage_banner_url());
                            }
                        }
                    }

                    if (bannerResponse.getImage_banner_url() != null && !bannerResponse.getImage_banner_url().isEmpty()) {
                        imageUrlList.add(bannerResponse.getImage_banner_url());
                    }

                    if (!imageUrlList.isEmpty()) {
                        ImagePagerAdapter adapter = new ImagePagerAdapter(requireContext(), imageUrlList);
                        binding.imageViewpager.setAdapter(adapter);
                    }
                } else if ("Session expired".equals(response.message)) {
                    handleSessionExpiry();
                } else {
                    Log.w(TAG, "Banner retrieval failed: " + response.message);
                }
            } else {
                showToast("Something went wrong!");
            }
        });
    }



    public void getCart() {

        isLoggedIn = pref.getPrefBoolean(requireContext(), pref.login_status);
        if (!isLoggedIn){
            return;
        }

        // Show loader when API call starts
        if (binding != null) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
        }

        if (isLoadingCart.get() || !isAdded()) return;

        isLoadingCart.set(true);
        String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);

        fragmentsViewModel.getCart(auth).observe(getViewLifecycleOwner(), response -> {
            isLoadingCart.set(false);

            if (!isAdded() || binding == null) return;

            // Hide loader regardless of response
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response != null) {
                if (response.isSuccess && response.data != null) {
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
                } else if ("Unauthorized".equals(response.message)) {
                    handleSessionExpiry();
                } else {
                    Log.w(TAG, "Cart retrieval failed: " + response.message);
                }
            } else {
                showToast("Something went wrong!");
            }
        });
    }

    private void setupRecyclerViews(ArrayList<HomeResponse> responsee) {
        if (binding != null && responsee != null) {
            AdapterPromotion1 adapter = new AdapterPromotion1(requireActivity(), responsee, this);
            binding.promotionsRecycler.setAdapter(adapter);
        }
    }

    private void setupRecyclerViews2(ArrayList<HomeResponse> responsee) {
        if (binding != null && responsee != null) {
            AdapterPromotion2 adapter2 = new AdapterPromotion2(requireActivity(), responsee, this);
            binding.rcPromotion2.setAdapter(adapter2);
        }
    }

    @Override
    public void onAddToCartClicked(HomeResponse item) {
        // Check if button is in cooldown period
        if (isAddToCartCooldown.get()) {
            return;
        }

        // Set cooldown flag to prevent rapid clicks
        isAddToCartCooldown.set(true);

        // Show loader when adding to cart
        if (binding != null) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
        }

        // Start cooldown timer
        handler.postDelayed(() -> isAddToCartCooldown.set(false), 1000);

        handleAddToCartAction(item);
    }

    @Override
    public void onAddToCartClicked2(HomeResponse item) {
        // Check if button is in cooldown period
        if (isAddToCartCooldown.get()) {
            return;
        }

        // Set cooldown flag to prevent rapid clicks
        isAddToCartCooldown.set(true);

        // Show loader when adding to cart
        if (binding != null) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
        }

        // Start cooldown timer
        handler.postDelayed(() -> isAddToCartCooldown.set(false), 1000);

        handleAddToCartAction(item);
    }

    private void handleAddToCartAction(HomeResponse item) {
        if (!isAdded() || binding == null) return;

        // Check if user is logged in
         isLoggedIn = pref.getPrefBoolean(requireContext(), pref.login_status);

        if (!isLoggedIn) {
            // User is not logged in, redirect to login screen
            Intent intent = new Intent(requireActivity(), SignInActivity.class);
            startActivity(intent);
            return;
        }

        // User is logged in, proceed with cart actions
        String authToken = "Bearer " + pref.getPrefString(requireContext(), pref.user_token);
        String nonce = pref.getPrefString(requireActivity(), pref.nonce);

        if (cartResponse != null && cartResponse.getItems() != null && !cartResponse.getItems().isEmpty()) {
            if (!isCartAllLottery(cartResponse)) {
                showCartClearDialog(item, authToken, nonce);
                return;
            }
        }

        addItemToCart(item, authToken, nonce);
    }

    private boolean isCartAllLottery(CartResponse cartResponse) {
        if (cartResponse == null || cartResponse.getItems() == null || cartResponse.getItems().isEmpty()) {
            return true; // Empty cart can accept lottery items
        }

        for (Items item : cartResponse.getItems()) {
            if (!"lottery".equalsIgnoreCase(item.getType())) {
                return false;
            }
        }
        return true;
    }

    private void showCartClearDialog(HomeResponse item, String authToken, String nonce) {
        if (!isAdded() || binding == null) return;

        // Hide loader before showing dialog
        binding.loader.rlLoader.setVisibility(View.GONE);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cart Conflict")
                .setMessage("Your cart contains items of a different type. Adding this item will clear your cart. Do you want to proceed?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    // Show loader again when user confirms
                    if (binding != null) {
                        binding.loader.rlLoader.setVisibility(View.VISIBLE);
                    }
                    addItemToCart(item, authToken, nonce);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void addItemToCart(HomeResponse item, String authToken, String nonce) {
        if (!isAdded() || binding == null) return;

        AddItems addItems = new AddItems();
        addItems.setId(item.getId());
        addItems.setQuantity(1);

        fragmentsViewModel.AddToCart(authToken, nonce, addItems).observe(getViewLifecycleOwner(), response -> {
            if (!isAdded() || binding == null) return;

            // Hide loader regardless of response
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response != null) {
                if (response.isSuccess && response.data != null) {
                    cartResponse = response.data;
                    if (cartResponse.getItems() != null) {
                        int cartCount = cartResponse.getItems_count();
                        pref.setPrefInteger(requireActivity(), pref.cart_count, cartCount);

                        // Update badge in ContainerActivity
                        if (getActivity() instanceof ContainerActivity) {
                            ((ContainerActivity) getActivity()).updateCartBadge(cartCount);
                        }
                    }
                    getCart(); // Refresh cart data
                    showToast("Item has been added successfully");
                } else if ("Session expired".equals(response.message)) {
                    handleSessionExpiry();
                } else {
                    showToast(response.message != null ? response.message : "Unknown error");
                }
            } else {
                showToast("Something went wrong!");
            }
        });
    }

    // Centralized toast method to prevent multiple toasts
    private void showToast(String message) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAddToCart(HomeResponse item) {
        // Implement the cooldown logic here too
        if (isAddToCartCooldown.get()) {
            return;
        }

        // Set cooldown flag to prevent rapid clicks
        isAddToCartCooldown.set(true);

        // Show loader when adding to cart
        if (binding != null) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
        }

        // Start cooldown timer
        handler.postDelayed(() -> isAddToCartCooldown.set(false), 1000);
        handleAddToCartAction(item);
    }

    @Override
    public void onButtonClicked(DrawResponse item) {
        String link = item.getDraw_button_link();

        if (link != null && !link.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                requireActivity().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(requireActivity(), "No browser found to open the link", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireActivity(), "Invalid link", Toast.LENGTH_SHORT).show();
        }
    }

}