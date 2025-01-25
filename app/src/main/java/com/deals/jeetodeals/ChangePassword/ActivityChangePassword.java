package com.deals.jeetodeals.ChangePassword;

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
import com.deals.jeetodeals.databinding.ActivityChangePasswordBinding;

public class ActivityChangePassword extends AppCompatActivity {
ActivityChangePasswordBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSubmit();
            }
        });
    }

    private void validateAndSubmit() {
        String currentPassword = binding.edtFirstName.getText().toString().trim();
        String newPassword = binding.edtLastName.getText().toString().trim();
        String confirmPassword = binding.edtConfirmPass.getText().toString().trim();

        // Validation Rules
        if (TextUtils.isEmpty(currentPassword)) {
            binding.edtFirstName.setError("Current password cannot be empty");
            binding.edtFirstName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            binding.edtLastName.setError("New password cannot be empty");
            binding.edtLastName.requestFocus();
            return;
        }

        if (newPassword.length() < 8) {
            binding.edtLastName.setError("New password must be at least 8 characters");
            binding.edtLastName.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            binding.edtConfirmPass.setError("Passwords do not match");
            binding.edtConfirmPass.requestFocus();
            return;
        }

        // Success: Proceed with password update
        Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
        // Add logic to update the password (e.g., API call or database update)
    }
}