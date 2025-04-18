package com.deals.jeetodeals.ChangeAddress;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.deals.jeetodeals.Model.UpdateAddress;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.databinding.ActivityChangeAddress2Binding;
import com.deals.jeetodeals.Model.BillingAddress;
import com.deals.jeetodeals.Model.ShippingAddress;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

public class ActivityChangeAddress2 extends Utility {
    ActivityChangeAddress2Binding binding;
    ChangeAddressViewModel viewModel;
    String[] countryNames = {"India"};
    String[] stateNames = {"Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal"};
    String countryCode = "IN";

    String selectedBillingStateCode,selectedShippingStateCode;
    private Map<String, String> stateCodeMap;
    private Map<String, String> stateNameMap;
    String from = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeAddress2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(ChangeAddressViewModel.class);

        // Initialize state mappings first
        initializeStateMappings();

        from = getIntent().getStringExtra("edit") != null ? getIntent().getStringExtra("edit") : "";

        // Now set up the fields after state mappings are initialized
        if (from.equals("billing")) {
            setupBillingFields();
        } else {
            setupShippingFields();
        }

        binding.backBtn.setOnClickListener(view -> finish());
        binding.updateBtn.setOnClickListener(view -> validateAndUpdateAddress());

        binding.edtState.setOnItemClickListener((parent, view, position, id) -> {
            String selectedState = parent.getItemAtPosition(position).toString();
            selectedBillingStateCode = stateCodeMap.get(selectedState);
            Log.d("Selected Billing State", "State: " + selectedState + ", Code: " + selectedBillingStateCode);
        });


        binding.edtStateShipping.setOnItemClickListener((parent, view, position, id) -> {
            String selectedState = parent.getItemAtPosition(position).toString();
            selectedShippingStateCode = stateCodeMap.get(selectedState);
            Log.d("Selected Shipping State", "State: " + selectedState + ", Code: " + selectedShippingStateCode);
        });

        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, stateNames);

        binding.edtState.setAdapter(stateAdapter);
        binding.edtStateShipping.setAdapter(stateAdapter);
        binding.billingAddressInputText.setOnClickListener(view -> {
            if (binding.billingAddressRel.getVisibility() == View.VISIBLE) {
                binding.billingAddressRel.setVisibility(View.GONE);
            } else {
                binding.billingAddressRel.setVisibility(View.VISIBLE);
            }
        });

// Make sure to set this click listener on the EditText inside as well
        MaterialAutoCompleteTextView billingAutoComplete =
                (MaterialAutoCompleteTextView) binding.billingAddressInputText.getEditText();
        if (billingAutoComplete != null) {
            billingAutoComplete.setOnClickListener(view -> {
                if (binding.billingAddressRel.getVisibility() == View.VISIBLE) {
                    binding.billingAddressRel.setVisibility(View.GONE);
                } else {
                    binding.billingAddressRel.setVisibility(View.VISIBLE);
                }
            });
        }

