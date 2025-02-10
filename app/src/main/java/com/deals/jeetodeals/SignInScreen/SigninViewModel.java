package com.deals.jeetodeals.SignInScreen;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.deals.jeetodeals.Model.Login;
import com.deals.jeetodeals.Model.User;
import com.deals.jeetodeals.SignInScreen.SiginRepository.ApiResponse;
import com.deals.jeetodeals.SignInScreen.SigninResponse;
import com.deals.jeetodeals.SignInScreen.ForgotPassResponse;

public class SigninViewModel extends AndroidViewModel {

    private final SiginRepository repository;

    // LiveData to hold the ForgotPassword response
    private LiveData<ApiResponse<ForgotPassResponse>> responseLiveDataForgot;

    public SigninViewModel(@NonNull Application application) {
        super(application);
        repository = new SiginRepository();
    }

    // Method for Login
    public LiveData<ApiResponse<SigninResponse>> postLogin(Login login) {
        return repository.loginPost(login);
    }

    // Method for Forgot Password
    public LiveData<ApiResponse<ForgotPassResponse>> forgotPassword(User user) {
        responseLiveDataForgot = repository.forgotPass(user);
        return responseLiveDataForgot;
    }
}
