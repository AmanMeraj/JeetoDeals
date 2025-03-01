package com.deals.jeetodeals.ContainerActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.deals.jeetodeals.ChangeAddress.ActivityChangeAddress;
import com.deals.jeetodeals.Fragments.CartFragment;
import com.deals.jeetodeals.Fragments.HomeFragment.HomeFragment;
import com.deals.jeetodeals.Fragments.ShopFragment;
import com.deals.jeetodeals.Fragments.TicketFragment;
import com.deals.jeetodeals.Fragments.WalletFragment;
import com.deals.jeetodeals.MyOrders.ActivityMyOrders;
import com.deals.jeetodeals.Profile.ActivityProfile;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.SignInScreen.SignInActivity;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.Wishlist.ActivityWishlist;
import com.deals.jeetodeals.databinding.ActivityContainerBinding;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseApp;

import java.util.Arrays;

public class ContainerActivity extends Utility {

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
        setupCartBadge();
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
                // Create the Material 3 AlertDialog
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Logout", (dialog, which) -> {
                            // Perform logout
                            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
                            pref.setPrefBoolean(this, pref.login_status, false);

                            // Start SignInActivity and finish the current one
                            Intent intent = new Intent(ContainerActivity.this, SignInActivity.class);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            // Cancel the logout (no action)
                            dialog.dismiss();
                        })
                        .show();
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
           if (itemId == R.id.nav_ticket) {
                loadFragment(new TicketFragment());
            } else if (itemId == R.id.nav_order) {
                Intent intentOrders = new Intent(ContainerActivity.this, ActivityMyOrders.class);
                startActivity(intentOrders);
            } else if (itemId == R.id.nav_wishlist) {
                Intent wishlist= new Intent(ContainerActivity.this, ActivityWishlist.class);
                startActivity(wishlist);
            } else if (itemId == R.id.nav_address) {
            Intent address= new Intent(ContainerActivity.this, ActivityChangeAddress.class);
            startActivity(address);
            } else if (itemId == R.id.nav_details) {
               Intent profile = new Intent(ContainerActivity.this,ActivityProfile.class);
               startActivity(profile);
            } else if (itemId == R.id.nav_contact) {
                Toast.makeText(this, "Contact Us Clicked", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_works) {
                Toast.makeText(this, "How It Works Clicked", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_call) {
                makePhoneCall(pref.getPrefString(this,pref.admin_number));
            } else if (itemId == R.id.nav_email) {
               sendEmailSingleRecipient(pref.getPrefString(this,pref.admin_email),"Need help regarding","Hello there !");
            } else if (itemId == R.id.nav_agreement) {
                Toast.makeText(this, "User Agreement Clicked", Toast.LENGTH_SHORT).show();
            }

            binding.main.closeDrawer(binding.navigationView); // Close the drawer after item click
            return true;
        });
    }


    private void bottomMenuClick() {

        binding.bottomNavigation.setItemIconTintList(null);

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

    public void sendEmailSingleRecipient(String recipient, String subject, String body) {
        // Log the input parameters
        Log.d("EmailDebug", "Recipient: " + recipient);
        Log.d("EmailDebug", "Subject: " + subject);
        Log.d("EmailDebug", "Body: " + body);

        // Create the email intent
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:")); // Ensure only email apps handle this
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        // Log the intent details
        Log.d("EmailDebug", "Intent Action: " + emailIntent.getAction());
        Log.d("EmailDebug", "Intent Data: " + emailIntent.getDataString());
        Log.d("EmailDebug", "Intent EXTRA_EMAIL: " + Arrays.toString(emailIntent.getStringArrayExtra(Intent.EXTRA_EMAIL)));
        Log.d("EmailDebug", "Intent EXTRA_SUBJECT: " + emailIntent.getStringExtra(Intent.EXTRA_SUBJECT));
        Log.d("EmailDebug", "Intent EXTRA_TEXT: " + emailIntent.getStringExtra(Intent.EXTRA_TEXT));

        try {
            // Start the email activity
            startActivity(emailIntent);
            Log.d("EmailDebug", "Email intent started successfully");
        } catch (ActivityNotFoundException e) {
            // Log the error if no email app is found
            Log.e("EmailDebug", "No email app found to handle the intent", e);
            // Provide user feedback
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
        }

        // Close the navigation drawer
        binding.main.closeDrawer(binding.navigationView);
        Log.d("EmailDebug", "Navigation drawer closed");
    }

    private void makePhoneCall(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "No dialer app found!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
        }
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

    private void setupCartBadge() {
        BadgeDrawable badge = binding.bottomNavigation.getOrCreateBadge(R.id.cart);
        badge.setBackgroundColor(Color.RED);
        badge.setBadgeTextColor(Color.WHITE);
        badge.setNumber(getCartItemCount());

        if (getCartItemCount() > 0) {
            badge.setVisible(true);
        } else {
            badge.setVisible(false);
        }
    }

    private int getCartItemCount() {
        return pref.getPrefInteger(this, pref.cart_count);
    }

    public void updateCartBadge(int count) {
        BadgeDrawable badge = binding.bottomNavigation.getOrCreateBadge(R.id.cart);
        if (count > 0) {
            badge.setNumber(count);
            badge.setVisible(true);
        } else {
            badge.setVisible(false);
        }
    }


}