// And for shipping address
        binding.shippingAddressInputText.setOnClickListener(view -> {
            if (binding.shippingRel.getVisibility() == View.VISIBLE) {
                binding.shippingRel.setVisibility(View.GONE);
            } else {
                binding.shippingRel.setVisibility(View.VISIBLE);
            }
        });

        MaterialAutoCompleteTextView shippingAutoComplete =
                (MaterialAutoCompleteTextView) binding.shippingAddressInputText.getEditText();
        if (shippingAutoComplete != null) {
            shippingAutoComplete.setOnClickListener(view -> {
                if (binding.shippingRel.getVisibility() == View.VISIBLE) {
                    binding.shippingRel.setVisibility(View.GONE);
                } else {
                    binding.shippingRel.setVisibility(View.VISIBLE);
                }
            });
        }

        // Set up billing address dropdown toggle
        setupAddressToggle(binding.billingAddressInputText, binding.billingAddressRel);

        // Set up shipping address dropdown toggle
        setupAddressToggle(binding.shippingAddressInputText, binding.shippingRel);


    }

    private void setupAddressToggle(TextInputLayout inputLayout, RelativeLayout contentLayout) {
        // 1. Set click listener on the entire TextInputLayout
        inputLayout.setOnClickListener(v -> toggleVisibility(contentLayout));

        // 2. Also set click listener on the EditText inside
        MaterialAutoCompleteTextView autoCompleteTextView =
                (MaterialAutoCompleteTextView) inputLayout.getEditText();
        if (autoCompleteTextView != null) {
            autoCompleteTextView.setOnClickListener(v -> toggleVisibility(contentLayout));
        }

        // 3. Add click listener for the dropdown end icon specifically
        inputLayout.setEndIconOnClickListener(v -> toggleVisibility(contentLayout));
    }

    private void toggleVisibility(RelativeLayout layout) {
        if (layout.getVisibility() == View.VISIBLE) {
            layout.setVisibility(View.GONE);
        } else {
            layout.setVisibility(View.VISIBLE);
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

    private void setupBillingFields() {
        BillingAddress billing = (BillingAddress) getIntent().getSerializableExtra("BillingAddress");
        binding.edtCountry.setText("India");

        // Retrieve shared preferences
        String prefFirstName = pref.getPrefString(this, pref.first_name);
        String prefLastName = pref.getPrefString(this, pref.last_name);
        String prefPhone = pref.getPrefString(this, pref.mobile);
        String prefEmail = pref.getPrefString(this, pref.user_email);

        if (billing != null) {
            // Use billing address info if available, fallback to shared preferences if empty
            binding.edtFirstName.setText(!TextUtils.isEmpty(billing.getFirst_name()) ? billing.getFirst_name() : prefFirstName);
            binding.edtLastName.setText(!TextUtils.isEmpty(billing.getLast_name()) ? billing.getLast_name() : prefLastName);
            binding.edtPhone.setText(!TextUtils.isEmpty(billing.getPhone()) ? billing.getPhone() : prefPhone);
            binding.edtEamil.setText(!TextUtils.isEmpty(billing.getEmail()) ? billing.getEmail() : prefEmail);

            binding.edtAddress1Billing.setText(billing.getAddress_1());
            binding.edtAddress2Billing.setText(billing.getAddress_2());
            binding.edtCity.setText(billing.getCity());
            binding.edtPostCode.setText(billing.getPostcode());

            // Store and map state code
            selectedBillingStateCode = billing.getState();
            String stateName = stateNameMap != null && billing.getState() != null ? stateNameMap.getOrDefault(billing.getState(), billing.getState()) : billing.getState();
            binding.edtState.setText(stateName);

        } else {
            // Billing is null, so use shared preferences for these fields
            if (!TextUtils.isEmpty(prefFirstName)) binding.edtFirstName.setText(prefFirstName);
            if (!TextUtils.isEmpty(prefLastName)) binding.edtLastName.setText(prefLastName);
            if (!TextUtils.isEmpty(prefPhone)) binding.edtPhone.setText(prefPhone);
            if (!TextUtils.isEmpty(prefEmail)) binding.edtEamil.setText(prefEmail);
        }

        binding.shippingAddressInputText.setVisibility(View.GONE);
        binding.billingAddressRel.setVisibility(View.VISIBLE);
        binding.shippingRel.setVisibility(View.GONE);
    }

    private void setupShippingFields() {
        ShippingAddress shipping = (ShippingAddress) getIntent().getSerializableExtra("ShippingAddress");
        binding.edtCountryShipping.setText("India");

        // Retrieve shared preferences
        String prefFirstName = pref.getPrefString(this, pref.first_name);
        String prefLastName = pref.getPrefString(this, pref.last_name);

        if (shipping != null) {
            // Use shipping address info if available, fallback to shared preferences if empty
            binding.edtFirstNameShipping.setText(!TextUtils.isEmpty(shipping.getFirst_name()) ? shipping.getFirst_name() : prefFirstName);
            binding.edtLastNameShipping.setText(!TextUtils.isEmpty(shipping.getLast_name()) ? shipping.getLast_name() : prefLastName);

            binding.edtAddress1.setText(shipping.getAddress_1());
            binding.edtAddress2.setText(shipping.getAddress_2());
            binding.edtCityShipping.setText(shipping.getCity());
            binding.edtPostCodeShipping.setText(shipping.getPostcode());

            // Store and map state code
            selectedShippingStateCode = shipping.getState();
            String stateName = stateNameMap != null && shipping.getState() != null ? stateNameMap.getOrDefault(shipping.getState(), shipping.getState()) : shipping.getState();
            binding.edtStateShipping.setText(stateName);

        } else {
            // Shipping is null, so use shared preferences for these fields
            if (!TextUtils.isEmpty(prefFirstName)) binding.edtFirstNameShipping.setText(prefFirstName);
            if (!TextUtils.isEmpty(prefLastName)) binding.edtLastNameShipping.setText(prefLastName);
        }

        binding.billingAddressRel.setVisibility(View.GONE);
        binding.shippingRel.setVisibility(View.VISIBLE);
        binding.billingAddressInputText.setVisibility(View.GONE);
    }


    private void validateAndUpdateAddress() {
        boolean isBillingValid = from.equals("billing") && validateBillingFields();
        boolean isShippingValid = from.equals("shipping") && validateShippingFields();

        if (isBillingValid) {
            updateBillingAddress();
        }
        if (isShippingValid) {
            updateShippingAddress();
        }
    }

    private boolean validateBillingFields() {
        if (isEmpty(binding.edtFirstName) || isEmpty(binding.edtLastName) ||
                isEmpty(binding.edtAddress1Billing) || isEmpty(binding.edtCity) ||
                isEmpty(binding.edtPostCode) || isEmpty(binding.edtCountry) ||
                isEmpty(binding.edtState) || isEmpty(binding.edtPhone) ||
                isEmpty(binding.edtEamil)) {
            Toast.makeText(this, "All billing fields must be filled!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateShippingFields() {
        if (isEmpty(binding.edtFirstNameShipping) || isEmpty(binding.edtLastNameShipping) ||
                isEmpty(binding.edtAddress1) || isEmpty(binding.edtCityShipping) ||
                isEmpty(binding.edtPostCodeShipping) || isEmpty(binding.edtCountryShipping) ||
                isEmpty(binding.edtStateShipping)) {
            Toast.makeText(this, "All shipping fields must be filled!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isEmpty(View view) {
        return view instanceof android.widget.EditText &&
                TextUtils.isEmpty(((android.widget.EditText) view).getText().toString().trim());
    }

    private void updateBillingAddress() {
        UpdateAddress update = new UpdateAddress();
        update.setBilling_first_name(binding.edtFirstName.getText().toString().trim());
        update.setBilling_last_name(binding.edtLastName.getText().toString().trim());
        update.setBilling_address_1(binding.edtAddress1Billing.getText().toString().trim());
        update.setBilling_address_2(binding.edtAddress2Billing.getText().toString().trim());
        update.setBilling_city(binding.edtCity.getText().toString().trim());
        update.setBilling_postcode(binding.edtPostCode.getText().toString().trim());
        update.setBilling_country("IN");
        // Use the selected state code instead of the state name
        if (selectedBillingStateCode != null && !selectedBillingStateCode.isEmpty()) {
            update.setBilling_state(selectedBillingStateCode);
        } else {
            // Fallback: Try to get state code from the current text
            String stateName = binding.edtState.getText().toString().trim();
            String stateCode = stateCodeMap.get(stateName);
            update.setBilling_state(stateCode != null ? stateCode : stateName);
        }
        update.setBilling_phone(binding.edtPhone.getText().toString().trim());
        update.setBilling_email(binding.edtEamil.getText().toString().trim());

        sendUpdateRequest(update, true);
    }

    private void updateShippingAddress() {
        UpdateAddress update = new UpdateAddress();
        update.setShipping_first_name(binding.edtFirstNameShipping.getText().toString().trim());
        update.setShipping_last_name(binding.edtLastNameShipping.getText().toString().trim());
        update.setShipping_address_1(binding.edtAddress1.getText().toString().trim());
        update.setShipping_address_2(binding.edtAddress2.getText().toString().trim());
        update.setShipping_city(binding.edtCityShipping.getText().toString().trim());
        update.setShipping_postcode(binding.edtPostCodeShipping.getText().toString().trim());
        update.setShipping_country("IN");
        // Use the selected state code instead of the state name
        if (selectedShippingStateCode != null && !selectedShippingStateCode.isEmpty()) {
            update.setShipping_state(selectedShippingStateCode);
        } else {
            // Fallback: Try to get state code from the current text
            String stateName = binding.edtStateShipping.getText().toString().trim();
            String stateCode = stateCodeMap.get(stateName);
            update.setShipping_state(stateCode != null ? stateCode : stateName);
        }

        sendUpdateRequest(update, false);
    }

    private void sendUpdateRequest(UpdateAddress update, boolean isBilling) {
        binding.loader.rlLoader.setVisibility(View.VISIBLE);
        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);

        viewModel.UpdateAddress(auth, update).observe(this, response -> {
            if (response != null && response.isSuccess) {
                binding.loader.rlLoader.setVisibility(View.GONE);
                Toast.makeText(this, (isBilling ? "Billing" : "Shipping") + " address updated successfully!", Toast.LENGTH_SHORT).show();
                if (!isBilling) finish();
            } else {
                binding.loader.rlLoader.setVisibility(View.GONE);
                Toast.makeText(this, "Failed to update " + (isBilling ? "billing" : "shipping") + " address!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}