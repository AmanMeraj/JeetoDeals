package com.deals.jeetodeals.SignInScreen;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.deals.jeetodeals.ContainerActivity.ContainerActivity;
import com.deals.jeetodeals.ForgotPasswordScreen.ForgotPasswordActivity;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.SignupScreen.SignUpActivity;
import com.deals.jeetodeals.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {


    ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignInBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(SignInActivity.this, ForgotPasswordActivity.class);
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
        proceedToNextActivity();
    }

    private void proceedToNextActivity() {
        // TODO: Add logic to proceed to the next activity
        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();

        Intent intent= new Intent(SignInActivity.this, ContainerActivity.class);
        startActivity(intent);
    }

}