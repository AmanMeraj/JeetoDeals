package com.deals.jeetodeals.ContainerActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import com.deals.jeetodeals.SignupScreen.SignUpActivity;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.WebViewActivity;
import com.deals.jeetodeals.Wishlist.ActivityWishlist;
import com.deals.jeetodeals.databinding.ActivityContainerBinding;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;

import java.util.Arrays;

public class ContainerActivity extends Utility {

    ActivityContainerBinding binding;
    private Fragment currentFragment;

    private NavigationBarView.OnItemSelectedListener navigationItemSelectedListener;


    private static final String USER_AGREEMENT_URL = "https://www.jeetodeals.com/terms-condition/";
    private static final String PRIVACY_POLICY_URL = "https://www.jeetodeals.com/privacy-policy-2/";
    private  boolean isLoggedIn;

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

        if (getIntent().hasExtra("navigate_to")) {
            String navigateTo = getIntent().getStringExtra("navigate_to");

            if ("ticket_fragment".equals(navigateTo)) {
                // Load the ticket fragment
                currentFragment = new TicketFragment();
                loadFragment(currentFragment);

                // Update bottom navigation to select the ticket item
                binding.bottomNavigation.setSelectedItemId(R.id.ticket);
                updateSelectedIcon(R.id.ticket);
            }
        } else {
            // Set the default fragment (HomeFragment) when the activity is created
            binding.bottomNavigation.setSelectedItemId(R.id.home);
            updateSelectedIcon(R.id.home);

            // Initialize with HomeFragment
            if (currentFragment == null) {
                currentFragment = new HomeFragment();
                loadFragment(currentFragment);
            }
        }

        isLoggedIn = pref.getPrefBoolean(ContainerActivity.this, pref.login_status);

        if(isLoggedIn){
            binding.tvUserNsame.setText("Hi ! "+pref.getPrefString(this,pref.first_name)+" "+pref.getPrefString(this,pref.last_name));
        }else {
            binding.tvUserNsame.setText("");
        }


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
        isLoggedIn = pref.getPrefBoolean(this, pref.login_status);

        Log.d("LoginStatus", "Is user logged in: " + isLoggedIn); // Add logging to debug
        View socialLayout = binding.navigationView.getMenu().findItem(R.id.nav_social_media).getActionView();

        // Find ImageViews
        ImageView imgFacebook = socialLayout.findViewById(R.id.img_facebook);
        ImageView imgInstagram = socialLayout.findViewById(R.id.img_instagram);
        ImageView imgLinkedin = socialLayout.findViewById(R.id.img_linkedin);

        // Set click listeners
        imgFacebook.setOnClickListener(v -> openSocialMedia(pref.getPrefString(this,pref.facebook)));
        imgInstagram.setOnClickListener(v -> openSocialMedia(pref.getPrefString(this,pref.instagram)));
        imgLinkedin.setOnClickListener(v -> openSocialMedia(pref.getPrefString(this,pref.linkedin)));

