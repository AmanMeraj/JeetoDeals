package com.deals.jeetodeals.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.deals.jeetodeals.ChangePassword.ActivityChangePassword;
import com.deals.jeetodeals.SignInScreen.SignInActivity;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.databinding.ActivityProfileBinding;

public class FragmentProfile extends Fragment {
    private ActivityProfileBinding binding;
    private ProfileResponse responsee;
    private ProfileViewModel viewModel;
    private Utility utility;

    public FragmentProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utility = new Utility();
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment using view binding
        binding = ActivityProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupListeners();

        if (utility.isInternetConnected(requireContext())) {
            getProfile();
        } else {
            Toast.makeText(requireContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        // Back button navigates back in fragment stack

        // Logout button functionality
        binding.logoutBtn.setOnClickListener(view -> {
            utility.pref.setPrefBoolean(requireContext(), utility.pref.login_status, false);

            // Redirect to SignInActivity
            Intent intent = new Intent(requireContext(), SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
        binding.resetPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(requireContext(), ActivityChangePassword.class);
                startActivity(intent);
            }
        });
    }

    private void getProfile() {
        binding.loader.rlLoader.setVisibility(View.VISIBLE);
        String auth = "Bearer " + utility.pref.getPrefString(requireContext(), utility.pref.user_token);

        viewModel.getProfile(auth).observe(getViewLifecycleOwner(), response -> {
            binding.loader.rlLoader.setVisibility(View.GONE);
            if (response != null && response.isSuccess && response.data != null) {
                responsee = response.data;
                binding.numberTv.setText(responsee.phone_number);
                binding.emailTv.setText(responsee.email);
                binding.edtFirstName.setText(responsee.first_name);
                binding.edtLastName.setText(responsee.last_name);
                if ("Indian".equals(responsee.nationality)) {
                    binding.edtEamilAddress.setText(responsee.nationality);
                } else {
                    binding.edtEamilAddress.setText("Indian");
                }
                binding.nameTv.setText(responsee.first_name + " " + responsee.last_name);
            } else {
                Toast.makeText(requireContext(), response != null ? response.message : "Unknown error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
