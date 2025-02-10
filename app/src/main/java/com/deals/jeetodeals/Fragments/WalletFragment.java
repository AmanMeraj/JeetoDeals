package com.deals.jeetodeals.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.deals.jeetodeals.Adapters.TransactionAdapter;
import com.deals.jeetodeals.ContainerActivity.ContainerActivity;
import com.deals.jeetodeals.Model.Login;
import com.deals.jeetodeals.Model.Transaction;
import com.deals.jeetodeals.Model.WalletResponse;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.SignInScreen.SignInActivity;
import com.deals.jeetodeals.Utils.SharedPref;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.databinding.FragmentWalletBinding;

import java.util.ArrayList;
import java.util.List;


public class WalletFragment extends Fragment {
    FragmentWalletBinding binding;
    SharedPref pref= new SharedPref();
    Utility utility= new Utility();
    FragmentsViewModel viewModel;
    ArrayList<WalletResponse> responsee;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentWalletBinding.inflate(getLayoutInflater());
        viewModel= new ViewModelProvider(this).get(FragmentsViewModel.class);
        if(utility.isInternetConnected(requireActivity())){
            getBalance();
            getWalletRcData();
        }else{
            Toast.makeText(requireActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
        return binding.getRoot();
    }

    public void getBalance(){
        String auth= "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);
        binding.loader.rlLoader.setVisibility(View.VISIBLE);
        viewModel.getCurrentBalance(auth).observe(getViewLifecycleOwner(),apiResponse->{
            binding.loader.rlLoader.setVisibility(View.GONE);
            if (apiResponse != null) {
                if (apiResponse.isSuccess) {
                   binding.balance.setText("Vouchers "+apiResponse.data);
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

    public void getWalletRcData(){
        String auth= "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);
        binding.loader.rlLoader.setVisibility(View.VISIBLE);
        viewModel.GetWallet(auth).observe(getViewLifecycleOwner(),apiResponse->{
            binding.loader.rlLoader.setVisibility(View.GONE);
            if (apiResponse != null) {
                if (apiResponse.isSuccess) {
                    // Successful login
                    responsee = apiResponse.data;
                    populateRc(responsee);

                    // Save token and navigate to the next activity

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

    private void populateRc(ArrayList<WalletResponse> responsee) {
        if (responsee == null || responsee.isEmpty()) {
            // If the response is empty or null, hide the RecyclerView and show another view
            binding.rcTransaction.setVisibility(View.GONE);
            binding.cardNoData.setVisibility(View.VISIBLE);  // Assuming noDataView is the view you want to show
        } else {
            // If there is data, show the RecyclerView and hide the other view
            binding.rcTransaction.setVisibility(View.VISIBLE);
            binding.cardNoData.setVisibility(View.GONE);

            // Set the adapter to RecyclerView
            TransactionAdapter adapter = new TransactionAdapter(requireActivity(), responsee);
            binding.rcTransaction.setAdapter(adapter);
        }
    }

}