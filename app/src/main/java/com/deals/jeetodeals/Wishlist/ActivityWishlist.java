package com.deals.jeetodeals.Wishlist;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.deals.jeetodeals.Adapters.WishlistAdapter;
import com.deals.jeetodeals.Model.Wishlist;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.databinding.ActivityWishlistBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityWishlist extends Utility {
    ActivityWishlistBinding binding;
    private WishlistAdapter adapter;
    WishlistViewModel viewModel;
    ArrayList<WishlistResponse> responses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityWishlistBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        viewModel= new ViewModelProvider(this).get(WishlistViewModel.class);
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


        if(isInternetConnected(this)){
            getWishlist();
        }else {
            Toast.makeText(this, "No Internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void getWishlist() {
        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);
        viewModel.getWishlist(auth).observe(this, response -> {
            if (response != null && response.isSuccess && response.data != null) {
                responses = response.data;
                Log.d("WishlistActivity", "Wishlist Size: " + responses.size()); // Log the size
                setupRecyclerView(responses);
            } else {
                Log.e("WishlistActivity", "Error: " + (response != null ? response.message : "Unknown error"));
                Toast.makeText(this, response != null ? response.message : "Unknown error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupRecyclerView(ArrayList<WishlistResponse> responses) {
        adapter = new WishlistAdapter(this, responses, new WishlistAdapter.OnWishlistItemClickListener() {
            @Override
            public void onDeleteClick(WishlistResponse item, int position) {
                deleteWishlistItem(item, position);
            }
        });
        binding.rcWishlist.setAdapter(adapter);
    }

    private void deleteWishlistItem(WishlistResponse item, int position) {
        if (item == null || item.product_id <= 0) {
            Toast.makeText(this, "Invalid product ID", Toast.LENGTH_SHORT).show();
            return;
        }

        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);

        // Add debug log
        Log.d("WishlistActivity", "Sending delete request for product ID: " + item.product_id);

        Wishlist wishlist= new Wishlist();
        wishlist.setProduct_id(item.getProduct_id());

        viewModel.deleteWishlist(auth,wishlist).observe(this, response -> {
            if (response != null) {
                if (response.isSuccess && response.data != null) {
                    // Successfully deleted - update the UI
                    adapter.removeItem(position);
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show();
                } else {
                    // Show error message
                    String errorMessage = response.message != null ? response.message : "Failed to delete item";
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("WishlistActivity", "Error: " + errorMessage);
                }
            } else {
                Toast.makeText(this, "Unknown error occurred", Toast.LENGTH_SHORT).show();
                Log.e("WishlistActivity", "Error: Null response");
            }
        });
    }

}