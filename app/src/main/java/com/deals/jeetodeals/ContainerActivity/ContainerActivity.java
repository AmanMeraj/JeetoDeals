package com.deals.jeetodeals.ContainerActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.deals.jeetodeals.Fragments.CartFragment;
import com.deals.jeetodeals.Fragments.HomeFragment;
import com.deals.jeetodeals.Fragments.ShopFragment;
import com.deals.jeetodeals.Fragments.TicketFragment;
import com.deals.jeetodeals.Fragments.WalletFragment;
import com.deals.jeetodeals.MyOrders.ActivityMyOrders;
import com.deals.jeetodeals.OTP.ActivityOTP;
import com.deals.jeetodeals.Profile.ActivityProfile;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.SignInScreen.SignInActivity;
import com.deals.jeetodeals.Wishlist.ActivityWishlist;
import com.deals.jeetodeals.databinding.ActivityContainerBinding;

public class ContainerActivity extends AppCompatActivity {

    ActivityContainerBinding binding;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContainerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Set up Toolbar with drawer toggle
        setupToolbar();

        // Handle bottom navigation item clicks
        bottomMenuClick();

        setupNavigationView();

        // Set the default fragment (HomeFragment) when the activity is created
        if (savedInstanceState == null) {
            currentFragment = new HomeFragment();
            loadFragment(currentFragment);
        }

        // Handle profile image click
        binding.profileImage.setOnClickListener(view -> {
            Intent intent = new Intent(ContainerActivity.this, ActivityProfile.class);
            startActivity(intent);
        });

        // Handle back press to navigate to HomeFragment or close the app
        handleBackPress();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);

        // Remove the default drawer toggle icon and use your custom menu icon
        binding.toolbar.setNavigationIcon(null);
        binding.navigationView.setItemIconTintList(null);// This removes the default hamburger icon.

        // Set the custom menu icon to open/close the drawer
        binding.menu.setOnClickListener(v -> {
            if (binding.main.isDrawerOpen(binding.navigationView)) {
                binding.main.closeDrawer(binding.navigationView);
            } else {
                binding.main.openDrawer(binding.navigationView);
            }
        });

        // Handle drawer toggle behavior with your custom menu
        binding.toolbar.setNavigationOnClickListener(v -> {
            if (binding.main.isDrawerOpen(binding.navigationView)) {
                binding.main.closeDrawer(binding.navigationView);
            } else {
                binding.main.openDrawer(binding.navigationView);
            }
        });
    }



    private void setupNavigationView() {
        Menu menu = binding.navigationView.getMenu();

        // Setup logout item
        MenuItem logoutItem = menu.findItem(R.id.logout);
        if (logoutItem != null) {
            View logoutView = getLayoutInflater().inflate(R.layout.menu_logout_button, null);

            // Make sure the view takes up the full width
            logoutView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            logoutView.setOnClickListener(v -> {
                // Handle logout
                Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
            });

            logoutItem.setActionView(logoutView);
        }

        // Change text and icon color for all menu items
        ColorStateList whiteColor = ColorStateList.valueOf(Color.WHITE);
        binding.navigationView.setItemTextColor(whiteColor);
        binding.navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Disable icon tint to show original icon colors
            binding.navigationView.setItemIconTintList(null);

            // Safely set white color for menu items
            for (int i = 0; i < menu.size(); i++) {
                MenuItem menuItem = menu.getItem(i);
                CharSequence title = menuItem.getTitle();

                // Only process if the title is not null
                if (title != null) {
                    SpannableString spanString = new SpannableString(title);
                    spanString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spanString.length(), 0);
                    menuItem.setTitle(spanString);
                }
            }

            // Handle navigation item clicks
            if (itemId == R.id.nav_login) {
                startActivity(new Intent(ContainerActivity.this, SignInActivity.class));
            } else if (itemId == R.id.nav_ticket) {
                loadFragment(new TicketFragment());
            } else if (itemId == R.id.nav_order) {
                Intent intentOrders = new Intent(ContainerActivity.this, ActivityMyOrders.class);
                startActivity(intentOrders);
            } else if (itemId == R.id.nav_wishlist) {
                Intent wishlist= new Intent(ContainerActivity.this, ActivityWishlist.class);
                startActivity(wishlist);
            } else if (itemId == R.id.nav_address) {
                Toast.makeText(this, "User Address Clicked", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_details) {
                Toast.makeText(this, "Account Details Clicked", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_contact) {
                Toast.makeText(this, "Contact Us Clicked", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_works) {
                Toast.makeText(this, "How It Works Clicked", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_call) {
                makePhoneCall();
            } else if (itemId == R.id.email) {
                sendEmail();
            } else if (itemId == R.id.nav_agreement) {
                Toast.makeText(this, "User Agreement Clicked", Toast.LENGTH_SHORT).show();
            }

            binding.main.closeDrawer(binding.navigationView); // Close the drawer after item click
            return true;
        });
    }


    private void bottomMenuClick() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            if (item.getItemId() == R.id.home) {
                fragment = new HomeFragment();
            } else if (item.getItemId() == R.id.shop) {
                fragment = new ShopFragment();
            } else if (item.getItemId() == R.id.ticket) {
                fragment = new TicketFragment();
            } else if (item.getItemId() == R.id.wallet) {
                fragment = new WalletFragment();
            } else if (item.getItemId() == R.id.cart) {
                fragment = new CartFragment();
            }

            // Load the selected fragment if it's not the same as the current one
            if (fragment != null && fragment != currentFragment) {
                currentFragment = fragment;
                loadFragment(fragment);
            }
            return true;
        });
    }

    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:support@example.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Request");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello, I need help with...");

        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        } else {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void makePhoneCall() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:+1234567890"));
        startActivity(intent);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }

    private void handleBackPress() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!(currentFragment instanceof HomeFragment)) {
                    currentFragment = new HomeFragment();
                    loadFragment(currentFragment);
                    binding.bottomNavigation.setSelectedItemId(R.id.home);
                } else {
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}
