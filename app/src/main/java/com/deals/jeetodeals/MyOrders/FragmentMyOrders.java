package com.deals.jeetodeals.MyOrders;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.deals.jeetodeals.Adapters.AdapterOrders;
import com.deals.jeetodeals.ContainerActivity.ContainerActivity;
import com.deals.jeetodeals.Fragments.FragmentsRepository;
import com.deals.jeetodeals.Fragments.FragmentsViewModel;
import com.deals.jeetodeals.Fragments.HomeFragment.HomeRepository;
import com.deals.jeetodeals.Model.CartResponse;
import com.deals.jeetodeals.SignInScreen.SignInActivity;
import com.deals.jeetodeals.Utils.SharedPref;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.databinding.FargmentMyOrdersBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class FragmentMyOrders extends Fragment {

    private FargmentMyOrdersBinding binding;
    private MyOrderViewModel viewModel;
    private FragmentsViewModel fragmentViewModel;
    private AtomicBoolean isLoadingCart = new AtomicBoolean(false);
    CartResponse cartResponse;

    SharedPref pref = new SharedPref();
    private static AtomicBoolean isSessionDialogShowing = new AtomicBoolean(false);

    Utility utility = new Utility();
    private ArrayList<MyOrdersResponse> responsee;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FargmentMyOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MyOrderViewModel.class);
        fragmentViewModel =new ViewModelProvider(this).get(FragmentsViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        if (utility.isInternetConnected(requireContext())) {
            getOrders();
            getCart();
        } else {
            Toast.makeText(requireContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getOrders() {
        binding.loader.rlLoader.setVisibility(View.VISIBLE);
        String auth = "Bearer " + utility.pref.getPrefString(requireContext(), utility.pref.user_token);
        Log.d("TAG", "getOrders: auth "+auth);
        viewModel.getOrders(auth).observe(getViewLifecycleOwner(), response -> {
            binding.loader.rlLoader.setVisibility(View.GONE);
            if (response != null && response.isSuccess && response.data != null) {
                responsee = response.data;
                setUpRecyclerView(responsee);
            } else if ("Expired token".equals(response.message)) {
                handleSessionExpiry();
            } else {
                Toast.makeText(requireContext(), "" + (response != null ? response.message : "Error fetching orders"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpRecyclerView(ArrayList<MyOrdersResponse> responsee) {
        if (responsee == null || responsee.isEmpty()) {
            binding.rcOrders.setVisibility(View.GONE);
            binding.noItem.setVisibility(View.VISIBLE);
        } else {
            binding.rcOrders.setVisibility(View.VISIBLE);
            binding.noItem.setVisibility(View.GONE);
            AdapterOrders adapter = new AdapterOrders(requireContext(), responsee);
            binding.rcOrders.setAdapter(adapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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

    public void getCart() {
        // Show loader when API call starts
        if (binding != null) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
        }

        if (isLoadingCart.get() || !isAdded()) return;

        isLoadingCart.set(true);
        String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);

        fragmentViewModel.getCart(auth).observe(getViewLifecycleOwner(), response -> {
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

    @Override
    public void onResume() {
        super.onResume();
        getCart();
    }

    private void showToast(String message) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
