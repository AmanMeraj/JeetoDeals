package com.deals.jeetodeals.OTP;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class OtpViewModel extends AndroidViewModel {
    OtpRepository repository;
    LiveData<OtpRepository.ApiResponse<OtpResponse>> responseLiveData;
    public OtpViewModel(@NonNull Application application) {
        super(application);
        repository= new OtpRepository();
    }
    public LiveData<OtpRepository.ApiResponse<OtpResponse>> postOtp(Otp otp){
        responseLiveData=repository.otpPost(otp);
        return responseLiveData;
    }
}
