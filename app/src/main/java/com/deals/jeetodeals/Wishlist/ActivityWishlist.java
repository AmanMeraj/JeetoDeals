package com.deals.jeetodeals.Wishlist;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.deals.jeetodeals.Adapters.WishlistAdapter;
import com.deals.jeetodeals.Model.Wishlist;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.databinding.ActivityWishlistBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityWishlist extends AppCompatActivity {
    ActivityWishlistBinding binding;
    private WishlistAdapter adapter;
    private List<Wishlist> wishlistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityWishlistBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
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

        wishlistList = new ArrayList<>();

        // Populate your wishlistList with data
        wishlistList.add(new Wishlist("In Stock", "Description here", "60 Vouchers", "Add to Cart", R.drawable.promotion_image, R.drawable.promotion_image));
        wishlistList.add(new Wishlist("In Stock", "Description here will be in the center", "40 Vouchers", "Add to Cart", R.drawable.promotion_image, R.drawable.promotion_image));

        adapter = new WishlistAdapter(this, wishlistList);
        binding.rcWishlist.setAdapter(adapter);
    }
}