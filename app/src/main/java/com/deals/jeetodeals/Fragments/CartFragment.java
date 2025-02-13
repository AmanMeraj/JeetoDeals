package com.deals.jeetodeals.Fragments;

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

public class CartFragment extends Fragment implements AdapterCart.OnCartItemActionListener { // Step 1: Implement interface

    FragmentCartBinding binding;
    Utility utility = new Utility();
    SharedPref pref = new SharedPref();
    FragmentsViewModel fragmentsViewModel;
    CartResponse responsee;
    private AdapterCart adapter;
    private List<Items> itemList = new ArrayList<>(); // Store cart items

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(getLayoutInflater());
        fragmentsViewModel = new ViewModelProvider(this).get(FragmentsViewModel.class);

        if (utility.isInternetConnected(requireActivity())) {
            getCart();
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

    public void getCart() {
        String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);
        Log.d("TAG", "getCart: " + auth);
        fragmentsViewModel.getCart(auth).observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.isSuccess && response.data != null) {
                responsee = response.data;
                String nonce = FragmentsRepository.getNonce();
                pref.setPrefString(requireActivity(), pref.nonce, nonce);

                itemList = responsee.items; // Store items in the list

                populateRC(responsee.totals);
            } else {
                Toast.makeText(requireContext(), response != null ? response.message : "Unknown error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateRC(Total totals) {
        if (itemList.isEmpty()) {
            binding.cardTotal.setVisibility(View.GONE);
            binding.tvTotal.setVisibility(View.GONE);
            binding.proceedBtn.setVisibility(View.GONE);
            binding.rcCart.setVisibility(View.GONE);
            binding.ivEmptyCart.setVisibility(View.VISIBLE);

            Glide.with(this)
                    .asGif()
                    .load(R.drawable.empty_cart)
                    .into(binding.ivEmptyCart);
        } else {
            binding.cardTotal.setVisibility(View.VISIBLE);
            binding.tvTotal.setVisibility(View.VISIBLE);
            binding.proceedBtn.setVisibility(View.VISIBLE);
            binding.rcCart.setVisibility(View.VISIBLE);
            binding.ivEmptyCart.setVisibility(View.GONE);
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
        if(allLottery){
            binding.walletBalance.setVisibility(View.GONE);
            binding.total.setText(responsee.totals.currency_code+" "+responsee.totals.getTotal_price());
            binding.subTotal.setText(responsee.totals.currency_code+" "+responsee.totals.getTotal_items());
            binding.shipping.setText(responsee.totals.total_shipping);
            binding.discount.setText(responsee.totals.total_discount);
        }else{
            binding.walletBalance.setVisibility(View.VISIBLE);
            binding.walletBalance.setText("Wallet Balance :"+" "+"Vouchers "+pref.getPrefString(requireActivity(),pref.main_balance));
            binding.total.setText(responsee.totals.currency_symbol+" "+responsee.totals.getTotal_price());
            binding.subTotal.setText(responsee.totals.currency_symbol+" "+responsee.totals.getTotal_items());
            binding.shipping.setText(responsee.totals.total_shipping);
            binding.discount.setText(responsee.totals.total_discount);

            if(Integer.valueOf(pref.getPrefString(requireActivity(),pref.main_balance))<Integer.valueOf(totals.getTotal_price())){
                binding.proceedBtn.setVisibility(View.GONE);
            }else{
                binding.proceedBtn.setVisibility(View.VISIBLE);
            }
        }
    }





    @Override
    public void onItemDeleted(int position) {
        if (position >= 0 && position < itemList.size()) {
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

                    if (itemList.isEmpty()) { // Hide views if cart is empty
                        binding.cardTotal.setVisibility(View.GONE);
                        binding.tvTotal.setVisibility(View.GONE);
                    }

                    Toast.makeText(requireContext(), "Item removed", Toast.LENGTH_SHORT).show();
                    getCart();
                } else {
                    Toast.makeText(requireContext(), response != null ? response.message : "Unknown error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    public void onQuantityIncreased(int position) {
        if (position >= 0 && position < itemList.size()) {
            Items item = itemList.get(position);

            if (item.getQuantity() < item.getQuantity_limits().getMaximum()) { // Max limit check
                item.setQuantity(item.getQuantity() + 1);
                adapter.notifyItemChanged(position); // Update UI
            } else {
                Toast.makeText(requireContext(), "Maximum quantity reached", Toast.LENGTH_SHORT).show();
                return; // Exit function early to prevent API call
            }

            String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);
            String nonce = pref.getPrefString(requireActivity(), pref.nonce);

            AddItems add = new AddItems();
            add.setKey(item.getKey());
            add.setQuantity(item.getQuantity()); // Send updated quantity

            fragmentsViewModel.UpdateItem(auth, nonce, add).observe(getViewLifecycleOwner(), response -> {
                if (response != null && response.isSuccess && response.data != null) {
                    responsee = response.data;
                    itemList.set(position, item); // Update the item in the list
                    adapter.notifyItemChanged(position); // Refresh UI for that item
                    Toast.makeText(requireContext(), "Quantity updated", Toast.LENGTH_SHORT).show();
                    getCart(); // Fetch the updated cart
                } else {
                    Toast.makeText(requireContext(), response != null ? response.message : "Unknown error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onQuantityDecreased(int position) {
        if (position >= 0 && position < itemList.size()) {
            Items item = itemList.get(position);

            if (item.getQuantity() > item.getQuantity_limits().getMinimum()) { // Ensure it doesn't go below min limit
                item.setQuantity(item.getQuantity() - 1);
                adapter.notifyItemChanged(position); // Refresh UI
            } else {
                Toast.makeText(requireContext(), "Minimum quantity reached", Toast.LENGTH_SHORT).show();
                return; // Prevent unnecessary API call
            }

            String auth = "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);
            String nonce = pref.getPrefString(requireActivity(), pref.nonce);

            AddItems add = new AddItems();
            add.setKey(item.getKey());
            add.setQuantity(item.getQuantity()); // Send updated quantity

            fragmentsViewModel.UpdateItem(auth, nonce, add).observe(getViewLifecycleOwner(), response -> {
                if (response != null && response.isSuccess && response.data != null) {
                    responsee = response.data;
                    itemList.set(position, item); // Update item in the list
                    adapter.notifyItemChanged(position); // Refresh UI
                    Toast.makeText(requireContext(), "Quantity updated", Toast.LENGTH_SHORT).show();
                    getCart(); // Fetch the updated cart
                } else {
                    Toast.makeText(requireContext(), response != null ? response.message : "Unknown error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}
