package com.deals.jeetodeals.Fragments;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.deals.jeetodeals.Adapters.AdapterCart;
import com.deals.jeetodeals.Checkout.ActivityCheckout;
import com.deals.jeetodeals.ContainerActivity.ContainerActivity;
import com.deals.jeetodeals.Model.AddItems;
import com.deals.jeetodeals.Model.CartResponse;
import com.deals.jeetodeals.Model.Items;
import com.deals.jeetodeals.Model.Total;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.Utils.SharedPref;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.databinding.FragmentCartBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CartFragment extends Fragment implements AdapterCart.OnCartItemActionListener { // Step 1: Implement interface

    FragmentCartBinding binding;
    Utility utility = new Utility();
    private boolean isWalletSelected = false; // Track selection state
    SharedPref pref = new SharedPref();
    FragmentsViewModel fragmentsViewModel;
    CartResponse responsee;
    private AdapterCart adapter;
    private AtomicBoolean isLoadingBalance = new AtomicBoolean(false);
    private List<Items> itemList = new ArrayList<>(); // Store cart items

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(getLayoutInflater());
        fragmentsViewModel = new ViewModelProvider(this).get(FragmentsViewModel.class);

        if (utility.isInternetConnected(requireActivity())) {
            getCart();
            getBalance();
        } else {
            Toast.makeText(requireActivity(), "No internet connection!", Toast.LENGTH_SHORT).show();
        }
        binding.proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String paymentMethod;
                boolean allLottery = true;
                boolean allSimple = true;

                for (Items item : itemList) {
                    if (!"lottery".equalsIgnoreCase(item.getType())) {
                        allLottery = false;
                    }
                    if (!"simple".equalsIgnoreCase(item.getType())) {
                        allSimple = false;
                    }
                }

                if (allLottery) {
                    paymentMethod = "razorpay";
                } else {
                    paymentMethod = "wallet";
                }

                Intent intent = new Intent(requireActivity(), ActivityCheckout.class);
                intent.putExtra("cart_response", responsee);
                intent.putExtra("payment_method", paymentMethod);
                startActivity(intent);
            }
        });


        return binding.getRoot();
    }
    private void toggleWalletSelection(Total totals) {
        isWalletSelected = !isWalletSelected;
        binding.walletBalance.setChecked(isWalletSelected);
        updateProceedButton(totals);
    }

    private void updateProceedButton(Total totals) {
        int walletBalance = Integer.parseInt(pref.getPrefString(requireActivity(), pref.main_balance));
        int totalPrice = Integer.parseInt(totals.getTotal_price());

        if (isWalletSelected && walletBalance >= totalPrice) {
            binding.proceedBtn.setVisibility(View.VISIBLE);
        } else {
            binding.proceedBtn.setVisibility(View.GONE);
        }
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
                } else {
                    Log.w(TAG, "Balance retrieval failed: " + apiResponse.message);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getCart();
        getBalance();
    }

    public void getCart() {
        showLoader(true);

        String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);
        Log.d("TAG", "getCart: " + auth);

        fragmentsViewModel.getCart(auth).observe(getViewLifecycleOwner(), response -> {

            if (response != null && response.isSuccess && response.data != null) {
                showLoader(false);
                responsee = response.data;
                String nonce = FragmentsRepository.getNonce();
                pref.setPrefString(requireActivity(), pref.nonce, nonce);

                itemList = responsee.items; // Store items in the list
                int cartCount = responsee.getItems_count();
                pref.setPrefInteger(requireActivity(), pref.cart_count, cartCount);
                if (getActivity() instanceof ContainerActivity) {
                    ((ContainerActivity) getActivity()).updateCartBadge(cartCount);
                }

                populateRC(responsee.totals);
            } else {
                showLoader(false);
                binding.ivEmptyCart.setVisibility(View.VISIBLE); // Show empty cart icon
                Toast.makeText(requireContext(), response != null ? response.message : "Unknown error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void populateRC(Total totals) {
        if (itemList.isEmpty()) {
            binding.noItem.setVisibility(View.VISIBLE);
            binding.cardTotal.setVisibility(View.GONE);
            binding.tvTotal.setVisibility(View.GONE);
            binding.proceedBtn.setVisibility(View.GONE);
            binding.rcCart.setVisibility(View.GONE);
            binding.walletBalance.setVisibility(View.GONE);
            return; // Exit if no items
        } else {
            binding.noItem.setVisibility(View.GONE);
            binding.cardTotal.setVisibility(View.VISIBLE);
            binding.tvTotal.setVisibility(View.VISIBLE);
            binding.rcCart.setVisibility(View.VISIBLE);
        }

        // Perform actions based on item type
        boolean allLottery = true;
        boolean allSimple = true;

        for (Items item : itemList) {
            if (!"lottery".equalsIgnoreCase(item.getType())) {
                allLottery = false;
            }
            if (!"simple".equalsIgnoreCase(item.getType())) {
                allSimple = false;
            }
        }

        adapter = new AdapterCart(requireActivity(), itemList, this);
        binding.rcCart.setAdapter(adapter);

        if (allLottery) {
            binding.walletBalance.setVisibility(View.GONE);
            binding.total.setText(responsee.totals.currency_code + " " + responsee.totals.getTotal_price());
            binding.subTotal.setText(responsee.totals.currency_code + " " + responsee.totals.getTotal_items());
        } else {
            binding.walletBalance.setVisibility(View.VISIBLE);
            binding.walletBalance.setText("Wallet Balance: Vouchers " + pref.getPrefString(requireActivity(), pref.main_balance));

            binding.total.setText(responsee.totals.currency_symbol + " " + responsee.totals.getTotal_price());
            binding.subTotal.setText(responsee.totals.currency_symbol + " " + responsee.totals.getTotal_items());

            // Wallet Balance Selection Logic
            binding.walletBalance.setOnClickListener(v -> toggleWalletSelection(totals));

            // Initially check if the wallet is selected or not
            updateProceedButton(totals);
        }
    }





    @Override
    public void onItemDeleted(int position) {
        if (position >= 0 && position < itemList.size()) {
            showLoader(true);
            String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);
            String nonce = pref.getPrefString(requireActivity(), pref.nonce);
            AddItems delete = new AddItems();
            delete.setKey(itemList.get(position).getKey());

            fragmentsViewModel.RemoveItem(auth, nonce, delete).observe(getViewLifecycleOwner(), response -> {
                if (response != null && response.isSuccess && response.data != null) {
                    responsee = response.data;
                    itemList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, itemList.size());
                    Toast.makeText(requireContext(), "Item removed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), response != null ? response.message : "Unknown error", Toast.LENGTH_SHORT).show();
                }
                getCart(); // Call getCart() to refresh the UI
            });
        }
    }



    @Override
    public void onQuantityIncreased(int position) {
        if (position >= 0 && position < itemList.size()) {
            Items item = itemList.get(position);

            if (item.getQuantity() < item.getQuantity_limits().getMaximum()) {
                showLoader(true);

                item.setQuantity(item.getQuantity() + 1);
                adapter.notifyItemChanged(position);
            } else {
                Toast.makeText(requireContext(), "Maximum quantity reached", Toast.LENGTH_SHORT).show();
                return;
            }

            String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);
            String nonce = pref.getPrefString(requireActivity(), pref.nonce);

            AddItems add = new AddItems();
            add.setKey(item.getKey());
            add.setQuantity(item.getQuantity());

            fragmentsViewModel.UpdateItem(auth, nonce, add).observe(getViewLifecycleOwner(), response -> {
                if (response != null && response.isSuccess && response.data != null) {
                    responsee = response.data;
                    Toast.makeText(requireContext(), "Quantity updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), response != null ? response.message : "Unknown error", Toast.LENGTH_SHORT).show();
                }
                getCart(); // Refresh the UI
            });
        }
    }


    @Override
    public void onQuantityDecreased(int position) {
        if (position >= 0 && position < itemList.size()) {
            Items item = itemList.get(position);

            if (item.getQuantity() > item.getQuantity_limits().getMinimum()) {
                showLoader(true);
                item.setQuantity(item.getQuantity() - 1);
                adapter.notifyItemChanged(position);
            } else {
                Toast.makeText(requireContext(), "Minimum quantity reached", Toast.LENGTH_SHORT).show();
                return;
            }

            String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);
            String nonce = pref.getPrefString(requireActivity(), pref.nonce);

            AddItems add = new AddItems();
            add.setKey(item.getKey());
            add.setQuantity(item.getQuantity());

            fragmentsViewModel.UpdateItem(auth, nonce, add).observe(getViewLifecycleOwner(), response -> {
                if (response != null && response.isSuccess && response.data != null) {
                    responsee = response.data;
                    Toast.makeText(requireContext(), "Quantity updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), response != null ? response.message : "Unknown error", Toast.LENGTH_SHORT).show();
                }
                getCart(); // Refresh the UI
            });
        }
    }
    private void showLoader(boolean show) {
        if (show) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE); // Show loader
            binding.layoutCart.setVisibility(View.GONE); // Hide entire cart layout
            binding.ivEmptyCart.setVisibility(View.GONE); // Hide empty cart message
        } else {
            binding.loader.rlLoader.setVisibility(View.GONE); // Hide loader
            binding.layoutCart.setVisibility(View.VISIBLE); // Show cart layout
        }
    }




}
