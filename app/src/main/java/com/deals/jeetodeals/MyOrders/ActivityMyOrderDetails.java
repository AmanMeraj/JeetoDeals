package com.deals.jeetodeals.MyOrders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.deals.jeetodeals.Adapters.TrackingAdapter;
import com.deals.jeetodeals.Fragments.FragmentsViewModel;
import com.deals.jeetodeals.Model.OrderDetailsResponse;
import com.deals.jeetodeals.Model.TrackingResponse;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.databinding.ActivityMyOrderDetailsBinding;

import java.util.ArrayList;

public class ActivityMyOrderDetails extends Utility {
    ActivityMyOrderDetailsBinding binding;
    int orderId;
    OrderDetailsResponse responsee;
    String date="";
    FragmentsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        try {
            binding = ActivityMyOrderDetailsBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing UI", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            viewModel = new ViewModelProvider(this).get(FragmentsViewModel.class);
        } catch (Exception e) {
            // Handle viewModel initialization error
        }

        try {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        } catch (Exception e) {
            // Handle window insets error
        }

        // Get order ID from intent
        try {
            orderId = getIntent().getIntExtra("order_id", 0);
            date=getIntent().getStringExtra("formatted_date");
        } catch (Exception e) {
            // Handle error getting order ID
        }

        // Check and proceed with order details fetch
        if (orderId != 0) {
            if (isInternetConnected(this)) {
                getOrderDetails();
            } else {
                Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No order ID provided", Toast.LENGTH_SHORT).show();
            finish();
        }

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getOrderDetails() {
        Log.d("OrderDetails", "Starting to fetch order details");
        showLoader(true);

        String authToken;
        try {
            authToken = getAuthorizationToken();
            Log.d("OrderDetails", "Auth token retrieved successfully");
        } catch (Exception e) {
            Log.e("OrderDetails", "Failed to get auth token", e);
            showLoader(false);
            Toast.makeText(this, "Authentication error", Toast.LENGTH_SHORT).show();
            return;
        }

        int orderIntId;
        try {
            orderIntId = orderId;
            Log.d("OrderDetails", "Processing order ID: " + orderIntId);
        } catch (NumberFormatException e) {
            Log.e("OrderDetails", "Invalid order ID format: " + orderId, e);
            Toast.makeText(this, "Invalid Order ID", Toast.LENGTH_SHORT).show();
            showLoader(false);
            return;
        }

        try {
            Log.d("OrderDetails", "Making API call to get order details for order ID: " + orderIntId);
            viewModel.getOrderDetails(authToken, orderIntId).observe(this, response -> {
                showLoader(false);
                Log.d("OrderDetails", "Received response from API");

                if (response == null) {
                    Log.e("OrderDetails", "Response is null");
                    Toast.makeText(this, "Failed to load order details", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("OrderDetails", "Response success status: " + response.isSuccess);
                Log.d("OrderDetails", "Response message: " + response.message);

                if (response.data != null) {
                    Log.d("OrderDetails", "Response data is not null");
                } else {
                    Log.e("OrderDetails", "Response data is null");
                }

                if (response.isSuccess && response.data != null) {
                    responsee = response.data;
                    Log.d("OrderDetails", "Order details retrieved successfully");

                    // Log details about the order data to help diagnose issues
                    try {
                        // Add logging based on your response structure - modify this according to your model
                        if (responsee != null) {

                            // Log first few items for debugging
                            int itemsToLog = Math.min(responsee.getItems().size(), 3);
                            for (int i = 0; i < itemsToLog; i++) {
                                // Adapt this based on your actual data structure
                                Log.d("OrderDetails", "Item " + i + " details: " +
                                        responsee.getItems().get(i).toString());
                            }
                        } else {
                            Log.e("OrderDetails", "Order items list is null");
                        }
                    } catch (Exception e) {
                        Log.e("OrderDetails", "Error when logging order details", e);
                    }

                    // Update UI on main thread to be safe
                    runOnUiThread(() -> {
                        try {
                            Log.d("OrderDetails", "Updating UI with order details");
                            updateUI(responsee);
                            Log.d("OrderDetails", "UI updated successfully");

                            // After successfully loading order details, fetch tracking information
                            Log.d("OrderDetails", "Now fetching tracking information");
                            getTrackingInfo(authToken, orderIntId);
                        } catch (Exception e) {
                            Log.e("OrderDetails", "Error updating UI", e);
                            e.printStackTrace();
                        }
                    });
                } else {
                    String msg = response.message != null ? response.message : "Failed to load order details";
                    Log.e("OrderDetails", "Failed to get order details: " + msg);
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("OrderDetails", "Exception during API call", e);
            showLoader(false);
            Toast.makeText(this, "Error loading order details", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Fetch tracking information for the current order
     */
    private void getTrackingInfo(String authToken, int orderId) {
        try {
            // Use the hardcoded order ID that you know has data


            viewModel.getTrackingDetails(authToken, orderId).observe(this, response -> {
                // Add detailed logging to debug the response
                if (response != null) {
                    Log.d("OrderDetails", "Response received: isSuccess=" + response.isSuccess);

                    if (response.data != null) {
                        Log.d("OrderDetails", "Response data is not null");

                        if (response.data.data != null) {
                            Log.d("OrderDetails", "Tracking data list is not null, size: " +
                                    response.data.data.size());
                        } else {
                            Log.d("OrderDetails", "Tracking data list is null");
                        }
                    } else {
                        Log.d("OrderDetails", "Response data is null");
                    }
                } else {
                    Log.d("OrderDetails", "Response is null");
                }

                if (response != null && response.isSuccess &&
                        response.data != null && response.data.data != null &&
                        !response.data.data.isEmpty()) {

                    // Data exists, show tracking section and set up RecyclerView
                    Log.d("OrderDetails", "Setting tracking view to VISIBLE");
                    binding.relTracking.setVisibility(View.VISIBLE);
                    setupTrackingRecyclerView(response.data.data);
                } else {
                    // No tracking data, hide the tracking section
                    Log.d("OrderDetails", "Setting tracking view to GONE");
                    binding.relTracking.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            // Handle error, hide tracking section
            binding.relTracking.setVisibility(View.GONE);
            Log.e("OrderDetails", "Error fetching tracking info", e);
        }
    }

    /**
     * Set up the tracking RecyclerView with data
     */
    private void setupTrackingRecyclerView(ArrayList<TrackingResponse.Data> trackingList) {
        try {
            // Create and set adapter - no need for click listener since it's handled in the adapter
            TrackingAdapter adapter = new TrackingAdapter(this, trackingList);

            // Set the adapter
            binding.rcTracking.setAdapter(adapter);
        } catch (Exception e) {
            Log.e("OrderDetails", "Error setting up tracking recycler view", e);
        }
    }

    private void updateUI(OrderDetailsResponse data) {
        if (data == null) {
            return;
        }

        try {
            String status = data.getStatus();
            String orderStatusText = "Order #" + orderId + " was placed on " + date +
                    " and is currently " + status + ".";
            binding.tvOrderStatus.setText(orderStatusText);
        } catch (Exception e) {
            // Handle error
        }

        try {
            String name =data.getItems().get(0).getName();
            binding.tvProductName.setText(name);
        }catch (Exception e){
            //handle error
        }
        try {
            String total =data.getItems().get(0).getPrices().getPrice();
            binding.tvTotal.setText(total+ " Vouchers");
        }catch (Exception e){
            //handle error
        }

        try {
            if (data.getItems() != null && !data.getItems().isEmpty() && data.getItems().get(0).getImages() != null && !data.getItems().get(0).getImages().isEmpty()) {

                // Get the thumbnail URL from the first item's first image
                String thumbnailUrl = data.getItems().get(0).getImages().get(0).getThumbnail();

                // Use your preferred image loading library (Glide, Picasso, etc.)
                // Example with Glide:
                Glide.with(this)
                        .load(thumbnailUrl)
                        .placeholder(R.drawable.no_image)
                        .error(R.drawable.error_image)
                        .into(binding.ivProductImage);
            }
        } catch (Exception e) {
            // Handle error
            Log.e("OrderDetails", "Error loading thumbnail image", e);
        }

        try {
            if (data.getItems() != null) {
                String totalPrice = data.getItems().get(0).getPrices().getPrice();
                binding.tvProductPrice.setText(totalPrice + " Vouchers");
            }
        } catch (Exception e) {
            // Handle error
        }
        try {
            if (data.getTotals() != null) {
                int subTotalPrice = Integer.parseInt(data.getItems().get(0).getTotals().getLine_subtotal());
                binding.tvSubTotal.setText(subTotalPrice + " Vouchers");
            }
        } catch (Exception e) {
            // Handle error
        }

        try {
            if (data.getBilling_address() != null && binding.textBillingAddress != null) {
                StringBuilder billingAddressBuilder = new StringBuilder();
                billingAddressBuilder.append(data.getBilling_address().getFirstName())
                        .append(" ")
                        .append(data.getBilling_address().getLastName())
                        .append("\n")
                        .append(data.getBilling_address().getAddress1());

                if (data.getBilling_address().getAddress2() != null && !data.getBilling_address().getAddress2().isEmpty()) {
                    billingAddressBuilder.append("\n").append(data.getBilling_address().getAddress2());
                }

                billingAddressBuilder.append("\n")
                        .append(data.getBilling_address().getCity())
                        .append(" ")
                        .append(data.getBilling_address().getPostcode())
                        .append("\n")
                        .append(data.getBilling_address().getState())
                        .append("\n")
                        .append(data.getBilling_address().getPhone());

                binding.textBillingAddress.setText(billingAddressBuilder.toString());
            }
        } catch (Exception e) {
            // Handle error
        }


                try {
            if (data.getShipping_address() != null && binding.textShippingAddress != null) {
                StringBuilder shippingAddressBuilder = new StringBuilder();
                shippingAddressBuilder.append(data.getShipping_address().getFirstName())
                        .append(" ")
                        .append(data.getShipping_address().getLastName())
                        .append("\n")
                        .append(data.getShipping_address().getAddress1());

                if (data.getShipping_address().getAddress2() != null && !data.getShipping_address().getAddress2().isEmpty()) {
                    shippingAddressBuilder.append("\n").append(data.getShipping_address().getAddress2());
                }

                shippingAddressBuilder.append("\n")
                        .append(data.getShipping_address().getCity())
                        .append(" ")
                        .append(data.getShipping_address().getPostcode())
                        .append("\n")
                        .append(data.getShipping_address().getState())
                        .append("\n")
                        .append(data.getShipping_address().getPhone());

                binding.textShippingAddress.setText(shippingAddressBuilder.toString());
            }
        } catch (Exception e) {
            // Handle error
        }

        // Update other UI elements here
    }

    private void showLoader(boolean show) {
        try {
            if (binding != null && binding.loader != null && binding.loader.rlLoader != null) {
                binding.loader.rlLoader.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        } catch (Exception e) {
            // Handle error
        }
    }

    private String getAuthorizationToken() {
        try {
            return "Bearer " + pref.getPrefString(this, pref.user_token);
        } catch (Exception e) {
            return null;
        }
    }
}