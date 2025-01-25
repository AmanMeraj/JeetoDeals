package com.deals.jeetodeals.MyOrders;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.deals.jeetodeals.Adapters.AdapterOrders;
import com.deals.jeetodeals.Model.Orders;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.databinding.ActivityMyOrdersBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityMyOrders extends AppCompatActivity {
ActivityMyOrdersBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityMyOrdersBinding.inflate(getLayoutInflater());
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

        List<Orders> items = new ArrayList<>();
        items.add(new Orders("#786", "processing", "Pack Of_5 Mobile Erasers",
                "Voucher 5 for 1 item", "Date: November 22, 2024",
                "https://via.placeholder.com/100")); // Replace URL with actual image URLs
        items.add(new Orders("#123", "completed", "Set Of_10 Pencils",
                "Voucher 2 for 10 items", "Date: December 15, 2024",
                "https://via.placeholder.com/100")); // Example items

        // Set Adapter
        AdapterOrders adapter = new AdapterOrders(this, items);
        binding.rcOrders.setAdapter(adapter);
    }
}