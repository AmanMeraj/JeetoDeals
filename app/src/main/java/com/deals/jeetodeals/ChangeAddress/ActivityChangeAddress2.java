package com.deals.jeetodeals.ChangeAddress;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.deals.jeetodeals.Model.UpdateAddress;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.databinding.ActivityChangeAddress2Binding;
import com.deals.jeetodeals.Model.BillingAddress;
import com.deals.jeetodeals.Model.ShippingAddress;

public class ActivityChangeAddress2 extends Utility {
    ActivityChangeAddress2Binding binding;
    ChangeAddressViewModel viewModel;
    String from = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeAddress2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(ChangeAddressViewModel.class);

        from = getIntent().getStringExtra("edit") != null ? getIntent().getStringExtra("edit") : "";

        if (from.equals("billing")) {
            setupBillingFields();
        } else {
            setupShippingFields();
        }

        binding.backBtn.setOnClickListener(view -> finish());
        binding.updateBtn.setOnClickListener(view -> validateAndUpdateAddress());
    }

    private void setupBillingFields() {
        BillingAddress billing = (BillingAddress) getIntent().getSerializableExtra("BillingAddress");
        if (billing != null) {
            binding.edtFirstName.setText(billing.getFirst_name());
            binding.edtLastName.setText(billing.getLast_name());
            binding.edtAddress1Billing.setText(billing.getAddress_1());
            binding.edtAddress2Billing.setText(billing.getAddress_2());
            binding.edtCity.setText(billing.getCity());
            binding.edtPostCode.setText(billing.getPostcode());
            binding.edtCountry.setText(billing.getCountry());
            binding.edtState.setText(billing.getState());
            binding.edtPhone.setText(billing.getPhone());
            binding.edtEamil.setText(billing.getEmail());
        }
        binding.shippingAddressInputText.setVisibility(View.GONE);
        binding.billingAddressRel.setVisibility(View.VISIBLE);
        binding.shippingRel.setVisibility(View.GONE);
    }

    private void setupShippingFields() {
        ShippingAddress shipping = (ShippingAddress) getIntent().getSerializableExtra("ShippingAddress");
        if (shipping != null) {
            binding.edtFirstNameShipping.setText(shipping.getFirst_name());
            binding.edtLastNameShipping.setText(shipping.getLast_name());
            binding.edtAddress1.setText(shipping.getAddress_1());
            binding.edtAddress2.setText(shipping.getAddress_2());
            binding.edtCityShipping.setText(shipping.getCity());
            binding.edtPostCodeShipping.setText(shipping.getPostcode());
            binding.edtCountryShipping.setText(shipping.getCountry());
            binding.edtStateShipping.setText(shipping.getState());
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
        update.setBilling_country(binding.edtCountry.getText().toString().trim());
        update.setBilling_state(binding.edtState.getText().toString().trim());
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
        update.setShipping_country(binding.edtCountryShipping.getText().toString().trim());
        update.setShipping_state(binding.edtStateShipping.getText().toString().trim());

        sendUpdateRequest(update, false);
    }

    private void sendUpdateRequest(UpdateAddress update, boolean isBilling) {
        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);

        viewModel.UpdateAddress(auth, update).observe(this, response -> {
            if (response != null && response.isSuccess) {
                Toast.makeText(this, (isBilling ? "Billing" : "Shipping") + " address updated successfully!", Toast.LENGTH_SHORT).show();
                if (!isBilling) finish();
            } else {
                Toast.makeText(this, "Failed to update " + (isBilling ? "billing" : "shipping") + " address!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
