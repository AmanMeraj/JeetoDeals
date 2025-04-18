package com.deals.jeetodeals.MyOrders;

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
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.databinding.FargmentMyOrdersBinding;

import java.util.ArrayList;

public class FragmentMyOrders extends Fragment {

    private FargmentMyOrdersBinding binding;
    private MyOrderViewModel viewModel;

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

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        if (utility.isInternetConnected(requireContext())) {
            getOrders();
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
}
