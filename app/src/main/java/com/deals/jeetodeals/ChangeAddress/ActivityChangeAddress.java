package com.deals.jeetodeals.ChangeAddress;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.deals.jeetodeals.Model.BillingAddress;
import com.deals.jeetodeals.Model.ShippingAddress;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.databinding.ActivityChangeAddressBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityChangeAddress extends Utility {
ActivityChangeAddressBinding binding;
ChangeAddressViewModel viewModel;
ChangeAddressResponse responsee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityChangeAddressBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(ChangeAddressViewModel.class);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (isInternetConnected(this)){
            getAddress();
        }else{
            Toast.makeText(this, "No Internet Connection !", Toast.LENGTH_SHORT).show();
        }
        binding.editBilling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String edit = "billing";
                BillingAddress billing = responsee != null && responsee.getBillingAddress() != null
                        ? responsee.getBillingAddress()
                        : new BillingAddress(); // ✅ Send empty object if null

                Intent intent = new Intent(ActivityChangeAddress.this, ActivityChangeAddress2.class);
                intent.putExtra("edit", edit);
                intent.putExtra("BillingAddress", billing);
                startActivity(intent);
            }
        });

        binding.editShipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String edit = "shipping";
                ShippingAddress shipping = responsee != null && responsee.getShippingAddress() != null
                        ? responsee.getShippingAddress()
                        : new ShippingAddress(); // ✅ Send empty object if null

                Intent intent = new Intent(ActivityChangeAddress.this, ActivityChangeAddress2.class);
                intent.putExtra("edit", edit);
                intent.putExtra("ShippingAddress", shipping);
                startActivity(intent);
            }
        });

    }

    private void getAddress() {
        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);
        Log.d("TAG", "getAddress: "+auth);
        viewModel.GetAddress(auth).observe(this, response -> {
            if (response != null) {
                if (response.isSuccess) {
                    // Log the raw response for debugging
                    Log.d("API Response", "Raw Response: " + response.data);
                    responsee = response.data;
                    setBillingData(response);  // Pass response to the method
                    setShippingData(response);
                } else {
                    Log.d("TAG", "Error: " + response.message);
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Login failed! Please check your network connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setShippingData(ChangeAddressRepository.ApiResponse<ChangeAddressResponse> response) {
        ShippingAddress shipping = response.data.getShippingAddress();

        if (shipping != null) {
            List<String> addressParts = new ArrayList<>();

            if (!isNullOrEmpty(shipping.getAddress_1())) {
                addressParts.add(shipping.getAddress_1());
            }
            if (!isNullOrEmpty(shipping.getAddress_2())) {
                addressParts.add(shipping.getAddress_2());
            }
            if (!isNullOrEmpty(shipping.getCity())) {
                addressParts.add(shipping.getCity());
            }
            if (!isNullOrEmpty(shipping.getPostcode())) {
                addressParts.add(shipping.getPostcode());
            }
            if (!isNullOrEmpty(shipping.getCountry())) {
                addressParts.add(shipping.getCountry());
            }
            if (!isNullOrEmpty(shipping.getState())) {
                addressParts.add(shipping.getState());
            }

            if (!addressParts.isEmpty()) {
                // Join all non-empty parts with a comma
                String shippingAddress = TextUtils.join(", ", addressParts);
                binding.textShippingAddress.setText(shippingAddress);
            } else {
                binding.textShippingAddress.setText("Update your shipping address by clicking on the Edit button");
            }

            // Handling Name
            List<String> nameParts = new ArrayList<>();
            if (!isNullOrEmpty(shipping.getFirst_name())) {
                nameParts.add(shipping.getFirst_name());
            }
            if (!isNullOrEmpty(shipping.getLast_name())) {
                nameParts.add(shipping.getLast_name());
            }

            if (!nameParts.isEmpty()) {
                binding.textName2.setText(TextUtils.join(" ", nameParts));
            } else {
                binding.textName2.setText("No Name Provided");
            }
        } else {
            binding.textShippingAddress.setText("Update your shipping address by clicking on the Edit button");
        }
    }

    // Utility method to check if a string is null or empty
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }


    private void setBillingData(ChangeAddressRepository.ApiResponse<ChangeAddressResponse> response) {
        BillingAddress billing = response.data.getBillingAddress();

        if (billing != null) {
            // Log to verify data in BillingAddress
            Log.d("Billing Address", "Billing Address: " + billing.getAddress_1() + ", " + billing.getCity());

            // Concatenate billing address fields
            String billingAddress = billing.getAddress_1() + " "
                    + billing.getAddress_2() + ", "
                    + billing.getCity() + ", "
                    + billing.getPostcode() + ", "
                    + billing.getCountry() + ", "
                    + billing.getState();

            // Set the billing address and name data in the views
            binding.textBillingAddress.setText(billingAddress);
            binding.textName.setText(billing.getFirst_name() + " " + billing.getLast_name());
        } else {
            Log.d("Billing Address", "Billing Address is null");
            binding.textShippingAddress.setText("Update your Billing Address by clicking on the Edit button");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isInternetConnected(this)){
            getAddress();
        }else{
            Toast.makeText(this, "No Internet Connection.", Toast.LENGTH_SHORT).show();
        }
    }

}