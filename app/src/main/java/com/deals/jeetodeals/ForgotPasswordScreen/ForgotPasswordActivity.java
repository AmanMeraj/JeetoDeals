package com.deals.jeetodeals.ForgotPasswordScreen;

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

import com.deals.jeetodeals.R;
import com.deals.jeetodeals.SignInScreen.SignInActivity;
import com.deals.jeetodeals.SignupScreen.SignUpActivity;
import com.deals.jeetodeals.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {
    ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.cardRegisterNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(ForgotPasswordActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        binding.loginBtn.setOnClickListener(v -> validateAndUpdatePassword());
    }

    private void validateAndUpdatePassword() {
        String newPassword = binding.edtPass.getText().toString().trim();
        String confirmPassword = binding.edtConfPass.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword)) {
            binding.textFieldUser.setError(getString(R.string.error_new_password_required));
        } else if (newPassword.length() < 6) {
            binding.textFieldUser.setError(getString(R.string.error_password_length));
        } else if (TextUtils.isEmpty(confirmPassword)) {
            binding.textFieldPass.setError(getString(R.string.error_confirm_password_required));
        } else if (!newPassword.equals(confirmPassword)) {
            binding.textFieldPass.setError(getString(R.string.error_password_mismatch));
        } else {
            // Passwords are valid
            Toast.makeText(this, getString(R.string.password_updated_success), Toast.LENGTH_SHORT).show();
            finish(); // Close the activity after success
        }
    }
}