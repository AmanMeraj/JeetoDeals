package com.deals.jeetodeals.SplashScreen;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import com.deals.jeetodeals.Fragments.FragmentsRepository;
import com.deals.jeetodeals.Fragments.FragmentsViewModel;
import com.deals.jeetodeals.IntroductionScreen.ActivityIntroduction;
import com.deals.jeetodeals.Model.AppVersion;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.SignInScreen.SignInActivity;
import com.deals.jeetodeals.SplashVideo.ActivityVideoScreen;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.databinding.ActivitySplashBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SplashActivity extends Utility {
    ActivitySplashBinding binding;
    FragmentsViewModel viewModel;
    private static final String INTRO_SHOWN_KEY = "intro_shown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(FragmentsViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        proceed();
    }

    private void proceed() {
        viewModel.getAppVersion().observe(this, response -> {
            if (response != null && response.data != null) {
                // Log all data
                Log.d("AppVersion", "Response: " + response);
                Log.d("AppVersion", "WhatsApp: " + (response.data.getWhatsapp() != null ? response.data.getWhatsapp() : "null"));
                Log.d("AppVersion", "Email: " + (response.data.getEmail() != null ? response.data.getEmail() : "null"));
                Log.d("AppVersion", "Calling: " + (response.data.getCalling() != null ? response.data.getCalling() : "null"));

                // Handle app update
                handleAppUpdate(response);

                // Store data in preferences with null checks
                String whatsapp = response.data.getWhatsapp();
                String email = response.data.getEmail();
                String calling = response.data.getCalling();
                String androidVersion =response.data.getAndriod_version();
                String paymeny=response.data.getPayment_key();
                int voucher = response.data.getVoucher_rate();

                if (whatsapp != null) {
                    pref.setPrefString(this, pref.admin_whatsapp, whatsapp);
                }
                if (email != null) {
                    pref.setPrefString(this, pref.admin_email, email);
                }
                if (calling != null) {
                    pref.setPrefString(this, pref.admin_number, calling);
                }
                if(androidVersion!=null){
                    pref.setPrefString(this,pref.android_version,androidVersion);
                }
                if(paymeny!=null){
                    pref.setPrefString(this,pref.payment_key,paymeny);
                }

                pref.setPrefInteger(this, pref.voucher_rate, voucher);
                Log.d("TAG", "proceed: " + pref.getPrefInteger(this, pref.voucher_rate));
            } else {
                Log.d("AppVersion", "Response or response.data is null");
                continueNavigation();
            }
        });
    }

    private void handleAppUpdate(FragmentsRepository.ApiResponse<AppVersion> response) {
        if (response == null) {
            continueNavigation();
            return;
        }

        try {
            String currentVersion = getCurrentAppVersion();
            String latestVersion = response.data.getAndriod_version(); // API version
            boolean forceUpdate = response.data.isForce_update(); // API flag

            if (isVersionOlder(currentVersion, latestVersion)) {
                if (forceUpdate) {
                    showForceUpdateDialog(); // Mandatory update
                } else {
                    continueNavigation(); // Allow user to proceed
                }
            } else {
                continueNavigation(); // No update needed
            }
        } catch (Exception e) {
            e.printStackTrace();
            continueNavigation();
        }
    }

    private String getCurrentAppVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "1.0"; // Default version in case of error
        }
    }

    private boolean isVersionOlder(String currentVersion, String latestVersion) {
        return currentVersion.compareTo(latestVersion) < 0;
    }

    private void showForceUpdateDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Update Required")
                .setMessage("A new version of the app is available. Please update to continue.")
                .setCancelable(false)
                .setPositiveButton("Update Now", (dialog, which) -> openPlayStore())
                .setNegativeButton("Exit", (dialog, which) -> finish())
                .show();
    }

    private void openPlayStore() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    private void continueNavigation() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            // Check if this is first time launch
            if (!pref.getPrefBoolean(SplashActivity.this, INTRO_SHOWN_KEY)) {
                // First time launch, show intro screen
                startActivity(new Intent(SplashActivity.this, ActivityIntroduction.class));
                pref.setPrefBoolean(SplashActivity.this, INTRO_SHOWN_KEY, true);
                finish();
            } else {
                // Not first time, show video screen
                startActivity(new Intent(SplashActivity.this, ActivityVideoScreen.class));
                finish();
            }
        }, 3000); // Show splash for 3 seconds
    }
}