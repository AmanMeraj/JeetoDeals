package com.deals.jeetodeals.IntroductionScreen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.deals.jeetodeals.Adapters.IntroPagerAdapter2;
import com.deals.jeetodeals.ContainerActivity.ContainerActivity;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.SignInScreen.SignInActivity;
import com.deals.jeetodeals.Utils.SharedPref;
import com.deals.jeetodeals.databinding.ActivityIntroductionBinding;

public class ActivityIntroduction extends AppCompatActivity {
    private ActivityIntroductionBinding binding;
    private SharedPref sharedPref;
    private static final String INTRO_SHOWN_KEY = "intro_shown";

    private int[] images = {
            R.drawable.intro1,
            R.drawable.intro2,
            R.drawable.intro3,
            R.drawable.intro4
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityIntroductionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkAndRequestNotificationPermission();

        sharedPref = new SharedPref();

        // Setup edge-to-edge display using the root view
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Setup ViewPager
        IntroPagerAdapter2 adapter = new IntroPagerAdapter2(this, images);
        binding.viewPager.setAdapter(adapter);

        // Setup back button
        binding.btnBack.setOnClickListener(v -> {
            if (binding.viewPager.getCurrentItem() > 0) {
                binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() - 1);
            }
        });

        // Setup next button - using the parent RelativeLayout for click
        binding.getRoot().findViewById(R.id.nextButtonLayout).setOnClickListener(v -> {
            if (binding.viewPager.getCurrentItem() < images.length - 1) {
                binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() + 1);
            } else {
                // Last page reached
                sharedPref.setPrefBoolean(this, INTRO_SHOWN_KEY, true);
                startSignInActivity();
                finish();
            }
        });

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                binding.btnBack.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
                binding.nextbutton.setText(position == images.length - 1 ? "Start" : "Next");
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void startSignInActivity() {
        Intent intent = new Intent(this, ContainerActivity.class);
        startActivity(intent);
    }

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                Log.d("NotificationPermission", isGranted ? "Permission granted!" : "Permission denied!");
            }
    );

    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                Log.d("NotificationPermission", "Notification permission is already granted.");
            } else {
                Log.d("NotificationPermission", "Requesting notification permission.");
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            Log.d("NotificationPermission", "Notification permission is not required for this Android version.");
        }
    }
}