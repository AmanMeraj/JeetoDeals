package com.deals.jeetodeals.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deals.jeetodeals.Adapters.AdapterCart;
import com.deals.jeetodeals.ChangeAddress.ActivityChangeAddress;
import com.deals.jeetodeals.Model.Cart;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.databinding.FragmentCartBinding;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

   FragmentCartBinding binding;
    private AdapterCart adapter;
    private List<Cart> itemList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentCartBinding.inflate(getLayoutInflater());
        // Inflate the layout for this fragment
        binding=FragmentCartBinding.inflate(getLayoutInflater());

        itemList = new ArrayList<>();
        itemList.add(new Cart("MacBook Pro M2", "Voucher 7,500", "Win MacBook Pro 16-inch (M2)", 5));
        itemList.add(new Cart("iPhone 15", "Voucher 5,000", "Win iPhone 15 Pro Max", 2));
        itemList.add(new Cart("AirPods Pro", "Voucher 1,500", "Win AirPods Pro (2nd Gen)", 3));

        // Set Adapter
        adapter = new AdapterCart(requireActivity(), itemList);
        binding.rcCart.setAdapter(adapter);
        binding.changeAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(requireActivity(), ActivityChangeAddress.class);
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }
}