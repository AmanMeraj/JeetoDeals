package com.deals.jeetodeals.Profile;

import android.content.Intent;
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

import com.deals.jeetodeals.ChangePassword.ActivityChangePassword;
import com.deals.jeetodeals.Fragments.FragmentsRepository;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.databinding.ActivityProfileBinding;

public class ActivityProfile extends Utility {
ActivityProfileBinding binding;
ProfileResponse responsee;
ProfileViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityProfileBinding.inflate(getLayoutInflater());
        viewModel= new ViewModelProvider(this).get(ProfileViewModel.class);
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
        binding.cardChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(ActivityProfile.this, ActivityChangePassword.class);
                startActivity(intent);
            }
        });
        if (isInternetConnected(this)){
            getProfile();
        }else {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }
    public  void getProfile(){
        binding.loader.rlLoader.setVisibility(View.VISIBLE);
        String auth = "Bearer " + pref.getPrefString(this, pref.user_token); // Replace with actual token
        viewModel.getProfile(auth).observe(this, response -> {
            binding.loader.rlLoader.setVisibility(View.GONE);
            if (response != null && response.isSuccess && response.data != null) {
                responsee = response.data;
                binding.numberTv.setText(responsee.phone_number);
                binding.emailTv.setText(responsee.email);
                binding.edtFirstName.setText(responsee.first_name);
                binding.edtLastName.setText(responsee.last_name);
                binding.edtEamilAddress.setText(responsee.email);
                binding.nameTv.setText(responsee.first_name+" "+responsee.last_name);

            } else {
                Toast.makeText(this, response != null ? response.message : "Unknown error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}