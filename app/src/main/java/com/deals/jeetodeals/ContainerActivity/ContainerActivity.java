package com.deals.jeetodeals.ContainerActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
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
    private  boolean isLoggedIn=false;

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
             isLoggedIn = pref.getPrefBoolean(ContainerActivity.this, pref.login_status);

            if (isLoggedIn) {
                // If logged in, open the profile activity
                Intent intent = new Intent(ContainerActivity.this, ActivityProfile.class);
                startActivity(intent);
            } else {
                // If not logged in, redirect to login activity
                Intent intent = new Intent(ContainerActivity.this, SignInActivity.class);
                startActivity(intent);}
            finish();
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
        boolean isLoggedIn = pref.getPrefBoolean(this, pref.login_status);

        // Setup logout item
        MenuItem logoutItem = menu.findItem(R.id.logout);
        if (logoutItem != null) {
            if (isLoggedIn) {
                // Inflate logout button view if logged in
                View logoutView = getLayoutInflater().inflate(R.layout.menu_logout_button, null);

                logoutView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));

                logoutView.setOnClickListener(v -> {
                    // Show logout confirmation dialog
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("Logout")
                            .setMessage("Are you sure you want to logout?")
                            .setPositiveButton("Logout", (dialog, which) -> {
                                Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
                                pref.setPrefBoolean(this, pref.login_status, false);

                                // Redirect to SignInActivity
                                Intent intent = new Intent(ContainerActivity.this, SignInActivity.class);
                                startActivity(intent);
                                finish();
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                            .show();
                });

                logoutItem.setActionView(logoutView);
                logoutItem.setVisible(true);
            } else {
                logoutItem.setVisible(false);
            }
        }

        // Change text and icon color for all menu items
        ColorStateList whiteColor = ColorStateList.valueOf(Color.WHITE);
        binding.navigationView.setItemTextColor(whiteColor);
        binding.navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            binding.navigationView.setItemIconTintList(null);

            // Safely set white color for menu items
            for (int i = 0; i < menu.size(); i++) {
                MenuItem menuItem = menu.getItem(i);
                CharSequence title = menuItem.getTitle();
                if (title != null) {
                    SpannableString spanString = new SpannableString(title);
                    spanString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spanString.length(), 0);
                    menuItem.setTitle(spanString);
                }
            }

            // If not logged in, redirect to login page
            if (!isLoggedIn) {
                Toast.makeText(this, "Please log in to access this feature", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ContainerActivity.this, SignInActivity.class));
                finish();
                return true;
            }

            // Handle navigation item clicks
            if (itemId == R.id.nav_ticket) {
                loadFragment(new TicketFragment());
            } else if (itemId == R.id.nav_order) {
                startActivity(new Intent(ContainerActivity.this, ActivityMyOrders.class));
            } else if (itemId == R.id.nav_wishlist) {
                startActivity(new Intent(ContainerActivity.this, ActivityWishlist.class));
            } else if (itemId == R.id.nav_address) {
                startActivity(new Intent(ContainerActivity.this, ActivityChangeAddress.class));
            } else if (itemId == R.id.nav_details) {
                startActivity(new Intent(ContainerActivity.this, ActivityProfile.class));
            } else if (itemId == R.id.nav_contact) {
                String adminNumber = pref.getPrefString(this, pref.admin_whatsapp);
                openWhatsApp(adminNumber, "Hello");
            } else if (itemId == R.id.nav_call) {
                makePhoneCall(pref.getPrefString(this, pref.admin_number));
            } else if (itemId == R.id.nav_email) {
                sendEmailSingleRecipient(pref.getPrefString(this, pref.admin_email), "Need help regarding", "Hello there!");
            }

            binding.main.closeDrawer(binding.navigationView); // Close the drawer after item click
            return true;
        });
    }

    private void openWhatsApp(String phoneNumber, String message) {
        PackageManager packageManager = getPackageManager();
        try {
            // Check if WhatsApp is installed
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String url = "https://wa.me/" + phoneNumber + "?text=" + Uri.encode(message);
            intent.setData(Uri.parse(url));

            // Verify if the WhatsApp package exists
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "WhatsApp is not installed on your device", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to open WhatsApp", Toast.LENGTH_SHORT).show();
        }
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
                isLoggedIn = pref.getPrefBoolean(this, pref.login_status);

                if (!isLoggedIn) {
                    // User is not logged in, redirect to login screen
                    Intent intent = new Intent(ContainerActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    fragment = new TicketFragment();
                }

            } else if (item.getItemId() == R.id.wallet) {
                isLoggedIn = pref.getPrefBoolean(this, pref.login_status);

                if (!isLoggedIn) {
                    // User is not logged in, redirect to login screen
                    Intent intent = new Intent(ContainerActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();

                }else{
                    fragment = new WalletFragment();
                }

            } else if (item.getItemId() == R.id.cart) {
                isLoggedIn = pref.getPrefBoolean(this, pref.login_status);

                if (!isLoggedIn) {
                    // User is not logged in, redirect to login screen
                    Intent intent = new Intent(ContainerActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    fragment = new CartFragment();
                }

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
