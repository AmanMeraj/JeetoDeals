package com.deals.jeetodeals.ChangeAddress;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.deals.jeetodeals.Model.UpdateAddress;
import com.deals.jeetodeals.Model.UpdateAddressResponse;
import com.deals.jeetodeals.SignInScreen.ForgotPassResponse;

public class ChangeAddressViewModel extends AndroidViewModel {

    private final ChangeAddressRepository repository;

    // LiveData to hold the ForgotPassword response
    private LiveData<ChangeAddressRepository.ApiResponse<ForgotPassResponse>> responseLiveDataForgot;

    public ChangeAddressViewModel(@NonNull Application application) {
        super(application);
        repository = new ChangeAddressRepository();
    }

    // Method for Login
    public LiveData<ChangeAddressRepository.ApiResponse<ChangeAddressResponse>> GetAddress(String auth) {
        return repository.getAddress(auth); // Ensure 'repository' is properly initialized
    }


    public LiveData<ChangeAddressRepository.ApiResponse<UpdateAddressResponse>> UpdateAddress(String auth, UpdateAddress updateAddress) {
        return repository.updateAddress(auth,updateAddress); // Ensure 'repository' is properly initialized
    }

}
