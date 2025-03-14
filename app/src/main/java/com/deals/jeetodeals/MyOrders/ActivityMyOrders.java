package com.deals.jeetodeals.MyOrders;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.deals.jeetodeals.Adapters.AdapterOrders;
import com.deals.jeetodeals.Model.Orders;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.databinding.ActivityMyOrdersBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityMyOrders extends Utility {
ActivityMyOrdersBinding binding;
MyOrderViewModel viewModel;
ArrayList<MyOrdersResponse> responsee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityMyOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel= new ViewModelProvider(this).get(MyOrderViewModel.class);
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
            getOrders();
        }else{
            Toast.makeText(this, "No Internet Connection !", Toast.LENGTH_SHORT).show();
        }


        // Set Adapter

    }

    private void getOrders() {
        binding.loader.rlLoader.setVisibility(View.VISIBLE);
        String auth = "Bearer "+pref.getPrefString(this,pref.user_token);
        viewModel.getOrders(auth).observe(this,response->{
            if(response!=null){
                if(response.isSuccess && response.data!=null){
                    binding.loader.rlLoader.setVisibility(View.GONE);
                    responsee=response.data;
                    setUpRecyclerView(responsee);

                }else{
                    binding.loader.rlLoader.setVisibility(View.GONE);
                    Toast.makeText(this, ""+response.message, Toast.LENGTH_SHORT).show();
                }
            }else{
                binding.loader.rlLoader.setVisibility(View.GONE);
                Toast.makeText(this, ""+response.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpRecyclerView(ArrayList<MyOrdersResponse> responsee) {
        if (responsee == null || responsee.isEmpty()) {
            // Hide RecyclerView and Show "No Items" View
            binding.rcOrders.setVisibility(View.GONE);
            binding.noItem.setVisibility(View.VISIBLE);  // Ensure you have this view in XML
        } else {
            // Show RecyclerView and Hide "No Items" View
            binding.rcOrders.setVisibility(View.VISIBLE);
            binding.noItem.setVisibility(View.GONE);

            AdapterOrders adapter = new AdapterOrders(this, responsee);
            binding.rcOrders.setAdapter(adapter);
        }
    }

}