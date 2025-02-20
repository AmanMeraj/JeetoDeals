package com.deals.jeetodeals.Checkout;

import static android.widget.Toast.makeText;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.deals.jeetodeals.Fragments.FragmentsRepository;
import com.deals.jeetodeals.Fragments.FragmentsViewModel;
import com.deals.jeetodeals.Model.BillingAddress;
import com.deals.jeetodeals.Model.BillingAddressCheckout;
import com.deals.jeetodeals.Model.CartResponse;
import com.deals.jeetodeals.Model.Checkout;
import com.deals.jeetodeals.Model.GetCheckout;
import com.deals.jeetodeals.Model.ShippingAddress;
import com.deals.jeetodeals.Model.ShippingAddressCheckout;
import com.deals.jeetodeals.MyOrders.ActivityMyOrders;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.databinding.ActivityCheckoutBinding;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.gson.Gson;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeStateMappings();
        setupUI();
        handleIntentData();
        setupListeners();

        if (isInternetConnected(this)) {
            Checkout();
        } else {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
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

        // Initial visibility states
        binding.billingAddressRel.setVisibility(View.GONE);
        binding.shippingCheckBox.setVisibility(View.GONE);
        binding.shippingAddressInputText.setVisibility(View.GONE);
        binding.shippingRel.setVisibility(View.GONE);
    }

    private void handleIntentData() {
        CartResponse cartResponse = (CartResponse) getIntent().getSerializableExtra("cart_response");
        String paymentMethod = getIntent().getStringExtra("payment_method");

        if (cartResponse != null) {
            binding.subtotal.setText(cartResponse.totals.getCurrency_prefix() + " " + cartResponse.totals.getTotal_items());
            binding.shipping.setText(cartResponse.totals.getTotal_shipping());
            binding.discount.setText(cartResponse.totals.getTotal_discount());
            binding.total.setText(cartResponse.totals.getCurrency_prefix() + " " + cartResponse.totals.getTotal_price());
        } else {
            Log.e("ActivityCheckout", "CartResponse is null");
            Toast.makeText(this, "Failed to load cart data", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        // Back button
        binding.backBtn.setOnClickListener(view -> finish());

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

        // Billing address autocomplete
        MaterialAutoCompleteTextView billingAutoComplete = (MaterialAutoCompleteTextView) binding.billingAddressInputText.getEditText();
        billingAutoComplete.setOnClickListener(v -> {
            binding.billingAddressRel.setVisibility(View.VISIBLE);
            binding.shippingCheckBox.setVisibility(View.VISIBLE);
        });

        // Shipping checkbox
        binding.shippingCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.shippingAddressInputText.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked) {
                binding.shippingRel.setVisibility(View.GONE);
            }
        });

        // Shipping address autocomplete
        MaterialAutoCompleteTextView shippingAutoComplete = (MaterialAutoCompleteTextView) binding.shippingAddressInputText.getEditText();
        shippingAutoComplete.setOnClickListener(v -> binding.shippingRel.setVisibility(View.VISIBLE));

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
        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);
        String nonce = pref.getPrefString(this, pref.nonce);

        viewModel.getCheckout(auth, nonce).observe(this, response -> {
            if (response != null && response.isSuccess && response.data != null) {
                getCheckoutResponse = response;
                fillFeilds(getCheckoutResponse);
            } else {
                Log.e("Checkout Error", "Response is null or unsuccessful: " + (response != null ? response.message : "Unknown error"));
                Toast.makeText(this, response != null ? response.message : "Unknown error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillFeilds(FragmentsRepository.ApiResponse<GetCheckout> getCheckoutResponse) {

        //Order ID
        orderId= getCheckoutResponse.data.getRazorpay_order().getId();
        // Billing address
        binding.edtFirstName.setText(getCheckoutResponse.data.billing_address.getFirst_name());
        binding.edtLastName.setText(getCheckoutResponse.data.billing_address.getLast_name());
        binding.edtCompanyName.setText(getCheckoutResponse.data.billing_address.getCompany());
        binding.edtCountry.setText(getCheckoutResponse.data.billing_address.getCountry());
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
        binding.edtPhone.setText(getCheckoutResponse.data.billing_address.getPhone());
        binding.edtEmail.setText(getCheckoutResponse.data.billing_address.getEmail());

        // Shipping Address
        binding.edtFirstNameShipping.setText(getCheckoutResponse.data.shipping_address.getFirst_name());
        binding.edtLastNameShipping.setText(getCheckoutResponse.data.shipping_address.getLast_name());
        binding.edtCompanyNameShipping.setText(getCheckoutResponse.data.shipping_address.getCompany());
        binding.edtCountryShipping.setText(getCheckoutResponse.data.shipping_address.getCountry());
        binding.edtStreetAddressShipping.setText(getCheckoutResponse.data.shipping_address.getAddress_1());
        binding.edtApartmentShipping.setText(getCheckoutResponse.data.shipping_address.getAddress_2());
        binding.edtTownShipping.setText(getCheckoutResponse.data.shipping_address.getCity());

        // Convert state code to name for shipping
        String shippingStateCode = getCheckoutResponse.data.shipping_address.getState();
        String shippingStateName = stateNameMap.get(shippingStateCode);
        if (shippingStateName != null) {
            binding.edtStateShipping.setText(shippingStateName, false);
            selectedShippingStateCode = shippingStateCode;
        }

        binding.edtPinShipping.setText(getCheckoutResponse.data.shipping_address.getPostcode());
        binding.edtPhoneShipping.setText(getCheckoutResponse.data.shipping_address.getPhone());
    }

    private void setdata() {
        Checkout checkout = new Checkout();

        // Billing Address
        checkout.billing_address.first_name = binding.edtFirstName.getText().toString().trim();
        checkout.billing_address.last_name = binding.edtLastName.getText().toString().trim();
        checkout.billing_address.company = binding.edtCompanyName.getText().toString().trim();
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
            checkout.shipping_address.company = binding.edtCompanyNameShipping.getText().toString().trim();
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

        // Payment Data
        checkout.payment_data = new ArrayList<>();
        Checkout.PaymentData paymentData = new Checkout.PaymentData();
        paymentData.razorpay_order_id = String.valueOf(orderId);
        checkout.payment_data.add(paymentData);

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

        if ("razorpay".equals(paymentMethod)) {
            startRazorpayPayment();
        } else if ("wallet".equals(paymentMethod)) {
            processWalletPayment(checkout);
        } else {
            Toast.makeText(this, "Invalid payment method", Toast.LENGTH_SHORT).show();
        }
    }
    private void startRazorpayPayment() {
        com.razorpay.Checkout razorpayCheckout = new com.razorpay.Checkout();
        razorpayCheckout.setKeyID("rzp_test_eB9tKqgSGeVVtQ"); // Note: it's setKeyID, not setKey

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Jeeto Deals");
            options.put("description", "Order Payment");
            options.put("currency", "INR");
            // Remove non-numeric characters and convert to paise
            String amountStr = binding.total.getText().toString().replaceAll("[^0-9.]", "");
            double amount = Double.parseDouble(amountStr) * 100;
            options.put("amount", (int)amount);

            // Add theme color (optional)
            options.put("theme.color", R.color.orange);

            // Add customer information
            JSONObject preFill = new JSONObject();
            preFill.put("email", checkoutData.billing_address.email);
            preFill.put("contact", checkoutData.billing_address.phone);
            options.put("prefill", preFill);

            razorpayCheckout.open(ActivityCheckout.this, options);
        } catch (Exception e) {
            Log.e("RAZORPAY", "Error in starting payment: " + e.getMessage());
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
        Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();

        // Update payment data
        checkoutData.payment_data = new ArrayList<>();
        Checkout.PaymentData payment = new Checkout.PaymentData();
        payment.razorpay_payment_id = razorpayPaymentId;
        checkoutData.payment_data.add(payment);

        // Process the final checkout
        processWalletPayment(checkoutData);
    }

    @Override
    public void onPaymentError(int code, String description, PaymentData paymentData) {
        Log.e("Razorpay Error", "Payment failed: " + description);
        Toast.makeText(this, "Payment failed: " + description, Toast.LENGTH_SHORT).show();
    }

    private void processWalletPayment(Checkout checkout) {
        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);
        String nonce = pref.getPrefString(this, pref.nonce);

        int retryCount = 0;
        int maxRetries = 3;

        while (retryCount < maxRetries) {
            try {
                viewModel.postCheckout(auth, nonce, checkout).observe(this, response -> {
                    if (response != null && response.isSuccess && response.data != null) {
                        responsee = response.data;
                        Intent intent = new Intent(ActivityCheckout.this, ActivityMyOrders.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e("Checkout Error", "Response is null or unsuccessful: " +
                                (response != null ? response.message : "Unknown error"));
                        Toast.makeText(this, response != null ? response.message :
                                "Unknown error", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            } catch (Exception e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    Log.e("Checkout Error", "API timed out after " + maxRetries +
                            " retries: " + e.getMessage());
                    Toast.makeText(this, "Payment timed out. Please check your order status.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}