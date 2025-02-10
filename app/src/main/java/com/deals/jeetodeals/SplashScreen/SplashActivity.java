package com.deals.jeetodeals.SplashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.deals.jeetodeals.ContainerActivity.ContainerActivity;
import com.deals.jeetodeals.IntroductionScreen.ActivityIntroduction;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.SignInScreen.SignInActivity;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.databinding.ActivitySplashBinding;
import com.google.firebase.FirebaseApp;

public class SplashActivity extends Utility {
    ActivitySplashBinding binding;
    private static final String INTRO_SHOWN_KEY = "intro_shown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
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
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            // First check if user is logged in
            if (pref.getPrefBoolean(SplashActivity.this, pref.login_status)) {
                Intent i = new Intent(SplashActivity.this, ContainerActivity.class);
                startActivity(i);
                finish();
            } else {
                // If not logged in, check if intro has been shown
                if (pref.getPrefBoolean(SplashActivity.this, INTRO_SHOWN_KEY)) {
                    // Intro already shown, go to sign in
                    Intent i = new Intent(SplashActivity.this, SignInActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    // Intro not shown, show intro first
                    Intent i = new Intent(SplashActivity.this, ActivityIntroduction.class);
                    startActivity(i);
                    finish();
                }
            }
        }, 3000);
    }
}