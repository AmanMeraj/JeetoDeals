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
import com.deals.jeetodeals.MyOrders.FragmentMyOrders;
import com.deals.jeetodeals.Profile.FragmentProfile;
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


    private static final String USER_AGREEMENT_URL = "https://www.jeetodeals.com/draw-terms-conditions/";
    private static final String PRIVACY_POLICY_URL = "https://www.jeetodeals.com/privacy-policy-3/";
    private static final String HOW_IT_WORKS_URL = "https://www.jeetodeals.com/how-it-works-mobile/";
    private  boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContainerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d("JWD", "onCreate called");
        Log.d("ContainerActivity", "onCreate");

        // Initial load (first time)
        handleIntentNavigation(getIntent());



        // Set up Toolbar with drawer toggle
        setupToolbar();

        // Handle bottom navigation item clicks
        bottomMenuClick();

        setupNavigationView();




        isLoggedIn = pref.getPrefBoolean(ContainerActivity.this, pref.login_status);

        if(isLoggedIn){
            binding.tvUserNsame.setText("Hi ! "+pref.getPrefString(this,pref.first_name)+" "+pref.getPrefString(this,pref.last_name));
        }else {
            binding.tvUserNsame.setText("");
        }

        binding.bottomNavigation.setSelectedItemId(R.id.home);
            updateSelectedIcon(R.id.home);

            if (currentFragment == null) {
                currentFragment = new HomeFragment();
                loadFragment(currentFragment);
            }


        // Handle back press to navigate to HomeFragment or close the app
        handleBackPress();
        setupCartBadge();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("JWD", "onNewIntent triggered");

        setIntent(intent); // Important: Update the internal intent reference
        handleIntentNavigation(intent);
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
            }else if (itemId == R.id.how_it_works) {
                // Allow access to Privacy Policy without login
                openWebViewActivity(HOW_IT_WORKS_URL, "How it Works");
            } else if (!isLoggedIn) {
                // For all other protected pages, redirect to login if not logged in
                Toast.makeText(this, "Please log in to access this feature", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ContainerActivity.this, SignInActivity.class));
                return true;
            } else if (itemId == R.id.nav_ticket) {
                loadFragment(new TicketFragment());


                // Then properly set the new selection with full styling
                binding.bottomNavigation.setOnItemSelectedListener(null); // Temporarily remove listener
                binding.bottomNavigation.setSelectedItemId(R.id.ticket);  // Select ticket in bottom nav
                updateSelectedIcon(R.id.ticket);
                binding.bottomNavigation.setItemBackgroundResource(R.drawable.bottom_nav_icon_background);
                binding.bottomNavigation.setOnItemSelectedListener(navigationItemSelectedListener);

                currentFragment = new TicketFragment();
            } else if (itemId == R.id.nav_order) {
                loadFragment(new FragmentMyOrders());


                // Then properly set the new selection with full styling
                binding.bottomNavigation.setOnItemSelectedListener(null); // Temporarily remove listener
                binding.bottomNavigation.setSelectedItemId(R.id.order);  // Select order in bottom nav
                updateSelectedIcon(R.id.order);
                binding.bottomNavigation.setItemBackgroundResource(R.drawable.bottom_nav_icon_background);
                binding.bottomNavigation.setOnItemSelectedListener(navigationItemSelectedListener);

                currentFragment = new FragmentMyOrders();
            } else if (itemId == R.id.nav_wallet) {
                loadFragment(new WalletFragment());

                // Then clear all bottom navigation selection styling
                clearBottomNavSelection();

                // Store current fragment reference
                currentFragment = new WalletFragment();
            } else if (itemId == R.id.nav_wishlist) {
                startActivity(new Intent(ContainerActivity.this, ActivityWishlist.class));
            } else if (itemId == R.id.nav_address) {
                startActivity(new Intent(ContainerActivity.this, ActivityChangeAddress.class));
            } else if (itemId == R.id.nav_details) {
                binding.bottomNavigation.setOnItemSelectedListener(null); // Temporarily remove listener
                binding.bottomNavigation.setSelectedItemId(R.id.profile);  // Select ticket in bottom nav
                updateSelectedIcon(R.id.profile);
                binding.bottomNavigation.setItemBackgroundResource(R.drawable.bottom_nav_icon_background);
                binding.bottomNavigation.setOnItemSelectedListener(navigationItemSelectedListener);
                loadFragment(new FragmentProfile());
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

            isLoggedIn = pref.getPrefBoolean(this, pref.login_status);
            Fragment fragment = null;

            // Change icon for selected item
            updateSelectedIcon(item.getItemId());

            if (item.getItemId() == R.id.home) {
                fragment = new HomeFragment();
            } else if (item.getItemId() == R.id.profile) {

                if (isLoggedIn) {
                    fragment = new FragmentProfile();
                } else {
                    // If not logged in, redirect to login activity
                    Intent intent = new Intent(ContainerActivity.this, SignInActivity.class);
                    startActivity(intent);
                    return false;
                }

            }

            else if (item.getItemId() == R.id.ticket) {

                if (!isLoggedIn) {
                    // User is not logged in, redirect to login screen
                    Intent intent = new Intent(ContainerActivity.this, SignInActivity.class);
                    startActivity(intent);
                    return false; // Don't select this item if redirecting
                } else {
                    fragment = new TicketFragment();
                }
            } else if (item.getItemId() == R.id.order) {
                if (!isLoggedIn) {
                    // User is not logged in, redirect to login screen
                    Intent intent = new Intent(ContainerActivity.this, SignInActivity.class);
                    startActivity(intent);
                    return false; // Don't select this item if redirecting
                } else {
                    fragment = new FragmentMyOrders();
                }
            } else if (item.getItemId() == R.id.cart) {

                if (!isLoggedIn) {
                    // User is not logged in, redirect to login screen
                    Intent intent = new Intent(ContainerActivity.this, SignInActivity.class);
                    startActivity(intent);
                    return false; // Don't select this item if redirecting
                } else {
                    binding.bottomNavigation.setItemBackgroundResource(R.drawable.bottom_nav_icon_background);
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
        menu.findItem(R.id.order).setIcon(R.drawable.clipboard_black);
        menu.findItem(R.id.cart).setIcon(R.drawable.shop_jd);

        // Restore color state list for text (add this)
        restoreBottomNavTextColors();

        // Restore icon tint list (add this)
        binding.bottomNavigation.setItemIconTintList(null);

        // Set selected icon
        if (selectedItemId == R.id.home) {
            menu.findItem(R.id.home).setIcon(R.drawable.white_home);
        } else if (selectedItemId == R.id.ticket&&isLoggedIn) {
            menu.findItem(R.id.ticket).setIcon(R.drawable.white_ticket);
        } else if (selectedItemId == R.id.order&&isLoggedIn) {
            menu.findItem(R.id.order).setIcon(R.drawable.clipboard);
        } else if (selectedItemId == R.id.cart&&isLoggedIn) {
            menu.findItem(R.id.cart).setIcon(R.drawable.white_cart);
        } else if (selectedItemId == R.id.profile&&isLoggedIn) {
            Drawable icon = ContextCompat.getDrawable(this, R.drawable.user);
            if (icon != null) {
                icon = icon.mutate(); // Ensure it doesn't affect other uses of this drawable
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
        Log.d("NAVIGATION", "Loading fragment: " + fragment.getClass().getSimpleName());
        getSupportFragmentManager()
                .beginTransaction()
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
//        Log.d("JWD", "navigate_to = " );
//        if (getIntent().hasExtra("navigate_to")) {
//            String navigateTo = getIntent().getStringExtra("navigate_to");
//
//
//            if ("ticket_fragment".equals(navigateTo)) {
//                currentFragment = new TicketFragment();
//                binding.bottomNavigation.setSelectedItemId(R.id.ticket);
//                updateSelectedIcon(R.id.ticket);
//
//            } else if ("profile".equals(navigateTo)) {
//                currentFragment = new FragmentProfile();
//                binding.bottomNavigation.setSelectedItemId(R.id.profile);
//                updateSelectedIcon(R.id.profile);
//
//            } else if ("Order".equals(navigateTo)) {
//                Log.d("ContainerActivity2", "Navigating to FragmentMyOrders");
//                currentFragment = new FragmentMyOrders();
//                binding.bottomNavigation.setSelectedItemId(R.id.order);
//                updateSelectedIcon(R.id.order);
//            }
//
//            loadFragment(currentFragment);
//
//        } else {
//            Log.d("ContainerActivity2", "No navigate_to extra. Loading default HomeFragment.");
//            binding.bottomNavigation.setSelectedItemId(R.id.home);
//            updateSelectedIcon(R.id.home);
//
//            if (currentFragment == null) {
//                currentFragment = new HomeFragment();
//                loadFragment(currentFragment);
//            }
//        }
//
//        // Get the fragment that's currently displayed on screen
//        Fragment displayedFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
//
//        // Update the bottom navigation to match what's actually displayed
//        if (displayedFragment instanceof FragmentProfile) {
//            // If Profile fragment is shown, select Profile tab
//            binding.bottomNavigation.setOnItemSelectedListener(null); // Temporarily remove listener
//            binding.bottomNavigation.setSelectedItemId(R.id.profile);
//            updateSelectedIcon(R.id.profile);
//            binding.bottomNavigation.setItemBackgroundResource(R.drawable.bottom_nav_icon_background);
//            binding.bottomNavigation.setOnItemSelectedListener(navigationItemSelectedListener);
//            currentFragment = displayedFragment;
//        } else if (displayedFragment instanceof HomeFragment) {
//            binding.bottomNavigation.setSelectedItemId(R.id.home);
//            updateSelectedIcon(R.id.home);
//            currentFragment = displayedFragment;
//        } else if (displayedFragment instanceof TicketFragment) {
//            binding.bottomNavigation.setSelectedItemId(R.id.ticket);
//            updateSelectedIcon(R.id.ticket);
//            currentFragment = displayedFragment;
//        } else if (displayedFragment instanceof FragmentMyOrders) {
//            binding.bottomNavigation.setSelectedItemId(R.id.order);
//            updateSelectedIcon(R.id.order);
//            currentFragment = displayedFragment;
//        } else if (displayedFragment instanceof CartFragment) {
//            binding.bottomNavigation.setSelectedItemId(R.id.cart);
//            updateSelectedIcon(R.id.cart);
//            currentFragment = displayedFragment;
//        }
//
        // Other onResume code stays the same
        isLoggedIn = pref.getPrefBoolean(this, pref.login_status);
        setupNavigationView();
        setupCartBadge();

        if (isLoggedIn) {
            binding.tvUserNsame.setText("Hi ! " + pref.getPrefString(this, pref.first_name) + " " + pref.getPrefString(this, pref.last_name));
        } else {
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
        menu.findItem(R.id.order).setIcon(R.drawable.clipboard_black);
        menu.findItem(R.id.cart).setIcon(R.drawable.shop_jd);

        // Reset the background to transparent
        binding.bottomNavigation.setItemBackgroundResource(android.R.color.transparent);

        // Set all text to black
        ColorStateList blackColor = ColorStateList.valueOf(Color.BLACK);
        binding.bottomNavigation.setItemTextColor(blackColor);
        binding.bottomNavigation.setItemIconTintList(blackColor);

        // Clear current selection - by setting an invalid ID
        binding.bottomNavigation.setSelectedItemId(0); // 0 is an invalid ID that won't be recognized

        // Remove any badge if necessary
        for (int i = 0; i < binding.bottomNavigation.getMenu().size(); i++) {
            int itemId = binding.bottomNavigation.getMenu().getItem(i).getItemId();
            BadgeDrawable badge = binding.bottomNavigation.getBadge(itemId);
            if (badge != null) {
                badge.setVisible(false);
            }
        }
        updateCartBadge(getCartItemCount());

        // Store that no fragment is currently selected in the bottom nav
        currentFragment = new WalletFragment(); // This will help with back navigation logic

        // Reattach the listener
        binding.bottomNavigation.setOnItemSelectedListener(navigationItemSelectedListener);
    }
    private void restoreBottomNavTextColors() {
        // Create a ColorStateList for normal behavior where selected item is white
        int[][] states = new int[][] {
                new int[] {android.R.attr.state_checked},  // checked state
                new int[] {-android.R.attr.state_checked}  // unchecked state
        };
        int[] colors = new int[] {
                Color.WHITE,  // selected item is white
                Color.BLACK   // unselected items are black
        };
        ColorStateList colorStateList = new ColorStateList(states, colors);

        // Restore normal text color behavior
        binding.bottomNavigation.setItemTextColor(colorStateList);
    }

    private void handleIntentNavigation(Intent intent) {
        if (intent != null && intent.hasExtra("navigate_to")) {
            String navigateTo = intent.getStringExtra("navigate_to");
            Log.d("ContainerActivity", "handleIntentNavigation: navigate_to = " + navigateTo);

            switch (navigateTo) {
                case "ticket_fragment":
                    currentFragment = new TicketFragment();
                    binding.bottomNavigation.setSelectedItemId(R.id.ticket);
                    updateSelectedIcon(R.id.ticket);
                    break;

                case "profile":
                    currentFragment = new FragmentProfile();
                    binding.bottomNavigation.setSelectedItemId(R.id.profile);
                    updateSelectedIcon(R.id.profile);
                    break;

                case "Order":
                    currentFragment = new FragmentMyOrders();
                    binding.bottomNavigation.setSelectedItemId(R.id.order);
                    updateSelectedIcon(R.id.order);
                    break;

                default:
                    currentFragment = new HomeFragment();
                    binding.bottomNavigation.setSelectedItemId(R.id.home);
                    updateSelectedIcon(R.id.home);
            }

            loadFragment(currentFragment);
        }
    }



}
