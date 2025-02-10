package com.deals.jeetodeals.ForgotPassEmail;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.deals.jeetodeals.Model.User;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.SignInScreen.ForgotPassResponse;
import com.deals.jeetodeals.SignInScreen.SiginRepository;
import com.deals.jeetodeals.SignInScreen.SigninViewModel;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.databinding.ActivityForgotPassEmailBinding;

public class ActivityForgotPassEmail extends Utility {
    ActivityForgotPassEmailBinding binding;
    SigninViewModel viewModel;
    SiginRepository.ApiResponse<ForgotPassResponse> responsee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityForgotPassEmailBinding.inflate(getLayoutInflater());
        viewModel= new ViewModelProvider(this).get(SigninViewModel.class);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInternetConnected(ActivityForgotPassEmail.this)){
                    validateAndUpdatePassword();
                }else {
                    Toast.makeText(ActivityForgotPassEmail.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void forgotPassword(String username) {
        // Show loader while making the network request
        binding.loader.rlLoader.setVisibility(View.VISIBLE);

        // Create User object for forgot password
        User userRequest = new User();
        userRequest.setEmail(username);

        // Observe LiveData from ViewModel
        viewModel.forgotPassword(userRequest).observe(this, apiResponse -> {
            // Hide loader after receiving response
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (apiResponse != null) {
                if (apiResponse.isSuccess) {
                    responsee=apiResponse;

                    Toast.makeText(this, ""+apiResponse.message, Toast.LENGTH_SHORT).show();
                    new android.os.Handler().postDelayed(() -> finish(), 3000);
                } else {
                    // Display the error message from the API response
                    Toast.makeText(this, apiResponse.message, Toast.LENGTH_SHORT).show();
                }
            } else {
                // Null response or network failure
                Toast.makeText(this, "Forgot password request failed! Please check your network connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validateAndUpdatePassword() {
        String userEmail = binding.edtUser.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail)) {
            binding.textFieldEmail.setError(getString(R.string.error_email_required));
        } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            binding.textFieldEmail.setError(getString(R.string.error_invalid_email));
        } else {
            forgotPassword(userEmail);
        }
    }
}