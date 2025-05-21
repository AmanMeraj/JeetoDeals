package com.deals.jeetodeals.Checkout;

import static android.widget.Toast.makeText;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.deals.jeetodeals.ContainerActivity.ContainerActivity;
import com.deals.jeetodeals.Fragments.FragmentsRepository;
import com.deals.jeetodeals.Fragments.FragmentsViewModel;
import com.deals.jeetodeals.Model.CartResponse;
import com.deals.jeetodeals.Model.Checkout;
import com.deals.jeetodeals.Model.CouponResponse;
import com.deals.jeetodeals.Model.GetCheckout;
import com.deals.jeetodeals.MyOrders.FragmentMyOrders;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.databinding.ActivityCheckoutBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ActivityCheckout extends Utility implements PaymentResultWithDataListener {
    ActivityCheckoutBinding binding;
    String[] countryNames = {"India"};
    String[] stateNames = {"Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal"};
    String countryCode = "IN";
    FragmentsViewModel viewModel;
    private Map<String, String> stateCodeMap;
    private Map<String, String> stateNameMap;
    String selectedBillingStateCode,selectedShippingStateCode,orderId;
    CheckoutResponse responsee;
    CartResponse cartResponse;
    private Checkout checkoutData;
    private static final String PAYMENT_METHOD_WALLET = "wallet";
    private static final String PAYMENT_METHOD_RAZORPAY = "razorpay";
    FragmentsRepository.ApiResponse<GetCheckout> getCheckoutResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(FragmentsViewModel.class);
         cartResponse = (CartResponse) getIntent().getSerializableExtra("cart_response");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeStateMappings();
        setupUI();
        handleIntentData();
        setupListeners();

        String  type=cartResponse.getItems().get(0).getType();
        if("lottery".equals(type)){
            binding.couponBox.setVisibility(View.VISIBLE);
        }else{
            binding.couponBox.setVisibility(View.GONE);
            binding.removeCouponBtn.setVisibility(View.GONE);
        }

        if (isInternetConnected(this)) {
            Checkout();
        } else {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }

        TextInputLayout billingInputLayout = findViewById(R.id.billing_address_Input_text);
        RelativeLayout billingContentLayout = findViewById(R.id.billing_address_rel);

        binding.applyBtn.setEnabled(!binding.edtCoupon.getText().toString().isEmpty());

// Create a custom click listener for the entire field
        View.OnClickListener billingToggleListener = v -> {
            // Toggle visibility of content
            boolean isExpanding = billingContentLayout.getVisibility() != View.VISIBLE;
            billingContentLayout.setVisibility(isExpanding ? View.VISIBLE : View.GONE);

            // We need to use a different approach to change the dropdown icon
            // since we don't have direct access to it
            try {
                // Try to access the end icon via reflection
                Field endIconField = TextInputLayout.class.getDeclaredField("endIconView");
                endIconField.setAccessible(true);
                ImageView endIconView = (ImageView) endIconField.get(billingInputLayout);

                if (endIconView != null) {
                    // Animate the rotation of the icon
                    endIconView.animate()
                            .rotation(isExpanding ? 180f : 0f)
                            .setDuration(200)
                            .start();
                }
            } catch (Exception e) {
                Log.e("AddressActivity", "Failed to access end icon", e);
            }
        };

// Set the click listener on the TextInputLayout
        billingInputLayout.setOnClickListener(billingToggleListener);

// Also set it on the EditText inside
        MaterialAutoCompleteTextView billingAutoComplete =
                (MaterialAutoCompleteTextView) billingInputLayout.getEditText();
        if (billingAutoComplete != null) {
            billingAutoComplete.setOnClickListener(billingToggleListener);
        }

// Most importantly, set a click listener on the end icon itself
        billingInputLayout.setEndIconOnClickListener(billingToggleListener);

        binding.edtCoupon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable or disable the button based on trimmed text
                String input = s.toString().trim();
                binding.applyBtn.setEnabled(!input.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }
    private void initializeStateMappings() {
        stateCodeMap = new HashMap<>();
        stateNameMap = new HashMap<>();

        stateCodeMap.put("Andhra Pradesh", "AP");
        stateCodeMap.put("Arunachal Pradesh", "AR");
        stateCodeMap.put("Assam", "AS");
        stateCodeMap.put("Bihar", "BR");
        stateCodeMap.put("Chhattisgarh", "CG");
        stateCodeMap.put("Goa", "GA");
        stateCodeMap.put("Gujarat", "GJ");
        stateCodeMap.put("Haryana", "HR");
        stateCodeMap.put("Himachal Pradesh", "HP");
        stateCodeMap.put("Jharkhand", "JH");
        stateCodeMap.put("Karnataka", "KA");
        stateCodeMap.put("Kerala", "KL");
        stateCodeMap.put("Madhya Pradesh", "MP");
        stateCodeMap.put("Maharashtra", "MH");
        stateCodeMap.put("Manipur", "MN");
        stateCodeMap.put("Meghalaya", "ML");
        stateCodeMap.put("Mizoram", "MZ");
        stateCodeMap.put("Nagaland", "NL");
        stateCodeMap.put("Odisha", "OD");
        stateCodeMap.put("Punjab", "PB");
        stateCodeMap.put("Rajasthan", "RJ");
        stateCodeMap.put("Sikkim", "SK");
        stateCodeMap.put("Tamil Nadu", "TN");
        stateCodeMap.put("Telangana", "TG");
        stateCodeMap.put("Tripura", "TR");
        stateCodeMap.put("Uttar Pradesh", "UP");
        stateCodeMap.put("Uttarakhand", "UK");
        stateCodeMap.put("West Bengal", "WB");

        // Create reverse mapping
        for (Map.Entry<String, String> entry : stateCodeMap.entrySet()) {
            stateNameMap.put(entry.getValue(), entry.getKey());
        }
    }

    private void setupUI() {
        // Set up adapters
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, countryNames);
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, stateNames);

        binding.edtCountry.setAdapter(countryAdapter);
        binding.edtCountryShipping.setAdapter(countryAdapter);
        binding.edtState.setAdapter(stateAdapter);
        binding.edtStateShipping.setAdapter(stateAdapter);

        // Pre-select India
        binding.edtCountry.setText(countryNames[0], false);
        binding.edtCountryShipping.setText(countryNames[0], false);

        // Disable dropdown (prevent opening)
        binding.edtCountry.setFocusable(false);
        binding.edtCountry.setClickable(false);
        binding.edtCountry.setOnTouchListener((v, event) -> true); // Blocks touch event

        binding.edtCountryShipping.setFocusable(false);
        binding.edtCountryShipping.setClickable(false);
        binding.edtCountryShipping.setOnTouchListener((v, event) -> true);

        // Initial visibility states
        binding.billingAddressRel.setVisibility(View.GONE);
        binding.shippingCheckBox.setVisibility(View.GONE);
        binding.shippingAddressInputText.setVisibility(View.GONE);
        binding.shippingRel.setVisibility(View.GONE);
    }



    // Replace the existing handleIntentData() method with this one
    private void handleIntentData() {

        String paymentMethod = getIntent().getStringExtra("payment_method");

        if (cartResponse != null) {
            if (cartResponse.getItems() != null && !cartResponse.getItems().isEmpty() && cartResponse.getItems().get(0) != null) {
                String itemType = cartResponse.getItems().get(0).getType();
                Log.d("ActivityCheckout", "Item Type: " + itemType);

                // Check if item type is lottery
                if ("lottery".equals(itemType)) {
                    // For lottery items, forcefully hide all shipping-related UI elements
                    binding.shippingCheckBox.setVisibility(View.GONE);
                    binding.shippingAddressInputText.setVisibility(View.GONE);
                    binding.shippingRel.setVisibility(View.GONE);

                    if(cartResponse.totals.getTotal_discount().equals("0")){
                        binding.removeCouponBtn.setVisibility(View.GONE);
                    }else {
                        binding.removeCouponBtn.setVisibility(View.VISIBLE);
                    }

                    // Uncheck the shipping checkbox to prevent any shipping fields from being processed
                    binding.shippingCheckBox.setChecked(false);

                    binding.subtotal.setText(cartResponse.totals.getCurrency_code() + " " + cartResponse.totals.getTotal_items());
                    binding.total.setText(cartResponse.totals.getCurrency_code() + " " + cartResponse.totals.getTotal_price());
                    binding.shipping.setText(cartResponse.totals.getTotal_shipping());
                    binding.discount.setText("(-) "+cartResponse.totals.getCurrency_code() + " " +cartResponse.totals.getTotal_discount());
                } else {
                    // For non-lottery items, show shipping options
                    binding.subtotal.setText(cartResponse.totals.getCurrency_prefix() + " " + cartResponse.totals.getTotal_price());
                    binding.total.setText(cartResponse.totals.getCurrency_prefix() + " " + cartResponse.totals.getTotal_price());
                    binding.discount.setText("(-) "+cartResponse.totals.getCurrency_prefix() + " " +cartResponse.totals.getTotal_discount());

                }
            } else {
                Log.e("ActivityCheckout", "Cart items list is null or empty");
            }

            binding.shipping.setText(cartResponse.totals.getTotal_shipping());
        } else {
            Log.e("ActivityCheckout", "CartResponse is null");
            Toast.makeText(this, "Failed to load cart data", Toast.LENGTH_SHORT).show();
        }
    }

    // Also update setupListeners() to maintain proper state of shipping UI elements
    private void setupListeners() {
        // Back button
        binding.backBtn.setOnClickListener(view -> finish());




        binding.removeCouponBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeCoupon();
            }
        });

        binding.applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyCoupon();
            }
        });

        // Country selection
        binding.edtCountry.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCountry = parent.getItemAtPosition(position).toString();
            if (selectedCountry.equals("India")) {
                countryCode = "IN";
            }
        });

        // State selection for billing
        binding.edtState.setOnItemClickListener((parent, view, position, id) -> {
            String selectedState = parent.getItemAtPosition(position).toString();
            selectedBillingStateCode = stateCodeMap.get(selectedState);
            Log.d("Selected Billing State", "State: " + selectedState + ", Code: " + selectedBillingStateCode);
        });

        // State selection for shipping
        binding.edtStateShipping.setOnItemClickListener((parent, view, position, id) -> {
            String selectedState = parent.getItemAtPosition(position).toString();
            selectedShippingStateCode = stateCodeMap.get(selectedState);
            Log.d("Selected Shipping State", "State: " + selectedState + ", Code: " + selectedShippingStateCode);
        });

        // Billing address autocomplete - toggle visibility
        MaterialAutoCompleteTextView billingAutoComplete = (MaterialAutoCompleteTextView) binding.billingAddressInputText.getEditText();
        billingAutoComplete.setOnClickListener(v -> {
            if (binding.billingAddressRel.getVisibility() == View.VISIBLE) {
                binding.billingAddressRel.setVisibility(View.GONE);
                // Only hide the shipping checkbox if shipping rel is also not visible
                if (binding.shippingRel.getVisibility() != View.VISIBLE) {
                    // First check if this is a lottery item
                    CartResponse cartResponse = (CartResponse) getIntent().getSerializableExtra("cart_response");
                    boolean isLottery = false;

                    if (cartResponse != null && cartResponse.getItems() != null &&
                            !cartResponse.getItems().isEmpty() && cartResponse.getItems().get(0) != null) {
                        isLottery = "lottery".equals(cartResponse.getItems().get(0).getType());
                    }

                    // Only hide shipping checkbox if it's not a lottery item
                    if (!isLottery) {
                        binding.shippingCheckBox.setVisibility(View.GONE);
                    }
                }
            } else {
                binding.billingAddressRel.setVisibility(View.VISIBLE);

                // First check if this is a lottery item
                CartResponse cartResponse = (CartResponse) getIntent().getSerializableExtra("cart_response");
                boolean isLottery = false;

                if (cartResponse != null && cartResponse.getItems() != null &&
                        !cartResponse.getItems().isEmpty() && cartResponse.getItems().get(0) != null) {
                    isLottery = "lottery".equals(cartResponse.getItems().get(0).getType());
                }

                // Only show shipping checkbox if it's not a lottery item
                if (!isLottery) {
                    binding.shippingCheckBox.setVisibility(View.VISIBLE);
                }
            }
        });

        // Shipping checkbox
        binding.shippingCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.shippingAddressInputText.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked) {
                binding.shippingRel.setVisibility(View.GONE);
            }
        });

        // Shipping address autocomplete - toggle visibility
        MaterialAutoCompleteTextView shippingAutoComplete = (MaterialAutoCompleteTextView) binding.shippingAddressInputText.getEditText();
        shippingAutoComplete.setOnClickListener(v -> {
            if (binding.shippingRel.getVisibility() == View.VISIBLE) {
                binding.shippingRel.setVisibility(View.GONE);
            } else {
                binding.shippingRel.setVisibility(View.VISIBLE);
            }
        });

        // Checkout button
        binding.checkoutBtn.setOnClickListener(view -> {
            if (isInternetConnected(ActivityCheckout.this)) {
                setdata();
            } else {
                Toast.makeText(ActivityCheckout.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
            }
        });

        setupEmailValidation();
    }

    private void removeCoupon() {
        binding.loader.rlLoader.setVisibility(View.VISIBLE);

        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);
        String nonce = pref.getPrefString(this, pref.nonce);

        viewModel.removeCoupon(auth, nonce).observe(this, response -> {
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response != null && response.isSuccess && response.data != null) {
                binding.edtCoupon.setText("");
                // Clear discount UI
                binding.discount.setText("(-) INR 0");
                binding.discount.setTextColor(binding.subtotal.getCurrentTextColor());
                binding.removeCouponBtn.setVisibility(View.GONE);

                // Restore original cart total
                String originalTotal = pref.getPrefString(this, "cart_total");
                String currencyPrefix = "INR "; // Or fetch from preferences or API
                binding.total.setText(currencyPrefix + cartResponse.totals.getTotal_items());

                // Remove stored discounted values
                pref.setPrefString(this, "discounted_total", originalTotal);
                pref.setPrefString(this, "coupon_response", "");

                Toast.makeText(this, response.message != null ? response.message : "Coupon removed successfully", Toast.LENGTH_SHORT).show();
            } else {
                binding.edtCoupon.setText("");
                Log.e("RemoveCoupon", "Coupon removal failed: " + (response != null ? response.message : "Unknown error"));
                Toast.makeText(this, response != null ? response.message : "Failed to remove coupon", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupEmailValidation() {
        TextWatcher emailValidator = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = s.toString();
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.isEmpty()) {
                    binding.textFieldEmail.setError("Please enter a valid email address");
                } else {
                    binding.textFieldEmail.setError(null);
                }
            }
        };

        binding.edtEmail.addTextChangedListener(emailValidator);
    }

    private void Checkout() {
        binding.loader.rlLoader.setVisibility(View.VISIBLE);
        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);
        String nonce = pref.getPrefString(this, pref.nonce);

        viewModel.getCheckout(auth, nonce).observe(this, response -> {
            if (response != null && response.isSuccess && response.data != null) {

                getCheckoutResponse = response;
                fillFeilds(getCheckoutResponse);
            } else {
                binding.loader.rlLoader.setVisibility(View.GONE);
                Log.e("Checkout Error", "Response is null or unsuccessful: " + (response != null ? response.message : "Unknown error"));
                Toast.makeText(this, response != null ? response.message : "Unknown error", Toast.LENGTH_SHORT).show();
            }
            binding.loader.rlLoader.setVisibility(View.GONE);
        });
    }
    private void applyCoupon() {
        binding.loader.rlLoader.setVisibility(View.VISIBLE);

        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);
        String nonce = pref.getPrefString(this, pref.nonce);
        String couponCode = binding.edtCoupon.getText().toString().trim();

        viewModel.applyCoupon(auth, nonce, couponCode).observe(this, response -> {
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response != null && response.isSuccess && response.data != null) {
                // Since response.data is a List<CouponResponse>, get the first item
                binding.edtCoupon.setText("");
                CouponResponse couponResponse = response.data;
                binding.removeCouponBtn.setVisibility(View.VISIBLE);

                if (couponResponse != null && couponResponse.getTotals() != null) {
                    CouponResponse.Totals totals = couponResponse.getTotals();

                    String currencyPrefix = "(-) INR";
                    String totalDiscount = totals.getTotalDiscount();

                    // Format and display the discount
                    if (totalDiscount != null && !totalDiscount.equals("0")) {
                        binding.discount.setText("(-) INR " + totalDiscount);
                        binding.discount.setTextColor(Color.BLACK);
                    } else {
                        binding.discount.setText(currencyPrefix + "0.00");
                        binding.discount.setTextColor(binding.subtotal.getCurrentTextColor());
                    }

                    // Calculate new total (Assuming you already have subtotal and shipping elsewhere)
                    try {
                        String subtotalStr = cartResponse.getTotals().getTotal_items();
                        if (TextUtils.isEmpty(subtotalStr)) subtotalStr = "0";
                        double subTotal = Double.parseDouble(subtotalStr);

                        Log.d("ApplyCoupon", "Raw totalDiscount: " + totalDiscount);

                        String sanitizedDiscount = totalDiscount.replaceAll("[^\\d.]+", "").trim();
                        double discountValue = Double.parseDouble(sanitizedDiscount);

                        double finalTotal = subTotal - discountValue;

                        binding.total.setText( "INR "+String.format("%.2f", finalTotal));
                        pref.setPrefString(this, "discounted_total", String.valueOf(finalTotal));

                        // Save coupon response for future use
                        String couponResponseJson = new Gson().toJson(couponResponse);
                        pref.setPrefString(this, "coupon_response", couponResponseJson);

                        Toast.makeText(this, "Coupon applied successfully", Toast.LENGTH_SHORT).show();

                    } catch (NumberFormatException e) {
                        Log.e("ApplyCoupon", "Error parsing values: " + e.getMessage());
                        Toast.makeText(this, "Error calculating discount", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    binding.edtCoupon.setText("");
                    Toast.makeText(this, "Invalid coupon response", Toast.LENGTH_SHORT).show();
                }
            } else {
                binding.edtCoupon.setText("");
                Log.e("ApplyCoupon", "Coupon failed: " + (response != null ? response.message : "Unknown error"));
                assert response != null;
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fillFeilds(FragmentsRepository.ApiResponse<GetCheckout> getCheckoutResponse) {
        // Retrieve stored values from SharedPreferences
        String savedFirstName = pref.getPrefString(this,pref.first_name);
        String savedLastName = pref.getPrefString(this,pref.last_name);
        String savedPhone = pref.getPrefString(this,pref.mobile);
        String savedEmail = pref.getPrefString(this,pref.user_email);

        // Order ID
        // orderId = getCheckoutResponse.data.getRazorpay_order().getId();

        // Billing Address
        String firstName = getCheckoutResponse.data.billing_address.getFirst_name();
        String lastName = getCheckoutResponse.data.billing_address.getLast_name();
        String phone = getCheckoutResponse.data.billing_address.getPhone();
        String email = getCheckoutResponse.data.billing_address.getEmail();

        binding.edtFirstName.setText(!TextUtils.isEmpty(firstName) ? firstName : savedFirstName);
        binding.edtLastName.setText(!TextUtils.isEmpty(lastName) ? lastName : savedLastName);
        binding.edtPhone.setText(!TextUtils.isEmpty(phone) ? phone : savedPhone);
        binding.edtEmail.setText(!TextUtils.isEmpty(email) ? email : savedEmail);

        binding.edtStreetAddress.setText(getCheckoutResponse.data.billing_address.getAddress_1());
        binding.edtApartment.setText(getCheckoutResponse.data.billing_address.getAddress_2());
        binding.edtTown.setText(getCheckoutResponse.data.billing_address.getCity());

        // Convert state code to name for billing
        String billingStateCode = getCheckoutResponse.data.billing_address.getState();
        String billingStateName = stateNameMap.get(billingStateCode);
        if (billingStateName != null) {
            binding.edtState.setText(billingStateName, false);
            selectedBillingStateCode = billingStateCode;
        }

        binding.edtPin.setText(getCheckoutResponse.data.billing_address.getPostcode());

        // Shipping Address
        binding.edtFirstNameShipping.setText(getCheckoutResponse.data.shipping_address.getFirst_name());
        binding.edtLastNameShipping.setText(getCheckoutResponse.data.shipping_address.getLast_name());
        binding.edtStreetAddressShipping.setText(getCheckoutResponse.data.shipping_address.getAddress_1());
        binding.edtApartmentShipping.setText(getCheckoutResponse.data.shipping_address.getAddress_2());
        binding.edtTownShipping.setText(getCheckoutResponse.data.shipping_address.getCity());
        binding.edtPhoneShipping.setText(getCheckoutResponse.data.shipping_address.getPhone());
        binding.edtFirstNameShipping.setText(!TextUtils.isEmpty(firstName) ? firstName : savedFirstName);
        binding.edtLastNameShipping.setText(!TextUtils.isEmpty(lastName) ? lastName : savedLastName);
        binding.edtPhoneShipping.setText(!TextUtils.isEmpty(phone) ? phone : savedPhone);
        // Convert state code to name for shipping
        String shippingStateCode = getCheckoutResponse.data.shipping_address.getState();
        String shippingStateName = stateNameMap.get(shippingStateCode);
        if (shippingStateName != null) {
            binding.edtStateShipping.setText(shippingStateName, false);
            selectedShippingStateCode = shippingStateCode;
        }

        binding.edtPinShipping.setText(getCheckoutResponse.data.shipping_address.getPostcode());


        binding.loader.rlLoader.setVisibility(View.GONE);
    }

    private void setdata() {
        Checkout checkout = new Checkout();

        // Billing Address
        checkout.billing_address.first_name = binding.edtFirstName.getText().toString().trim();
        checkout.billing_address.last_name = binding.edtLastName.getText().toString().trim();
        checkout.billing_address.address_1 = binding.edtStreetAddress.getText().toString().trim();
        checkout.billing_address.address_2 = binding.edtApartment.getText().toString().trim();
        checkout.billing_address.city = binding.edtTown.getText().toString().trim();
        checkout.billing_address.state = selectedBillingStateCode; // Use the stored state code
        checkout.billing_address.postcode = binding.edtPin.getText().toString().trim();
        checkout.billing_address.country = countryCode; // Use country code instead of name
        checkout.billing_address.email = binding.edtEmail.getText().toString().trim();
        checkout.billing_address.phone = binding.edtPhone.getText().toString().trim();

        // Log billing address
        Log.d("CHECKOUT_DATA", "Billing Address: " + new Gson().toJson(checkout.billing_address));

        // Shipping Address
        if (binding.shippingCheckBox.isChecked()) {
            checkout.shipping_address.first_name = binding.edtFirstNameShipping.getText().toString().trim();
            checkout.shipping_address.last_name = binding.edtLastNameShipping.getText().toString().trim();
            checkout.shipping_address.address_1 = binding.edtStreetAddressShipping.getText().toString().trim();
            checkout.shipping_address.address_2 = binding.edtApartmentShipping.getText().toString().trim();
            checkout.shipping_address.city = binding.edtTownShipping.getText().toString().trim();
            checkout.shipping_address.state = selectedShippingStateCode; // Use the stored state code
            checkout.shipping_address.postcode = binding.edtPinShipping.getText().toString().trim();
            checkout.shipping_address.country = countryCode; // Use country code instead of name
            checkout.shipping_address.phone = binding.edtPhoneShipping.getText().toString().trim();
        } else {
            checkout.shipping_address = checkout.billing_address;
        }

        // Log shipping address
        Log.d("CHECKOUT_DATA", "Shipping Address: " + new Gson().toJson(checkout.shipping_address));

        // Other fields
        checkout.customer_note = binding.edtOtherNotesShipping.getText().toString().trim();
        checkout.create_account = false;
        checkout.payment_method = getIntent().getStringExtra("payment_method");

        // Extensions
        checkout.extensions = new HashMap<>();
        Map<String, String> extensionData = new HashMap<>();
        extensionData.put("some-data-key", "some data value");
        checkout.extensions.put("some-extension-name", extensionData);

        // Validate required fields
        if (validateCheckoutData(checkout)) {
            proceedToPayment(checkout);
        }
    }

    private boolean validateCheckoutData(Checkout checkout) {
        // Validate billing address
        if (checkout.billing_address.first_name.isEmpty()) {
            Toast.makeText(this, "Please enter billing first name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (checkout.billing_address.last_name.isEmpty()) {
            Toast.makeText(this, "Please enter billing last name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (checkout.billing_address.address_1.isEmpty()) {
            Toast.makeText(this, "Please enter billing address", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (checkout.billing_address.city.isEmpty()) {
            Toast.makeText(this, "Please enter billing city", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (checkout.billing_address.state == null || checkout.billing_address.state.isEmpty()) {
            Toast.makeText(this, "Please select billing state", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (checkout.billing_address.postcode.isEmpty()) {
            Toast.makeText(this, "Please enter billing postal code", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (checkout.billing_address.phone.isEmpty()) {
            Toast.makeText(this, "Please enter billing phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (checkout.billing_address.email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(checkout.billing_address.email).matches()) {
            Toast.makeText(this, "Please enter valid billing email", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate shipping address if different from billing
        if (binding.shippingCheckBox.isChecked()) {
            if (checkout.shipping_address.first_name.isEmpty()) {
                Toast.makeText(this, "Please enter shipping first name", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (checkout.shipping_address.last_name.isEmpty()) {
                Toast.makeText(this, "Please enter shipping last name", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (checkout.shipping_address.address_1.isEmpty()) {
                Toast.makeText(this, "Please enter shipping address", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (checkout.shipping_address.city.isEmpty()) {
                Toast.makeText(this, "Please enter shipping city", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (checkout.shipping_address.state == null || checkout.shipping_address.state.isEmpty()) {
                Toast.makeText(this, "Please select shipping state", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (checkout.shipping_address.postcode.isEmpty()) {
                Toast.makeText(this, "Please enter shipping postal code", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (checkout.shipping_address.phone.isEmpty()) {
                Toast.makeText(this, "Please enter shipping phone number", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private void proceedToPayment(Checkout checkout) {
        this.checkoutData = checkout;
        String paymentMethod = getIntent().getStringExtra("payment_method");
        binding.loader.rlLoader.setVisibility(View.VISIBLE);

        // Check if a coupon has been applied and retrieve the discounted total
        String discountedTotal = pref.getPrefString(this, "discounted_total");
        if (!TextUtils.isEmpty(discountedTotal)) {
            // If we have a discounted total, use it for the payment amount
            Log.d("Checkout", "Using discounted total for payment: " + discountedTotal);
            // You might want to add this to the checkout object if needed
            // For example, if your backend needs this information:
            // checkout.setDiscountedTotal(discountedTotal);
        } else {
            // No discount applied, use the regular total
            Log.d("Checkout", "Using regular total for payment");
        }

        if ("razorpay".equals(paymentMethod)) {
            processWalletPayment(checkout);
        } else if ("wallet".equals(paymentMethod)) {
            processWalletPayment(checkout);
        } else {
            Toast.makeText(this, "Invalid payment method", Toast.LENGTH_SHORT).show();
        }
    }
    private void startRazorpayPayment() {
        com.razorpay.Checkout razorpayCheckout = new com.razorpay.Checkout();
        razorpayCheckout.setKeyID(pref.getPrefString(this, pref.payment_key));

        // Log the initialization
        Log.d("RAZORPAY_DEBUG", "Initializing Razorpay payment");
        Log.d("RAZORPAY_DEBUG", "Key ID: " + pref.getPrefString(this, pref.payment_key));
        Log.d("RAZORPAY_DEBUG", "Order ID: " + orderId);

        try {
            // Log billing address details
            Log.d("RAZORPAY_DEBUG", "Billing Details:");
            Log.d("RAZORPAY_DEBUG", "Email: " + checkoutData.billing_address.email);
            Log.d("RAZORPAY_DEBUG", "Phone: " + checkoutData.billing_address.phone);

            // Check if we have a discounted total to use
            String discountedTotal = pref.getPrefString(this, "discounted_total");
            double amount;

            if (!TextUtils.isEmpty(discountedTotal)) {
                // Use the discounted total (already calculated in applyCoupon)
                amount = Double.parseDouble(discountedTotal) * 100;
                Log.d("RAZORPAY_DEBUG", "Using discounted amount: " + discountedTotal);
            } else {
                // Fall back to the original total shown in the UI
                String amountStr = binding.total.getText().toString();
                Log.d("RAZORPAY_DEBUG", "Original amount string: " + amountStr);

                amountStr = amountStr.replaceAll("[^0-9.]", "");
                Log.d("RAZORPAY_DEBUG", "Cleaned amount string: " + amountStr);

                amount = Double.parseDouble(amountStr) * 100;
                Log.d("RAZORPAY_DEBUG", "Using regular amount from UI");
            }

            Log.d("RAZORPAY_DEBUG", "Final amount in paise: " + (int)amount);

            JSONObject options = new JSONObject();
            options.put("name", "Jeeto Deals");
            options.put("description", "Order Payment");
            options.put("order_id", orderId);
            options.put("currency", "INR");
            options.put("amount", (int)amount);
            options.put("theme.color", R.color.orange);

            JSONObject preFill = new JSONObject();
            preFill.put("email", checkoutData.billing_address.email);
            preFill.put("contact", checkoutData.billing_address.phone);
            options.put("prefill", preFill);

            // Log the final options object
            Log.d("RAZORPAY_DEBUG", "Final Razorpay options:");
            Log.d("RAZORPAY_DEBUG", options.toString(4)); // Pretty print JSON with 4 spaces indentation

            razorpayCheckout.open(ActivityCheckout.this, options);
            Log.d("RAZORPAY_DEBUG", "Razorpay checkout opened successfully");

        } catch (Exception e) {
            Log.e("RAZORPAY_ERROR", "Stack trace: ", e);
            Log.e("RAZORPAY_ERROR", "Error message: " + e.getMessage());

            if (e instanceof JSONException) {
                Log.e("RAZORPAY_ERROR", "JSON creation failed");
            } else if (e instanceof NumberFormatException) {
                Log.e("RAZORPAY_ERROR", "Amount parsing failed");
            }

            Toast.makeText(this, "Error in payment process: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("billing_visibility", binding.billingAddressRel.getVisibility());
        outState.putInt("shipping_visibility", binding.shippingRel.getVisibility());
        outState.putBoolean("shipping_checked", binding.shippingCheckBox.isChecked());
        outState.putString("selected_billing_state_code", selectedBillingStateCode);
        outState.putString("selected_shipping_state_code", selectedShippingStateCode);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            binding.billingAddressRel.setVisibility(savedInstanceState.getInt("billing_visibility"));
            binding.shippingRel.setVisibility(savedInstanceState.getInt("shipping_visibility"));
            binding.shippingCheckBox.setChecked(savedInstanceState.getBoolean("shipping_checked"));
            selectedBillingStateCode = savedInstanceState.getString("selected_billing_state_code");
            selectedShippingStateCode = savedInstanceState.getString("selected_shipping_state_code");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentId, PaymentData paymentData) {
        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);
        String cartId = pref.getPrefString(this,pref.nonce); // Make sure you pass the correct cart ID

        viewModel.deleteItemInCart(auth, cartId).observe(this, deleteResponse -> {
            if (deleteResponse != null && deleteResponse.isSuccess) {
                // Step 2: If cart item is successfully deleted, proceed with payment success logic
                Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();

                // Create intent to return to ContainerActivity
                Intent intent = new Intent(ActivityCheckout.this, ContainerActivity.class);

                // Add extra to indicate we should show the TicketFragment
                intent.putExtra("navigate_to", "ticket_fragment");

                // Start activity and finish current one
                startActivity(intent);
                finish();
            } else {
                // If deletion fails, show an error message
                Toast.makeText(this, "Failed to delete cart item: " + (deleteResponse != null ? deleteResponse.message : "Unknown error"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPaymentError(int code, String description, PaymentData paymentData) {
        Log.e("Razorpay Error", "Payment failed: Code: " + code + ", Description: " + description);

        // Extract plain text from HTML if present
        if (description != null && description.contains("<")) {
            // For Android N and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Spanned spannedText = Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY);
                String plainText = spannedText.toString();
                Toast.makeText(this, "Payment failed: " + plainText, Toast.LENGTH_LONG).show();
            } else {
                // For older Android versions
                Spanned spannedText = Html.fromHtml(description);
                String plainText = spannedText.toString();
                Toast.makeText(this, "Payment failed: " + plainText, Toast.LENGTH_LONG).show();
            }
        } else {
            // No HTML content, display as is
            Toast.makeText(this, "Payment failed ", Toast.LENGTH_LONG).show();
            Log.d("TAG", "onPaymentError: "+description);
        }


    }


    private void processWalletPayment(Checkout checkout) {
        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);
        String nonce = pref.getPrefString(this, pref.nonce);

        binding.loader.rlLoader.setVisibility(View.VISIBLE);
        attemptCheckout(auth, nonce, checkout, 0, 3);
    }

    private void attemptCheckout(String auth, String nonce, Checkout checkout, int currentRetry, int maxRetries) {
        viewModel.postCheckout(auth, nonce, checkout).observe(this, response -> {
            if (response != null && response.isSuccess && response.data != null) {
                // Success case
                binding.loader.rlLoader.setVisibility(View.GONE);
                responsee = response.data;
                orderId = responsee.getRazorpay_order().getId();
                Log.d("RAZORPAY", "processWalletPayment: " + orderId);


                if (responsee.payment_method.matches("razorpay")) {
                    startRazorpayPayment();
                } else {
                   Toast.makeText(this, "Order Placed Successfully!", Toast.LENGTH_SHORT).show();
                    navigateToOrdersScreen();
                }
            } else {
                // Handle failure - check if we should retry
                if (currentRetry < maxRetries - 1) {
                    // Retry after delay
                    binding.loader.rlLoader.setVisibility(View.VISIBLE);
                    Log.d("Checkout Retry", "Attempt " + (currentRetry + 1) + " failed. Retrying in 2 seconds.");

                    new Handler().postDelayed(() -> {
                        // Get fresh nonce if needed
                        String freshNonce = pref.getPrefString(this, pref.nonce);
                        attemptCheckout(auth, freshNonce, checkout, currentRetry + 1, maxRetries);
                    }, 2000);
                } else {
                    // All retries failed
                    binding.loader.rlLoader.setVisibility(View.GONE);
                    String errorMessage = (response != null && response.message != null) ?
                            response.message : "Payment request failed after multiple attempts";
                    Log.e("Checkout Error", errorMessage);
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void navigateToOrdersScreen() {
        Log.d("Checkout", "Navigating to My Orders Fragment via ContainerActivity");

        Intent intent = new Intent(this, ContainerActivity.class);
        intent.putExtra("navigate_to", "Order");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish(); // Optional: Close current activity
    }




}