package com.deals.jeetodeals.OTP;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import com.deals.jeetodeals.ContainerActivity.ContainerActivity;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.SignupScreen.SignUpActivity;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.databinding.ActivityOtpBinding;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityOTP extends Utility {

    private static final String TAG = "OTP_DEBUG";
    private static final int SMS_CONSENT_REQUEST = 2;
    private static final int SMS_PERMISSION_REQUEST = 100;

    ActivityOtpBinding binding;
    OtpViewModel viewModel;
    OtpResponse responsee;
    String email;
    private String firstName;
    private String lastName;
    private String dob;
    private String password;
    private String phone;
    private String gender;
    private String otpMethod;

    private SMSConsentBroadcastReceiver smsConsentBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(OtpViewModel.class);
        setContentView(binding.getRoot());

        // Start SMS User Consent API
        startSmsUserConsent();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.otpButton.setOnClickListener(v -> validateAndVerifyOtp());
        binding.cardRegisterNow.setOnClickListener(v -> {
            returnToSignupWithData();
        });

        // Retrieve user data from intent
        retrieveUserData();

        // Request permissions for SMS reception
        requestPermissions();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            binding.otpView.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(binding.otpView, InputMethodManager.SHOW_IMPLICIT);
        }, 1000);
    }

    private void startSmsUserConsent() {
        // Create a client for the SMS User Consent API
        SmsRetrieverClient client = SmsRetriever.getClient(this);

        // Start the SMS User Consent flow
        Task<Void> task = client.startSmsUserConsent(null /* or sender phone number */);

        task.addOnSuccessListener(unused -> {
            Log.d(TAG, "SMS User Consent API started successfully");
        });

        task.addOnFailureListener(e -> {
            Log.e(TAG, "Failed to start SMS User Consent API", e);
            Toast.makeText(ActivityOTP.this, "Failed to initialize SMS detection", Toast.LENGTH_SHORT).show();
        });
    }

    private void registerBroadcastReceiver() {
        // Create and register the broadcast receiver for SMS User Consent
        smsConsentBroadcastReceiver = new SMSConsentBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(smsConsentBroadcastReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(smsConsentBroadcastReceiver, intentFilter);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcastReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (smsConsentBroadcastReceiver != null) {
            unregisterReceiver(smsConsentBroadcastReceiver);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SMS_CONSENT_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                // Get the SMS message content after user consent
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                Log.d(TAG, "SMS Content: " + message);

                // Extract OTP from the message
                getOtpFromMessage(message);
            } else {
                // User denied consent
                Log.d(TAG, "User denied SMS consent");
                Toast.makeText(this, "SMS consent denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getOtpFromMessage(String message) {
        if (message == null) return;

        // Use regex to extract the 6-digit OTP from the message
        Pattern otpPattern = Pattern.compile("(|^)\\d{6}");
        Matcher matcher = otpPattern.matcher(message);

        if (matcher.find()) {
            String otp = matcher.group(0);
            binding.otpView.setOTP(otp);
            Log.d(TAG, "OTP extracted: " + otp);
        } else {
            Log.d(TAG, "No OTP found in the message");
        }
    }

    // BroadcastReceiver for SMS User Consent
    public class SMSConsentBroadcastReceiver extends android.content.BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                    if (status != null) {
                        switch (status.getStatusCode()) {
                            case CommonStatusCodes.SUCCESS:
                                // Get consent intent
                                Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
                                try {
                                    // Start activity for consent
                                    startActivityForResult(consentIntent, SMS_CONSENT_REQUEST);
                                } catch (Exception e) {
                                    Log.e(TAG, "Error starting consent intent", e);
                                }
                                break;
                            case CommonStatusCodes.TIMEOUT:
                                // Timeout occurred, restart the process or notify user
                                Log.d(TAG, "SMS retrieval timeout");
                                Toast.makeText(context, "SMS retrieval timed out", Toast.LENGTH_SHORT).show();
                                // Optionally restart the SMS user consent flow
                                startSmsUserConsent();
                                break;
                        }
                    }
                }
            }
        }
    }

    private void retrieveUserData() {
        Intent intent = getIntent();
        if (intent != null) {
            email = intent.getStringExtra("email");
            firstName = intent.getStringExtra("firstName");
            lastName = intent.getStringExtra("lastName");
            dob = intent.getStringExtra("dob");
            password = intent.getStringExtra("password");
            phone = intent.getStringExtra("phone");
            gender = intent.getStringExtra("gender");
            otpMethod = intent.getStringExtra("otpMethod");

            Log.d(TAG, "Retrieved email: " + email);
            Log.d(TAG, "Retrieved phone: " + phone);
        }
    }

    private void returnToSignupWithData() {
        Intent intent = new Intent(ActivityOTP.this, SignUpActivity.class);

        // Add all user data to intent
        intent.putExtra("email", email);
        intent.putExtra("firstName", firstName);
        intent.putExtra("lastName", lastName);
        intent.putExtra("dob", dob);
        intent.putExtra("password", password);
        intent.putExtra("phone", phone);
        intent.putExtra("gender", gender);
        intent.putExtra("otpMethod", otpMethod);

        // Set result OK
        setResult(RESULT_OK, intent);

        // Start the activity with flag to clear the task
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        // Finish current activity
        finish();
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
        if (email != null) {
            verifyOtp(otp, email);
        } else {
            Toast.makeText(this, "Email is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void verifyOtp(String otp, String email) {
        // Show loader
        binding.loader.rlLoader.setVisibility(View.VISIBLE);

        // Log input values
        Log.d(TAG, "Verifying OTP: " + otp + " for email: " + email);

        // Create OTP object
        Otp otpclass = new Otp();
        otpclass.setOtp(otp);
        otpclass.setEmail(email);

        // Call the OTP verification API
        viewModel.postOtp(otpclass).observe(this, response -> {
            // Hide loader
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response != null) {
                Log.d(TAG, "OTP API Response: " + new Gson().toJson(response));

                // Check if response is successful and contains required fields
                if (response.isSuccess) {
                    responsee = response.data;
                    Log.d(TAG, "User registration successful." + response.data.message);
                    if (response.data != null && "User registered successfully.".equalsIgnoreCase(response.data.message)) {
                        Log.d(TAG, "User registration successful.");

                        String token = response.data.getToken();
                        if (token != null && !token.isEmpty()) {
                            // Store token and login status
                            pref.setPrefString(ActivityOTP.this, pref.user_token, token);
                            pref.setPrefBoolean(ActivityOTP.this, pref.login_status, true);
                            pref.setPrefString(this, pref.first_name, response.data.getFirst_name());
                            pref.setPrefString(this, pref.last_name, response.data.getLast_name());
                            pref.setPrefString(this, pref.mobile, response.data.getPhone());
                            pref.setPrefString(this, pref.user_email, response.data.getUser_email());
                            pref.setPrefString(this, pref.user_name, response.data.getUser_display_name());

                            Log.d(TAG, "Token saved: " + token);
                            navigateToContainer();
                        } else {
                            Log.e(TAG, "Token is missing! Login failed.");
                            showToast("Token is missing! Login failed.");
                        }
                    } else if (response.data != null && "invalid_otp".equalsIgnoreCase(response.data.getCode())) {
                        Log.e(TAG, "Invalid OTP entered.");
                        showToast("Invalid OTP. Please try again.");
                    } else {
                        Log.e(TAG, "Unexpected response: " + response.message);
                        showToast(response.message != null ? response.message : "An unexpected error occurred.");
                    }
                } else {
                    Log.e(TAG, "API failure. Response: " + new Gson().toJson(response));
                    showToast(response.message != null ? response.message : "Verification failed! Please try again later.");
                }
            } else {
                Log.e(TAG, "API response is null.");
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

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(ActivityOTP.this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActivityOTP.this, new String[]{
                    Manifest.permission.RECEIVE_SMS
            }, SMS_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start the SMS User Consent API
                startSmsUserConsent();
            } else {
                // Permission denied
                Toast.makeText(this, "SMS permission is required for automatic OTP detection", Toast.LENGTH_LONG).show();
            }
        }
    }
}