package com.deals.jeetodeals.OTP;

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

import com.deals.jeetodeals.ContainerActivity.ContainerActivity;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.SignInScreen.SignInActivity;
import com.deals.jeetodeals.SignupScreen.SignUpActivity;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.databinding.ActivityOtpBinding;
import com.google.gson.Gson;

public class ActivityOTP extends Utility {

    ActivityOtpBinding binding;
    OtpViewModel viewModel;
    OtpResponse responsee;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityOtpBinding.inflate(getLayoutInflater());
        viewModel= new ViewModelProvider(this).get(OtpViewModel.class);
        setContentView(binding.getRoot());
        email = getIntent().getStringExtra("email");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.otpButton.setOnClickListener( v -> validateAndVerifyOtp());
        binding.cardRegisterNow.setOnClickListener( v -> {
            Intent i = new Intent(ActivityOTP.this, SignUpActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void validateAndVerifyOtp() {
        // Retrieve the OTP from the OtpTextView
        String otp = binding.otpView.getOTP();

        // Check if OTP is empty or not complete
        if (otp == null || otp.isEmpty() || otp.length() < 6) {
            Toast.makeText(this, "Please enter a valid 6-digit OTP.", Toast.LENGTH_SHORT).show();
            return;
        }

        // If OTP is valid, call the verifyOtp() method
        if(email!=null){
            verifyOtp(otp,email);
        }else{
            Toast.makeText(this, "Email is null", Toast.LENGTH_SHORT).show();
        }

    }

    private void verifyOtp(String otp, String email) {
        // Show loader
        binding.loader.rlLoader.setVisibility(View.VISIBLE);

        // Log input values
        Log.d("OTP_DEBUG", "Verifying OTP: " + otp + " for email: " + email);

        // Create OTP object
        Otp otpclass = new Otp();
        otpclass.setOtp(otp);
        otpclass.setEmail(email);

        // Call the OTP verification API
        viewModel.postOtp(otpclass).observe(this, response -> {
            // Hide loader
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response != null) {
                Log.d("OTP_DEBUG", "OTP API Response: " + new Gson().toJson(response));

                // Check if response is successful and contains required fields
                if (response.isSuccess) {
                    responsee=response.data;
                    Log.d("OTP_DEBUG", "User registration successful."+response.data.message);
                    if (response.data != null && "User registered successfully.".equalsIgnoreCase(response.data.message)) {
                        Log.d("OTP_DEBUG", "User registration successful.");

                        String token = response.data.getToken();
                        if (token != null && !token.isEmpty()) {
                            // Store token and login status
                            pref.setPrefString(ActivityOTP.this, pref.user_token, token);
                            pref.setPrefBoolean(ActivityOTP.this, pref.login_status, true);
                            pref.setPrefString(this,pref.first_name,response.data.getFirst_name());
                            pref.setPrefString(this,pref.last_name,response.data.getLast_name());
                            pref.setPrefString(this,pref.mobile,response.data.getPhone());
                            pref.setPrefString(this,pref.user_email,response.data.getUser_email());
                            pref.setPrefString(this,pref.user_name,response.data.getUser_display_name());

                            Log.d("OTP_DEBUG", "Token saved: " + token);
                            navigateToContainer();
                        } else {
                            Log.e("OTP_DEBUG", "Token is missing! Login failed.");
                            showToast("Token is missing! Login failed.");
                        }
                    } else if (response.data != null && "invalid_otp".equalsIgnoreCase(response.data.getCode())) {
                        Log.e("OTP_DEBUG", "Invalid OTP entered.");
                        showToast("Invalid OTP. Please try again.");
                    } else {
                        Log.e("OTP_DEBUG", "Unexpected response: " + response.message);
                        showToast(response.message != null ? response.message : "An unexpected error occurred.");
                    }
                } else {
                    Log.e("OTP_DEBUG", "API failure. Response: " + new Gson().toJson(response));
                    showToast(response.message != null ? response.message : "Verification failed! Please try again later.");
                }
            } else {
                Log.e("OTP_DEBUG", "API response is null.");
                showToast("Verification failed! Please try again later.");
            }
        });
    }



    // Navigate to the main container activity
    private void navigateToContainer() {
        Intent intent = new Intent(ActivityOTP.this, ContainerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears back stack
        startActivity(intent);
        finish();
    }



    // Helper method to show Toast messages
    private void showToast(String message) {
        Toast.makeText(ActivityOTP.this, message, Toast.LENGTH_SHORT).show();
    }



}