        // Setup logout item
        MenuItem logoutItem = menu.findItem(R.id.logout);
        MenuItem loginItem = menu.findItem(R.id.nav_signIn);
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
                loginItem.setVisible(false);
                logoutItem.setVisible(true);
            } else {
                loginItem.setVisible(true);
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

            // Handle navigation item clicks
            if (itemId == R.id.nav_signIn) {
                Intent intent = new Intent(ContainerActivity.this, SignInActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.nav_jeeto_shop) {
                // Allow access to JeetoShop fragment without login
                loadFragment(new ShopFragment());
                clearBottomNavSelection();
                binding.bottomNavigation.setItemBackgroundResource(android.R.color.transparent);
            } else if (itemId == R.id.draw_terms_and_conditions) {
                // Allow access to Terms and Conditions without login
                openWebViewActivity(USER_AGREEMENT_URL, "User Agreement");
            } else if (itemId == R.id.draw_privacy) {
                // Allow access to Privacy Policy without login
                openWebViewActivity(PRIVACY_POLICY_URL, "Privacy Policy");
            } else if (!isLoggedIn) {
                // For all other protected pages, redirect to login if not logged in
                Toast.makeText(this, "Please log in to access this feature", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ContainerActivity.this, SignInActivity.class));
                return true;
            } else if (itemId == R.id.nav_ticket) {
                loadFragment(new TicketFragment());

                // Update bottom navigation to select ticket
                binding.bottomNavigation.setOnItemSelectedListener(null); // Temporarily remove listener
                binding.bottomNavigation.setSelectedItemId(R.id.ticket);  // Select ticket in bottom nav
                updateSelectedIcon(R.id.ticket);
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
        // Remove tint to use custom colors for icons
        binding.bottomNavigation.setItemIconTintList(null);

        // Create and store the listener
        navigationItemSelectedListener = item -> {
            Fragment fragment = null;

            // Change icon for selected item
            updateSelectedIcon(item.getItemId());

            if (item.getItemId() == R.id.home) {
                fragment = new HomeFragment();
                binding.bottomNavigation.setItemBackgroundResource(R.drawable.bottom_nav_icon_background);
            } else if (item.getItemId() == R.id.profile) {
                binding.bottomNavigation.setItemBackgroundResource(R.drawable.bottom_nav_icon_background);
                if (isLoggedIn) {
                    // If logged in, open the profile activity
                    Intent intent = new Intent(ContainerActivity.this, ActivityProfile.class);
                    startActivity(intent);
                } else {
                    // If not logged in, redirect to login activity
                    Intent intent = new Intent(ContainerActivity.this, SignInActivity.class);
                    startActivity(intent);} // Keep selection state
            } else if (item.getItemId() == R.id.ticket) {
                isLoggedIn = pref.getPrefBoolean(this, pref.login_status);
                binding.bottomNavigation.setItemBackgroundResource(R.drawable.bottom_nav_icon_background);

                if (!isLoggedIn) {
                    // User is not logged in, redirect to login screen
                    Intent intent = new Intent(ContainerActivity.this, SignInActivity.class);
                    startActivity(intent);
                    return false; // Don't select this item if redirecting
                } else {
                    fragment = new TicketFragment();
                }
            } else if (item.getItemId() == R.id.wallet) {
                isLoggedIn = pref.getPrefBoolean(this, pref.login_status);
                binding.bottomNavigation.setItemBackgroundResource(R.drawable.bottom_nav_icon_background);
                if (!isLoggedIn) {
                    // User is not logged in, redirect to login screen
                    Intent intent = new Intent(ContainerActivity.this, SignInActivity.class);
                    startActivity(intent);
                    return false; // Don't select this item if redirecting
                } else {
                    fragment = new WalletFragment();
                }
            } else if (item.getItemId() == R.id.cart) {
                isLoggedIn = pref.getPrefBoolean(this, pref.login_status);
                binding.bottomNavigation.setItemBackgroundResource(R.drawable.bottom_nav_icon_background);
                if (!isLoggedIn) {
                    // User is not logged in, redirect to login screen
                    Intent intent = new Intent(ContainerActivity.this, SignInActivity.class);
                    startActivity(intent);
                    return false; // Don't select this item if redirecting
                } else {
                    fragment = new CartFragment();
                }
            }

            // Load the selected fragment if it's not the same as the current one
            if (fragment != null && fragment != currentFragment) {
                currentFragment = fragment;
                loadFragment(fragment);
            }
            return true;
        };

        // Set the listener
        binding.bottomNavigation.setOnItemSelectedListener(navigationItemSelectedListener);
    }

    // Method to update the selected icon
    private void updateSelectedIcon(int selectedItemId) {
        Menu menu = binding.bottomNavigation.getMenu();

        // Reset all icons to default
        menu.findItem(R.id.home).setIcon(R.drawable.home_jd);
        menu.findItem(R.id.profile).setIcon(R.drawable.user);
        menu.findItem(R.id.ticket).setIcon(R.drawable.tickect_jd);
        menu.findItem(R.id.wallet).setIcon(R.drawable.wallet_jd);
        menu.findItem(R.id.cart).setIcon(R.drawable.shop_jd);

        // Set selected icon
        if (selectedItemId == R.id.home) {
            menu.findItem(R.id.home).setIcon(R.drawable.white_home);
        }  else if (selectedItemId == R.id.ticket) {
            menu.findItem(R.id.ticket).setIcon(R.drawable.white_ticket);
        } else if (selectedItemId == R.id.wallet) {
            menu.findItem(R.id.wallet).setIcon(R.drawable.white_wallet);
        } else if (selectedItemId == R.id.cart) {
            menu.findItem(R.id.cart).setIcon(R.drawable.white_cart);
        }else if (selectedItemId == R.id.profile) {
            Drawable icon = ContextCompat.getDrawable(this, R.drawable.user);
            if (icon != null) {
                icon = icon.mutate(); // Ensure it doesn’t affect other uses of this drawable
                icon.setTint(ContextCompat.getColor(this, android.R.color.white));
                menu.findItem(R.id.profile).setIcon(icon);
            }
        }


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
        // Check login status before showing cart badge
        isLoggedIn = pref.getPrefBoolean(this, pref.login_status);

        BadgeDrawable badge = binding.bottomNavigation.getOrCreateBadge(R.id.cart);
        badge.setBackgroundColor(Color.RED);
        badge.setBadgeTextColor(Color.WHITE);

        // Only show badge if user is logged in AND there are items in cart
        if (isLoggedIn && getCartItemCount() > 0) {
            badge.setNumber(getCartItemCount());
            badge.setVisible(true);
        } else {
            badge.setVisible(false);
        }
    }

    private int getCartItemCount() {
        return pref.getPrefInteger(this, pref.cart_count);
    }

    public void updateCartBadge(int count) {
        // Check login status before updating badge
        isLoggedIn = pref.getPrefBoolean(this, pref.login_status);

        BadgeDrawable badge = binding.bottomNavigation.getOrCreateBadge(R.id.cart);
        if (isLoggedIn && count > 0) {
            badge.setNumber(count);
            badge.setVisible(true);
        } else {
            badge.setVisible(false);
        }
    }
    private void openSocialMedia(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to open link", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (currentFragment != null) {
            int selectedItemId = R.id.home; // Default to home

            if (currentFragment instanceof HomeFragment) {
                selectedItemId = R.id.home;
            } else if (currentFragment instanceof TicketFragment) {
                selectedItemId = R.id.ticket;
            } else if (currentFragment instanceof WalletFragment) {
                selectedItemId = R.id.wallet;
            } else if (currentFragment instanceof CartFragment) {
                selectedItemId = R.id.cart;
            }

            // Temporarily remove the listener
            binding.bottomNavigation.setOnItemSelectedListener(null);

            // Update selection
            binding.bottomNavigation.setSelectedItemId(selectedItemId);
            updateSelectedIcon(selectedItemId);

            // Reattach the listener using the stored reference
            binding.bottomNavigation.setOnItemSelectedListener(navigationItemSelectedListener);
        }

        isLoggedIn = pref.getPrefBoolean(this, pref.login_status);
        setupNavigationView();
        setupCartBadge();
        if(isLoggedIn){
            binding.tvUserNsame.setText("Hi ! "+pref.getPrefString(this,pref.first_name)+" "+pref.getPrefString(this,pref.last_name));
        }else {
            binding.tvUserNsame.setText("");
        }
    }

    private void openWebViewActivity(String url, String title) {
        Intent intent = new Intent(ContainerActivity.this, WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        startActivity(intent);
    }
    private void clearBottomNavSelection() {
        // Temporarily remove the listener to prevent callback
        binding.bottomNavigation.setOnItemSelectedListener(null);

        // Reset all icons to default
        Menu menu = binding.bottomNavigation.getMenu();
        menu.findItem(R.id.home).setIcon(R.drawable.home_jd);
        menu.findItem(R.id.profile).setIcon(R.drawable.user);
        menu.findItem(R.id.ticket).setIcon(R.drawable.tickect_jd);
        menu.findItem(R.id.wallet).setIcon(R.drawable.wallet_jd);
        menu.findItem(R.id.cart).setIcon(R.drawable.shop_jd);

        // Clear current selection
        binding.bottomNavigation.setSelectedItemId(0); // 0 is an invalid ID that won't match any menu item

        // Reattach the listener
        binding.bottomNavigation.setOnItemSelectedListener(navigationItemSelectedListener);
    }


}
