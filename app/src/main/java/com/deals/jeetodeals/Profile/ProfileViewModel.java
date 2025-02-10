package com.deals.jeetodeals.Profile;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.deals.jeetodeals.SignInScreen.SiginRepository.ApiResponse;
import com.deals.jeetodeals.SignInScreen.ForgotPassResponse;

public class ProfileViewModel extends AndroidViewModel {

    private final ProfileRepository repository;

    // LiveData to hold the ForgotPassword response

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        repository = new ProfileRepository();
    }

    // Method for Login
    public LiveData<ProfileRepository.ApiResponse<ProfileResponse>> getProfile(String auth) {
        return repository.profile(auth);
    }

}

