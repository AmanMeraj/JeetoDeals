package com.deals.jeetodeals.Checkout;

import static android.widget.Toast.makeText;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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

public class ActivityCheckout extends Utility {
ActivityCheckoutBinding binding;
FragmentsViewModel viewModel;
CheckoutResponse responsee;
FragmentsRepository.ApiResponse<GetCheckout> getCheckoutResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel= new ViewModelProvider(this).get(FragmentsViewModel.class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        CartResponse cartResponse = (CartResponse) getIntent().getSerializableExtra("cart_response");
        String paymentMethod =getIntent().getStringExtra("payment_method");
        Log.d("PAYMENT METHOD", "onCreate: "+paymentMethod);
        if (cartResponse != null) {
            // Proceed with the data
            binding.subtotal.setText(cartResponse.totals.getCurrency_prefix()+" "+cartResponse.totals.getTotal_items());
            binding.shipping.setText(cartResponse.totals.getTotal_shipping());
            binding.discount.setText(cartResponse.totals.getTotal_discount());
            binding.total.setText(cartResponse.totals.getCurrency_prefix()+" "+cartResponse.totals.getTotal_price());
        } else {
            Log.e("ActivityCheckout", "CartResponse is null");
            Toast.makeText(this, "Failed to load cart data", Toast.LENGTH_SHORT).show();
        }

         if(isInternetConnected(this)){
             Checkout();
         }else{
             Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
         }

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        binding.billingAddressRel.setVisibility(View.GONE);
        binding.shippingCheckBox.setVisibility(View.GONE);
        binding.shippingAddressInputText.setVisibility(View.GONE);
        binding.shippingRel.setVisibility(View.GONE);

        // Set click listener for billing address autocomplete
        MaterialAutoCompleteTextView billingAutoComplete =
                (MaterialAutoCompleteTextView) binding.billingAddressInputText.getEditText();
        billingAutoComplete.setOnClickListener(v -> {
            binding.billingAddressRel.setVisibility(View.VISIBLE);
            binding.shippingCheckBox.setVisibility(View.VISIBLE);
        });

        // Set checkbox change listener
        binding.shippingCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.shippingAddressInputText.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            // If unchecked, also hide shipping relative layout
            if (!isChecked) {
                binding.shippingRel.setVisibility(View.GONE);
            }
        });
        binding.checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInternetConnected(ActivityCheckout.this)) {
                    setdata();
                }else{
                   Toast.makeText(ActivityCheckout.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for shipping address autocomplete
        MaterialAutoCompleteTextView shippingAutoComplete =
                (MaterialAutoCompleteTextView) binding.shippingAddressInputText.getEditText();
        shippingAutoComplete.setOnClickListener(v -> {
            binding.shippingRel.setVisibility(View.VISIBLE);
        });

        // Add validation for email fields
        setupEmailValidation();
    }
    private void setupEmailValidation() {
        TextWatcher emailValidator = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("billing_visibility", binding.billingAddressRel.getVisibility());
        outState.putInt("shipping_visibility", binding.shippingRel.getVisibility());
        outState.putBoolean("shipping_checked", binding.shippingCheckBox.isChecked());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            binding.billingAddressRel.setVisibility(savedInstanceState.getInt("billing_visibility"));
            binding.shippingRel.setVisibility(savedInstanceState.getInt("shipping_visibility"));
            binding.shippingCheckBox.setChecked(savedInstanceState.getBoolean("shipping_checked"));
        }
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

        // Billing address Pre-Fill
        binding.edtFirstName.setText(getCheckoutResponse.data.billing_address.getFirst_name());
        binding.edtLastName.setText(getCheckoutResponse.data.billing_address.getLast_name());
        binding.edtCompanyName.setText(getCheckoutResponse.data.billing_address.getCompany());
        binding.edtCountry.setText(getCheckoutResponse.data.billing_address.getCountry());
        binding.edtStreetAddress.setText(getCheckoutResponse.data.billing_address.getAddress_1());
        binding.edtApartment.setText(getCheckoutResponse.data.billing_address.getAddress_2());
        binding.edtTown.setText(getCheckoutResponse.data.billing_address.getCity());
        binding.edtState.setText(getCheckoutResponse.data.billing_address.getState());
        binding.edtPin.setText(getCheckoutResponse.data.billing_address.getPostcode());
        binding.edtPhone.setText(getCheckoutResponse.data.billing_address.getPhone());
        binding.edtEmail.setText(getCheckoutResponse.data.billing_address.getEmail());

        //Shipping Address PreFill
        binding.edtFirstNameShipping.setText(getCheckoutResponse.data.shipping_address.getFirst_name());
        binding.edtLastNameShipping.setText(getCheckoutResponse.data.shipping_address.getLast_name());
        binding.edtCompanyNameShipping.setText(getCheckoutResponse.data.shipping_address.getCompany());
        binding.edtCountryShipping.setText(getCheckoutResponse.data.shipping_address.getCountry());
        binding.edtStreetAddressShipping.setText(getCheckoutResponse.data.shipping_address.getAddress_1());
        binding.edtApartmentShipping.setText(getCheckoutResponse.data.shipping_address.getAddress_2());
        binding.edtTownShipping.setText(getCheckoutResponse.data.shipping_address.getCity());
        binding.edtStateShipping.setText(getCheckoutResponse.data.shipping_address.getState());
        binding.edtPinShipping.setText(getCheckoutResponse.data.shipping_address.getPostcode());
        binding.edtPhoneShipping.setText(getCheckoutResponse.data.shipping_address.getPhone());

    }
    private void setdata() {
        // Create main checkout object
        Checkout checkout = new Checkout();

        // Create the common Address object for billing
        Checkout.Address billingAddress = new Checkout.Address();
        billingAddress.setFirst_name(binding.edtFirstName.getText().toString().trim());
        billingAddress.setLast_name(binding.edtLastName.getText().toString().trim());
        billingAddress.setCompany(binding.edtCompanyName.getText().toString().trim());
        billingAddress.setCountry(binding.edtCountry.getText().toString().trim());
        billingAddress.setAddress_1(binding.edtStreetAddress.getText().toString().trim());
        billingAddress.setAddress_2(binding.edtApartment.getText().toString().trim());
        billingAddress.setCity(binding.edtTown.getText().toString().trim());
        billingAddress.setState(binding.edtState.getText().toString().trim());
        billingAddress.setPostcode(binding.edtPin.getText().toString().trim());
        billingAddress.setPhone(binding.edtPhone.getText().toString().trim());
        billingAddress.setEmail(binding.edtEmail.getText().toString().trim());

        // Create the common Address object for shipping
        Checkout.Address shippingAddress = new Checkout.Address();
        shippingAddress.setFirst_name(binding.edtFirstNameShipping.getText().toString().trim());
        shippingAddress.setLast_name(binding.edtLastNameShipping.getText().toString().trim());
        shippingAddress.setCompany(binding.edtCompanyNameShipping.getText().toString().trim());
        shippingAddress.setCountry(binding.edtCountryShipping.getText().toString().trim());
        shippingAddress.setAddress_1(binding.edtStreetAddressShipping.getText().toString().trim());
        shippingAddress.setAddress_2(binding.edtApartmentShipping.getText().toString().trim());
        shippingAddress.setCity(binding.edtTownShipping.getText().toString().trim());
        shippingAddress.setState(binding.edtStateShipping.getText().toString().trim());
        shippingAddress.setPostcode(binding.edtPinShipping.getText().toString().trim());
        shippingAddress.setPhone(binding.edtPhoneShipping.getText().toString().trim());

        // Set the addresses to checkout object
        checkout.setBilling_address(billingAddress);
        checkout.setShipping_address(shippingAddress);

        // Set other checkout fields
        checkout.setCustomer_note(binding.edtOtherNotesShipping.getText().toString().trim());
        checkout.setPayment_method(getIntent().getStringExtra("payment_method"));

        // Log data for verification
        Log.d("Checkout Data", "Billing First Name: " + checkout.getBilling_address().getFirst_name());
        Log.d("Checkout Data", "Shipping First Name: " + checkout.getShipping_address().getFirst_name());

        // Proceed with payment
        proceedToPayment(checkout);
    }


    private void proceedToPayment(Checkout checkout) {
        String auth= "Bearer "+pref.getPrefString(this, pref.user_token);
        String nonce=pref.getPrefString(this,pref.nonce);
        viewModel.postCheckout(auth,nonce,checkout).observe(this,response->{
            if (response != null && response.isSuccess && response.data != null) {
             responsee=response.data;
                Intent intent= new Intent(ActivityCheckout.this, ActivityMyOrders.class);
                startActivity(intent);
                finish();
            } else {
                Log.e("Checkout Error", "Response is null or unsuccessful: " + (response != null ? response.message : "Unknown error"));
                Toast.makeText(this, response != null ? response.message : "Unknown error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // Clean up binding
    }
}