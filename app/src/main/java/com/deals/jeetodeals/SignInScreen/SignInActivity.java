package com.deals.jeetodeals.SignInScreen;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.deals.jeetodeals.ContainerActivity.ContainerActivity;
import com.deals.jeetodeals.ForgotPassEmail.ActivityForgotPassEmail;
import com.deals.jeetodeals.ForgotPasswordScreen.ForgotPasswordActivity;
import com.deals.jeetodeals.Model.Login;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.SignupScreen.SignUpActivity;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.databinding.ActivitySignInBinding;

public class SignInActivity extends Utility {


    ActivitySignInBinding binding;
    SigninResponse responsee;
    SigninViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignInBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        viewModel= new ViewModelProvider(this).get(SigninViewModel.class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(SignInActivity.this, ActivityForgotPassEmail.class);
                startActivity(intent);
            }
        });


        binding.cardRegisterNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        binding.loginBtn.setOnClickListener(v -> validateInputs());
    }

    private void validateInputs() {
        // Get input values
        String username = binding.edtUser.getText() != null ? binding.edtUser.getText().toString().trim() : "";
        String password = binding.edtPass.getText() != null ? binding.edtPass.getText().toString().trim() : "";

        // Validate username
        if (TextUtils.isEmpty(username)) {
            binding.textFieldUser.setError(getString(R.string.error_username_required));
            binding.textFieldUser.requestFocus();
            return;
        } else {
            binding.textFieldUser.setError(null); // Clear previous errors
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            binding.textFieldPass.setError(getString(R.string.error_password_required));
            binding.textFieldPass.requestFocus();
            return;
        }

        if (password.length() < 6) {
            binding.textFieldPass.setError(getString(R.string.error_password_length));
            binding.textFieldPass.requestFocus();
            return;
        } else {
            binding.textFieldPass.setError(null); // Clear previous errors
        }

        // If validation passes
       login(username,password);
    }

    private void login(String username,String password) {
        // Show loader while making the network request
        binding.loader.rlLoader.setVisibility(View.VISIBLE);

        // Create Login object
        Login loginRequest = new Login();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        Log.d("TAG", "login: "+username+" "+password);

        // Observe LiveData from ViewModel
        viewModel.postLogin(loginRequest).observe(this, apiResponse -> {
            // Hide loader after receiving response
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (apiResponse != null) {
                if (apiResponse.isSuccess) {
                    // Successful login
                    responsee = apiResponse.data;
                    Toast.makeText(this, ""+responsee.getMessage(), Toast.LENGTH_SHORT).show();

                    // Save token and navigate to the next activity
                    saveToken(responsee.getToken(), responsee.user_display_name, responsee.user_nicename, responsee.user_email);
                    Intent intent = new Intent(SignInActivity.this, ContainerActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d("TAG", "login: "+apiResponse.message);
                    // Display the error message from the API response
                    Toast.makeText(this, apiResponse.message, Toast.LENGTH_SHORT).show();
                }
            } else {

                // Null response or network failure
                Toast.makeText(this, "Login failed! Please check your network connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveToken(String token,String displayName,String niceName,String email) {
        pref.setPrefString(this,pref.user_token,token);
        pref.setPrefString(this,pref.user_email,email);
        pref.setPrefString(this,pref.user_name,displayName);
        pref.setPrefString(this,pref.user_nice_name,niceName);
        pref.setPrefBoolean(this, pref.login_status, true);
    }


}