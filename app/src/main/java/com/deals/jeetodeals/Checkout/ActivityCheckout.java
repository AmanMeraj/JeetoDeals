package com.deals.jeetodeals.Checkout;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.deals.jeetodeals.R;
import com.deals.jeetodeals.databinding.ActivityCheckoutBinding;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

public class ActivityCheckout extends AppCompatActivity {
ActivityCheckoutBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityCheckoutBinding.inflate(getLayoutInflater());
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


        binding.billingAddressRel.setVisibility(View.GONE);
        binding.shippingCheckBox.setVisibility(View.GONE);
        binding.shippingAddressInputText.setVisibility(View.GONE);
        binding.shippingRel.setVisibility(View.GONE);

        // Set click listener for billing address autocomplete
        MaterialAutoCompleteTextView billingAutoComplete =
                (MaterialAutoCompleteTextView) binding.billingAddressInputText.getEditText();
        billingAutoComplete.setOnClickListener(v -> {
            binding.billingAddressRel.setVisibility(View.VISIBLE);
            binding.shippingCheckBox.setVisibility(View.VISIBLE);
        });

        // Set checkbox change listener
        binding.shippingCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.shippingAddressInputText.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            // If unchecked, also hide shipping relative layout
            if (!isChecked) {
                binding.shippingRel.setVisibility(View.GONE);
            }
        });

        // Set click listener for shipping address autocomplete
        MaterialAutoCompleteTextView shippingAutoComplete =
                (MaterialAutoCompleteTextView) binding.shippingAddressInputText.getEditText();
        shippingAutoComplete.setOnClickListener(v -> {
            binding.shippingRel.setVisibility(View.VISIBLE);
        });

        // Add validation for email fields
        setupEmailValidation();
    }
    private void setupEmailValidation() {
        TextWatcher emailValidator = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String email = s.toString();
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.isEmpty()) {
                    binding.textFieldEmail.setError("Please enter a valid email address");
                } else {
                    binding.textFieldEmail.setError(null);
                }
            }
        };

        binding.edtEmail.addTextChangedListener(emailValidator);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("billing_visibility", binding.billingAddressRel.getVisibility());
        outState.putInt("shipping_visibility", binding.shippingRel.getVisibility());
        outState.putBoolean("shipping_checked", binding.shippingCheckBox.isChecked());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            binding.billingAddressRel.setVisibility(savedInstanceState.getInt("billing_visibility"));
            binding.shippingRel.setVisibility(savedInstanceState.getInt("shipping_visibility"));
            binding.shippingCheckBox.setChecked(savedInstanceState.getBoolean("shipping_checked"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // Clean up binding
    }
}