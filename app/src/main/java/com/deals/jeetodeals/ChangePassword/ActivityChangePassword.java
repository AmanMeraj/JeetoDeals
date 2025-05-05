package com.deals.jeetodeals.ChangePassword;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.deals.jeetodeals.ContainerActivity.ContainerActivity;
import com.deals.jeetodeals.Fragments.FragmentsViewModel;
import com.deals.jeetodeals.Model.ChangePassword;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.databinding.ActivityChangePasswordBinding;

public class ActivityChangePassword extends Utility {
ActivityChangePasswordBinding binding;
FragmentsViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel= new ViewModelProvider(this).get(FragmentsViewModel.class);

        binding.backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(ActivityChangePassword.this, ContainerActivity.class);
            intent.putExtra("navigate_to", "profile");
            intent.putExtra("from_change_password", true); // this is optional, but helps if you want to know the exact source
            startActivity(intent);
            finish();
        });


        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isInternetConnected(ActivityChangePassword.this)){
                    validateAndSubmit();
                }else {
                    Toast.makeText(ActivityChangePassword.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void validateAndSubmit() {
        String currentPassword = binding.edtFirstName.getText().toString().trim();
        String newPassword = binding.edtLastName.getText().toString().trim();
        String confirmPassword = binding.edtConfirmPass.getText().toString().trim();

        // Validation Rules
        if (TextUtils.isEmpty(currentPassword)) {
            binding.edtFirstName.setError("Current password cannot be empty");
            binding.edtFirstName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            binding.edtLastName.setError("New password cannot be empty");
            binding.edtLastName.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            binding.edtLastName.setError("New password must be at least 6 characters");
            binding.edtLastName.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            binding.edtConfirmPass.setError("Passwords do not match");
            binding.edtConfirmPass.requestFocus();
            return;
        }
        changePassword(currentPassword,newPassword);
    }

    private void changePassword(String currentPassword, String newPassword) {
        binding.loader.rlLoader.setVisibility(View.VISIBLE);
        String auth="Bearer "+pref.getPrefString(this,pref.user_token);
        ChangePassword changePassword= new ChangePassword();
        changePassword.setCurrent_password(currentPassword);
        changePassword.setNew_password(newPassword);
        viewModel.changePassword(auth,changePassword).observe(this,response->{
            if(response.isSuccess){
                binding.loader.rlLoader.setVisibility(View.GONE);
                Toast.makeText(this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();

                Intent intent= new Intent(ActivityChangePassword.this, ContainerActivity.class);
                intent.putExtra("navigate_to","profile");
                startActivity(intent);
                finish();

            }else{
                binding.loader.rlLoader.setVisibility(View.GONE);
                Toast.makeText(this, ""+response.message, Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ActivityChangePassword.this, ContainerActivity.class);
        intent.putExtra("navigate_to", "profile");
        intent.putExtra("from_change_password", true);
        startActivity(intent);
        finish();
    }


}