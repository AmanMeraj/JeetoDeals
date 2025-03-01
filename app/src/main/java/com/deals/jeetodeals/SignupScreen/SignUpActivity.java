package com.deals.jeetodeals.SignupScreen;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.deals.jeetodeals.Model.Signup;
import com.deals.jeetodeals.OTP.ActivityOTP;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.SignInScreen.SignInActivity;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.WebViewActivity;
import com.deals.jeetodeals.databinding.ActivitySignUpBinding;
import com.google.gson.Gson;

public class SignUpActivity extends Utility {
    ActivitySignUpBinding binding;
    SignupViewModel viewModel;

    String OTP = "sms"; // Default OTP method

    // URLs for User Agreement and Privacy Policy
    private static final String USER_AGREEMENT_URL = "https://www.jeetodeals.com/terms-condition/";
    private static final String PRIVACY_POLICY_URL = "https://www.jeetodeals.com/privacy-policy-2/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(SignupViewModel.class);

        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up clickable terms and privacy policy text
        setupClickableLinks();

        // Navigate to Login screen
        binding.cardLoginNow.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
        });

        // Gender selection logic: Ensure only one is selected
        binding.male.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.female.setChecked(false);
            }
        });

        binding.female.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.male.setChecked(false);
            }
        });

        // OTP Selection logic
        binding.mailRelative.setOnClickListener(view -> {
            OTP = "mail";
            binding.mailRelative.setBackgroundResource(R.drawable.selected_otp_bg);
            binding.smsRelative.setBackgroundResource(R.drawable.otp_bg);
            binding.whatsappRelative.setBackgroundResource(R.drawable.otp_bg);
        });

        binding.smsRelative.setOnClickListener(view -> {
            OTP = "sms";
            binding.mailRelative.setBackgroundResource(R.drawable.otp_bg);
            binding.smsRelative.setBackgroundResource(R.drawable.selected_otp_bg);
            binding.whatsappRelative.setBackgroundResource(R.drawable.otp_bg);
        });

        binding.whatsappRelative.setOnClickListener(view -> {
            OTP = "whatsApp";
            binding.mailRelative.setBackgroundResource(R.drawable.otp_bg);
            binding.smsRelative.setBackgroundResource(R.drawable.otp_bg);
            binding.whatsappRelative.setBackgroundResource(R.drawable.selected_otp_bg);
        });

        // Register button click listener
        binding.registerBtn.setOnClickListener(v -> validateFields());

        // Hide loader initially
        binding.loader.rlLoader.setVisibility(View.GONE);
    }

    /**
     * Sets up the clickable links for User Agreement and Privacy Policy TextViews
     */
    private void setupClickableLinks() {
        // Set click listener for user agreement TextView
        TextView userAgreementTextView = binding.textUserAgreement;
        userAgreementTextView.setOnClickListener(v -> {
            openWebViewActivity(USER_AGREEMENT_URL, "User Agreement");
        });

        // Set click listener for privacy policy TextView
        TextView privacyPolicyTextView = binding.textPrivacyPolicy;
        privacyPolicyTextView.setOnClickListener(v -> {
            openWebViewActivity(PRIVACY_POLICY_URL, "Privacy Policy");
        });
    }

    /**
     * Opens a WebView activity with the specified URL and title
     * @param url The URL to load in the WebView
     * @param title The title to display in the WebView activity
     */
    private void openWebViewActivity(String url, String title) {
        Intent intent = new Intent(SignUpActivity.this, WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    private void validateFields() {
        // Get values from the UI
        String fname = binding.edtFirstName.getText().toString().trim();
        String lname = binding.edtLastName.getText().toString().trim();
        String email = binding.edtEmail.getText().toString().trim();
        String password = binding.edtPass.getText().toString().trim();
        String confirmPassword = binding.edtConfPass.getText().toString().trim();
        String phone = binding.edtPhone.getText().toString().trim();

        // Get gender selection
        boolean isMaleSelected = binding.male.isChecked();
        boolean isFemaleSelected = binding.female.isChecked();

        // Get terms and conditions checkbox state
        boolean isTermsChecked = binding.checkbox.isChecked();

        // Validate first name
        if (TextUtils.isEmpty(fname)) {
            binding.edtFirstName.setError("First name is required.");
            return;
        } else if (!fname.matches("[a-zA-Z]+")) {
            binding.edtFirstName.setError("First name should only contain letters.");
            return;
        }

        // Validate last name
        if (TextUtils.isEmpty(lname)) {
            binding.edtLastName.setError("Last name is required.");
            return;
        } else if (!lname.matches("[a-zA-Z]+")) {
            binding.edtLastName.setError("Last name should only contain letters.");
            return;
        }

        // Validate email
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.setError("Please enter a valid email.");
            return;
        }

        // Validate password
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            binding.edtPass.setError("Password should be at least 6 characters.");
            return;
        }

        // Validate confirm password
        if (!password.equals(confirmPassword)) {
            binding.edtConfPass.setError("Passwords do not match.");
            return;
        }

        // Validate phone number
        if (TextUtils.isEmpty(phone) || phone.length() != 10) {
            binding.edtPhone.setError("Please enter a valid phone number.");
            return;
        }

        // Validate gender selection
        if (!isMaleSelected && !isFemaleSelected) {
            Toast.makeText(this, "Please select your gender.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate terms and conditions checkbox
        if (!isTermsChecked) {
            Toast.makeText(this, "You must agree to the terms and privacy policy.", Toast.LENGTH_SHORT).show();
            return;
        }

        // If all validations pass, proceed with registration
        signUp();
    }

    private void signUp() {
        // Show loader
        binding.loader.rlLoader.setVisibility(View.VISIBLE);

        // Collect data from the UI
        String firstName = binding.edtFirstName.getText() != null ? binding.edtFirstName.getText().toString().trim() : "";
        String lastName = binding.edtLastName.getText() != null ? binding.edtLastName.getText().toString().trim() : "";
        String email = binding.edtEmail.getText() != null ? binding.edtEmail.getText().toString().trim() : "";
        String password = binding.edtPass.getText() != null ? binding.edtPass.getText().toString().trim() : "";
        String phone = binding.edtPhone.getText() != null ? binding.edtPhone.getText().toString().trim() : "";
        String gender = binding.male.isChecked() ? "Male" : "Female";
        String otpMethod = OTP; // OTP method selected (Mail, SMS, WhatsApp)

        // Validate input
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            binding.loader.rlLoader.setVisibility(View.GONE);
            return;
        }

        // Create Signup object
        Signup signup = new Signup();
        signup.setFirstname(firstName);
        signup.setLastname(lastName);
        signup.setEmail(email);
        signup.setPassword(password);
        signup.setGender(gender);
        signup.setPhone(phone);
        signup.setOtp_method(otpMethod);

        // Observe LiveData from ViewModel
        viewModel.postSignup(signup).observe(this, response -> {
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response != null) {
                Log.d("TAG", "Full Response: " + new Gson().toJson(response));

                String code = response.data.getCode(); // Extract the code from the response
                String message = response.data.getMessage(); // Extract the message from the response

                if (response.isSuccess) {
                    if ("otp_error".equals(code)) {
                        // Handle OTP sending failure
                        Toast.makeText(this, message != null ? message : "Failed to send OTP.", Toast.LENGTH_SHORT).show();
                    } else if (message != null && message.contains("OTP sent successfully")) {
                        // Handle OTP sending success
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, ActivityOTP.class);
                        intent.putExtra("email", signup.getEmail());
                        startActivity(intent);
                        finish();
                    } else {
                        // Handle other success cases
                        Toast.makeText(this, message != null ? message : "Signup successful!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle failure cases
                    if ("email_exists".equals(code)) {
                        Toast.makeText(this, "Email already exists!", Toast.LENGTH_SHORT).show();
                    } else if ("phone_exists".equals(code)) {
                        Toast.makeText(this, "Phone number already exists!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, message != null ? message : "An unexpected error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                // Handle null response
                Toast.makeText(this, "Signup failed! Please contact admin", Toast.LENGTH_SHORT).show();
            }
        });
    }
}