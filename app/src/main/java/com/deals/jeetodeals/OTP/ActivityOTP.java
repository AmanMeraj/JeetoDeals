package com.deals.jeetodeals.OTP;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.deals.jeetodeals.R;
import com.deals.jeetodeals.SignInScreen.SignInActivity;
import com.deals.jeetodeals.SignupScreen.SignUpActivity;
import com.deals.jeetodeals.databinding.ActivityOtpBinding;

public class ActivityOTP extends AppCompatActivity {

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

        // Create OTP object
        Otp otpclass = new Otp();
        otpclass.setOtp(otp);
        otpclass.setEmail(email);

        // Call the OTP verification API
        viewModel.postOtp(otpclass).observe(this, response -> {
            // Hide loader
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response != null) {
                if (response.isSuccess && response.data != null) {
                    String message = response.message; // Response message from ApiResponse
                    String code = response.data.getCode(); // Assuming OtpResponse has a `getCode()`

                    if ("User registered successfully.".equalsIgnoreCase(message)) {
                        showToast(message);
                        navigateToSignIn(); // Navigate to sign-in screen
                    } else if ("invalid_otp".equalsIgnoreCase(code)) {
                        showToast("Invalid OTP. Please try again.");
                    } else {
                        showToast(message != null ? message : "An unexpected error occurred.");
                    }
                } else {
                    // API failure case
                    showToast(response.message != null ? response.message : "Verification failed! Please try again later.");
                }
            } else {
                showToast("Verification failed! Please try again later.");
            }
        });
    }


    // Helper method to show Toast messages
    private void showToast(String message) {
        Toast.makeText(ActivityOTP.this, message, Toast.LENGTH_SHORT).show();
    }

    // Helper method to navigate to SignInActivity
    private void navigateToSignIn() {
        Intent intent = new Intent(ActivityOTP.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }



}