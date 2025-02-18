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
import com.deals.jeetodeals.Model.MyItem;
import com.deals.jeetodeals.Model.Promotion;
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

public class HomeFragment extends Fragment implements AdapterPromotion1.OnItemClickListener,AdapterPromotion2.OnItemClickListener2{
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
                    // Handle the result of the permission request
                    if (isGranted) {
                        Log.d("NotificationPermission", "Permission granted!");
                    } else {
                        Log.d("NotificationPermission", "Permission denied!");
                    }
                }
        );

        checkAndRequestNotificationPermission();

        // Safe FCM token retrieval
        if (isAdded()) {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!isAdded()) {
                            return; // Exit if Fragment is not attached
                        }

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get the FCM token
                        token = task.getResult();
                        if (isAdded()) {  // Double check before posting token
                            postToken(token);
                        }
                        Log.d(TAG, "FCM Token: " + token);
                    });

            // Check for internet connection before fetching data
            if (getActivity() != null && utility.isInternetConnected(getActivity())) {
                fetchHomeData();
                fetchHomeData2();
                getBanner();
                getBalance();
                getCart();
            } else if (getContext() != null) {
                Toast.makeText(getContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
            }
        }

        // Video view setup
        setupVideoView();
        hidePlayPauseButton();
        return binding.getRoot();
    }

    private void postToken(String token) {
        if (!isAdded()) {
            return; // Exit if Fragment is not attached
        }

        String auth = "Bearer " + pref.getPrefString(requireContext(), pref.user_token);
        User user = new User();
        user.setFcm_token(token);
        Log.d("GET TOKEN", "postToken: " + user.getFcm_token());

        fragmentsViewModel.postFcm(auth, user).observe(getViewLifecycleOwner(), apiResponse -> {
            if (!isAdded()) {
                return; // Exit if Fragment is not attached
            }

            if (apiResponse != null) {
                if (apiResponse.isSuccess) {
                    fcmResponse = apiResponse;
                    Log.d("TOKEN", "postToken: Success" + this.token);
                } else {
                    Toast.makeText(requireActivity(), apiResponse.message, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireActivity(), " ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }
    @Override
    public void onResume() {
        super.onResume();
        // Get FCM token when Fragment is attached and visible
        if (isAdded()) {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!isAdded()) {
                            return; // Exit if Fragment is not attached
                        }

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        token = task.getResult();
                        postToken(token);
                        Log.d(TAG, "FCM Token: " + token);
                    });

            // Check internet and fetch data
            if (utility.isInternetConnected(requireActivity())) {
                fetchHomeData();
                fetchHomeData2();
                getBanner();
                getBalance();
                getCart();
            } else {
                Toast.makeText(requireActivity(), "No internet connection!", Toast.LENGTH_SHORT).show();
            }
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
//            binding.playPauseButton.setImageResource(R.drawable.play_jd); // Reset to play icon
//            binding.playPauseButton.setVisibility(View.VISIBLE); // Make the button visible again
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

    public void getBalance(){
        String auth= "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);

        fragmentsViewModel.getCurrentBalance(auth).observe(getViewLifecycleOwner(),apiResponse->{
            if (apiResponse != null) {
                if (apiResponse.isSuccess) {
                   pref.setPrefString(requireActivity(),pref.main_balance, String.valueOf(apiResponse.data));
                } else {
                    // Display the error message from the API response
                    Toast.makeText(requireActivity(), apiResponse.message, Toast.LENGTH_SHORT).show();
                }
            } else {
                // Null response or network failure
                Toast.makeText(requireActivity(), " ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchHomeData() {
        String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token); // Replace with actual token
        String type = "lottery"; // Replace with required type

        viewModel.getHome(auth, type,20).observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                if (response.isSuccess && response.data != null) {
                    responsee = response.data; // Store response
                    setupRecyclerViews(responsee); // Populate RecyclerView
                } else if ("Unauthorized".equals(response.message)) { // Check for 401 status
                    showSessionExpiredDialog();
                } else {
                    Toast.makeText(requireContext(), response.message != null ? response.message : "Unknown error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void fetchHomeData2(){
        String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token); // Replace with actual token
        String type = "lottery";

        viewModel.getHome(auth, type,21).observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                if (response.isSuccess && response.data != null) {
                    responsee = response.data; // Store response
                    setupRecyclerViews2(responsee); // Populate RecyclerView
                } else if ("Unauthorized".equals(response.message)) { // Check for 401 status
                    showSessionExpiredDialog();
                } else {
                    Toast.makeText(requireContext(), response.message != null ? response.message : "Unknown error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void showSessionExpiredDialog() {
        new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.MaterialAlertDialog_Material3)
                .setTitle("Session Expired")
                .setMessage("Your session has expired. Please log in again.")
                .setCancelable(false) // Prevent dismissing the dialog
                .setPositiveButton("OK", (dialog, which) -> {
                    // Clear stored auth token
                   pref.setPrefBoolean(requireActivity(),pref.login_status,false);

                    // Navigate to SigninActivity
                    Intent intent = new Intent(requireActivity(), SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .show();
    }


    public void getBanner() {
        String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);
        fragmentsViewModel.getBanner(auth).observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.isSuccess && response.data != null) {
                bannerResponse = response.data;

                // Prepare a list to hold image URLs (either image or video)
                List<String> imageUrlList = new ArrayList<>();

                // If there is a video URL, handle video logic as before
                if (bannerResponse.getVideo_banner_url() != null && !bannerResponse.getVideo_banner_url().isEmpty()) {
                    Uri videoUri = Uri.parse(bannerResponse.getVideo_banner_url());
                    binding.videoView.setVideoURI(videoUri);
                    binding.videoView.start();
                }

                // If there's an image URL, add it to the list
                if (bannerResponse.getImage_banner_url() != null && !bannerResponse.getImage_banner_url().isEmpty()) {
                    imageUrlList.add(bannerResponse.getImage_banner_url());
                }

                // Pass the image URLs to the adapter (if there are any)
                if (!imageUrlList.isEmpty()) {
                    ImagePagerAdapter adapter = new ImagePagerAdapter(requireContext(), imageUrlList);
                    binding.imageViewpager.setAdapter(adapter);
                }

            } else {
                Toast.makeText(requireContext(), response != null ? response.message : "Unknown error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkAndRequestNotificationPermission() {
        // Check if the Android version is 13 (TIRAMISU) or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if the notification permission is already granted
            if (requireContext().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                Log.d("NotificationPermission", "Notification permission is already granted.");
            } else {
                // Request the notification permission since it is not granted
                Log.d("NotificationPermission", "Requesting notification permission.");
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            // No need to request notification permission for Android versions below 13
            Log.d("NotificationPermission", "Notification permission is not required for this Android version.");
        }
    }

    public void getCart() {
        String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token); // Replace with actual token
        fragmentsViewModel.getCart(auth).observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.isSuccess && response.data != null) {
                cartResponse = response.data;
                String nonce = FragmentsRepository.getNonce();
                pref.setPrefString(requireActivity(), pref.nonce, nonce);
                Log.d("GET CART NONCE", "getCart: " + nonce);
            } else {
                Toast.makeText(requireContext(), response != null ? response.message : "Unknown error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerViews(ArrayList<HomeResponse> responsee) {
        AdapterPromotion1 adapter = new AdapterPromotion1(requireActivity(), responsee, this);
        binding.promotionsRecycler.setAdapter(adapter);
    }
    private void setupRecyclerViews2(ArrayList<HomeResponse> responsee) {
        AdapterPromotion2 adapter2 = new AdapterPromotion2(requireActivity(), responsee, this);
        binding.rcPromotion2.setAdapter(adapter2);
    }

    @Override
    public void onAddToCartClicked(HomeResponse item) {
        String authToken = "Bearer " + pref.getPrefString(requireContext(), pref.user_token);
        String nonce = pref.getPrefString(requireActivity(), pref.nonce);

        if (cartResponse != null && cartResponse.getItems() != null && !cartResponse.getItems().isEmpty()) {
            if (!isCartAllLottery(cartResponse)) {  // ✅ Pass a single object, not a list
                showCartClearDialog(item, authToken, nonce);
                return;
            }
        }

        // If all items are "lottery" or cart is empty, add the item directly
        addItemToCart(item, authToken, nonce);
    }

    /**
     * Checks if all items in the cart are of type "lottery".
     */
    private boolean isCartAllLottery(CartResponse cartResponse) {  // ✅ Accepts a single CartResponse
        if (cartResponse == null || cartResponse.getItems() == null || cartResponse.getItems().isEmpty()) {
            return false; // Return false if cart is empty or invalid
        }

        for (Items item : cartResponse.getItems()) { // ✅ Iterate through items in a single CartResponse
            if (!"lottery".equalsIgnoreCase(item.getType())) { // ✅ Check type instead of category
                return false;
            }
        }
        return true;
    }

    /**
     * Shows a dialog informing the user that adding this item will clear the cart.
     */
    private void showCartClearDialog(HomeResponse item, String authToken, String nonce) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cart Conflict")
                .setMessage("Your cart contains items of a different type. Adding this item will clear your cart. Do you want to proceed?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    addItemToCart(item, authToken, nonce);  // ✅ Add item only if user confirms
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Adds an item to the cart.
     */
    private void addItemToCart(HomeResponse item, String authToken, String nonce) {
        AddItems addItems = new AddItems();
        addItems.setId(item.getId());
        addItems.setQuantity(1);

        fragmentsViewModel.AddToCart(authToken, nonce, addItems).observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.isSuccess && response.data != null) {
                cartResponse = response.data;
                getCart(); // Refresh cart data
                Toast.makeText(requireActivity(), "Item has been added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), response != null ? response.message : "Unknown error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAddToCartClicked2(HomeResponse item) {
        String authToken = "Bearer " + pref.getPrefString(requireContext(), pref.user_token);
        String nonce = pref.getPrefString(requireActivity(), pref.nonce);

        if (cartResponse != null && cartResponse.getItems() != null && !cartResponse.getItems().isEmpty()) {
            if (!isCartAllLottery(cartResponse)) {  // ✅ Pass a single object, not a list
                showCartClearDialog(item, authToken, nonce);
                return;
            }
        }

        // If all items are "lottery" or cart is empty, add the item directly
        addItemToCart(item, authToken, nonce);
    }
}
