package com.deals.jeetodeals.SignupScreen;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.deals.jeetodeals.Model.Signup;


public class SignupViewModel extends AndroidViewModel {
    private SignupRepository repository;
    private LiveData<SignupRepository.ApiResponse<ExistsResponse>> responseLiveData;

    public SignupViewModel(@NonNull Application application) {
        super(application);
        repository = new SignupRepository();
    }

    /**
     * Method to call the Signup API and get the response wrapped in ApiResponse.
     *
     * @param signup Signup object containing user details.
     * @return LiveData containing ApiResponse with ExistsResponse data and status message.
     */
    public LiveData<SignupRepository.ApiResponse<ExistsResponse>> postSignup(Signup signup) {
        responseLiveData = repository.signupPost(signup);  // Fixed method call with correct case
        return responseLiveData;
    }
}
