package com.deals.jeetodeals.Wishlist;

import android.os.Bundle;
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
        }
    }

    private void getWishlist() {
        String auth= "Bearer "+pref.getPrefString(this,pref.user_token);
        viewModel.getWishlist(auth).observe(this,response->{
            if(response!=null && response.isSuccess && response.data!=null){
                responses=response.data;
                setupRecyclerView(responses);

            }else {
                Toast.makeText(this, response != null ? response.message : "Unknown error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setupRecyclerView(ArrayList<WishlistResponse> responses) {
        adapter = new WishlistAdapter(this, responses);
        binding.rcWishlist.setAdapter(adapter);
    }
}