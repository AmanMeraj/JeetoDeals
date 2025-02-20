package com.deals.jeetodeals.Fragments.HomeFragment;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.deals.jeetodeals.Adapters.AdapterPromotion1;
import com.deals.jeetodeals.Adapters.AdapterPromotion2;
import com.deals.jeetodeals.Adapters.ImagePagerAdapter;
import com.deals.jeetodeals.Fragments.FragmentsRepository;
import com.deals.jeetodeals.Fragments.FragmentsViewModel;
import com.deals.jeetodeals.Model.AddItems;
import com.deals.jeetodeals.Model.BannerResponse;
import com.deals.jeetodeals.Model.CartResponse;
import com.deals.jeetodeals.Model.FcmResponse;
import com.deals.jeetodeals.Model.Items;
import com.deals.jeetodeals.Model.User;
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

public class HomeFragment extends Fragment implements AdapterPromotion1.OnItemClickListener, AdapterPromotion2.OnItemClickListener2 {
    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private FragmentsViewModel fragmentsViewModel;
    private ArrayList<HomeResponse> responsee;
    private BannerResponse bannerResponse;
    private CartResponse cartResponse;
    private Utility utility = new Utility();
    private SharedPref pref = new SharedPref();
    private boolean isPlaying = false;
    private ActivityResultLauncher<String> requestPermissionLauncher;
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

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    Log.d("NotificationPermission", isGranted ? "Permission granted!" : "Permission denied!");
                }
        );

        checkAndRequestNotificationPermission();

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

        // Video view setup
        setupVideoView();
        hidePlayPauseButton();
        return binding.getRoot();
    }

    private void loadInitialData() {
        if (!initialDataLoaded) {
            fetchHomeData();
            fetchHomeData2();
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
                    if (isAdded()) {
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
        if (isLoadingHomeData.get() || !isAdded()) return;

        isLoadingHomeData.set(true);
        String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);
        String type = "lottery";

        viewModel.getHome(auth, type, 20).observe(getViewLifecycleOwner(), response -> {
            isLoadingHomeData.set(false);

            if (!isAdded()) return;

            if (response != null) {
                if (response.isSuccess && response.data != null) {
                    responsee = response.data;
                    setupRecyclerViews(responsee);
                } else if ("Session expired".equals(response.message)) {
                    handleSessionExpiry();
                } else {
                    showToast(response.message != null ? response.message : "Unknown error");
                }
            }
        });
    }

    private void fetchHomeData2() {
        if (isLoadingHomeData2.get() || !isAdded()) return;

        isLoadingHomeData2.set(true);
        String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);
        String type = "lottery";

        viewModel.getHome(auth, type, 21).observe(getViewLifecycleOwner(), response -> {
            isLoadingHomeData2.set(false);

            if (!isAdded()) return;

            if (response != null) {
                if (response.isSuccess && response.data != null) {
                    responsee = response.data;
                    setupRecyclerViews2(responsee);
                } else if ("Session expired".equals(response.message)) {
                    handleSessionExpiry();
                } else {
                    showToast(response.message != null ? response.message : "Unknown error");
                }
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
                .setMessage("Your session has expired. Please log in again.")
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
        if (isLoadingBanner.get() || !isAdded()) return;

        isLoadingBanner.set(true);
        String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);

        fragmentsViewModel.getBanner(auth).observe(getViewLifecycleOwner(), response -> {
            isLoadingBanner.set(false);

            if (!isAdded()) return;

            if (response != null) {
                if (response.isSuccess && response.data != null) {
                    bannerResponse = response.data;

                    List<String> imageUrlList = new ArrayList<>();

                    if (bannerResponse.getVideo_banner_url() != null && !bannerResponse.getVideo_banner_url().isEmpty()) {
                        Uri videoUri = Uri.parse(bannerResponse.getVideo_banner_url());
                        binding.videoView.setVideoURI(videoUri);
                        binding.videoView.start();
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
            }
        });
    }

    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (requireContext().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                Log.d("NotificationPermission", "Notification permission is already granted.");
            } else {
                Log.d("NotificationPermission", "Requesting notification permission.");
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            Log.d("NotificationPermission", "Notification permission is not required for this Android version.");
        }
    }

    public void getCart() {
        if (isLoadingCart.get() || !isAdded()) return;

        isLoadingCart.set(true);
        String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);

        fragmentsViewModel.getCart(auth).observe(getViewLifecycleOwner(), response -> {
            isLoadingCart.set(false);

            if (!isAdded()) return;

            if (response != null) {
                if (response.isSuccess && response.data != null) {
                    cartResponse = response.data;
                    String nonce = FragmentsRepository.getNonce();
                    pref.setPrefString(requireActivity(), pref.nonce, nonce);
                    Log.d("GET CART NONCE", "getCart: " + nonce);
                } else if ("Unauthorized".equals(response.message)) {
                    handleSessionExpiry();
                } else {
                    Log.w(TAG, "Cart retrieval failed: " + response.message);
                }
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
        handleAddToCartAction(item);
    }

    @Override
    public void onAddToCartClicked2(HomeResponse item) {
        handleAddToCartAction(item);
    }

    private void handleAddToCartAction(HomeResponse item) {
        if (!isAdded()) return;

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
        if (!isAdded()) return;

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

    private void addItemToCart(HomeResponse item, String authToken, String nonce) {
        if (!isAdded()) return;

        AddItems addItems = new AddItems();
        addItems.setId(item.getId());
        addItems.setQuantity(1);

        fragmentsViewModel.AddToCart(authToken, nonce, addItems).observe(getViewLifecycleOwner(), response -> {
            if (!isAdded()) return;

            if (response != null) {
                if (response.isSuccess && response.data != null) {
                    cartResponse = response.data;
                    getCart(); // Refresh cart data
                    showToast("Item has been added successfully");
                } else if ("Session expired".equals(response.message)) {
                    handleSessionExpiry();
                } else {
                    showToast(response.message != null ? response.message : "Unknown error");
                }
            }
        });
    }

    // Centralized toast method to prevent multiple toasts
    private void showToast(String message) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}