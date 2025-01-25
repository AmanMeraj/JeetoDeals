package com.deals.jeetodeals.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deals.jeetodeals.Adapters.TransactionAdapter;
import com.deals.jeetodeals.Model.Transaction;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.databinding.FragmentWalletBinding;

import java.util.ArrayList;
import java.util.List;


public class WalletFragment extends Fragment {
    FragmentWalletBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentWalletBinding.inflate(getLayoutInflater());

        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(new Transaction(
                "Simple Dummy Text",
                "It is a long established fact that a reader will be distracted by the...",
                "500",
                "Accept",
                R.drawable.logo_jd,
                R.drawable.image_green_bg
        ));
        transactionList.add(new Transaction(
                "Another Transaction",
                "Lorem Ipsum is simply dummy text of the printing industry...",
                "1200",
                "Pending",
                R.drawable.logo_jd,
                R.drawable.image_green_bg
        ));

        // Set up RecyclerView
        TransactionAdapter adapter = new TransactionAdapter(requireActivity(), transactionList);
        binding.rcTransaction.setAdapter(adapter);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}