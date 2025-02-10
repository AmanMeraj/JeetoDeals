package com.deals.jeetodeals.Profile;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.deals.jeetodeals.Model.Login;
import com.deals.jeetodeals.Model.User;
import com.deals.jeetodeals.SignInScreen.ForgotPassResponse;
import com.deals.jeetodeals.SignInScreen.SiginRepository;
import com.deals.jeetodeals.SignInScreen.SigninResponse;
import com.deals.jeetodeals.retrofit.ApiRequest;
import com.deals.jeetodeals.retrofit.RetrofitRequest;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileRepository {
    private static final String TAG = ProfileRepository.class.getSimpleName();
    private final ApiRequest apiRequest;

    public ProfileRepository() {
        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
    }

    // Corrected LiveData type for SigninResponse
    public LiveData<ProfileRepository.ApiResponse<ProfileResponse>> profile(String auth) {
        final MutableLiveData<ProfileRepository.ApiResponse<ProfileResponse>> liveData = new MutableLiveData<>();

        apiRequest.getProfile(auth).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Extract the message explicitly from SigninResponse
//                    response.body().setMessage("Profile Fetched Successfully!");
//                    String successMessage = response.body().getMessage(); // Ensure SigninResponse has a getMessage() method
                    liveData.setValue(new ProfileRepository.ApiResponse<>(response.body(), true, "Profile Fetched Successfully"));
                } else if (response.errorBody() != null) {
                    try {
                        // Extract the error message dynamically
                        String errorBody = response.errorBody().string();
                        String errorMessage = extractDynamicErrorMessage(errorBody);
                        liveData.setValue(new ProfileRepository.ApiResponse<>(null, false, errorMessage));
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response: " + e.getMessage());
                        liveData.setValue(new ProfileRepository.ApiResponse<>(null, false, "An unknown error occurred."));
                    }
                } else {
                    liveData.setValue(new ProfileRepository.ApiResponse<>(null, false, "An unknown error occurred."));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                liveData.setValue(new ProfileRepository.ApiResponse<>(null, false, "Failed to connect. Please check your network."));
            }
        });

        return liveData;
    }


    /**
     * Dynamically parses the error message from the response body.
     *
     * @param errorBody The raw error body as a string.
     * @return The extracted error message or a default error message if parsing fails.
     */
    private String extractDynamicErrorMessage(String errorBody) {
        try {
            // Try to parse as JSON
            if (errorBody.trim().startsWith("{")) {
                JSONObject jsonObject = new JSONObject(errorBody);
                return jsonObject.optString("message", "An error occurred.");
            }

            // Return the plain error body if it's neither JSON nor HTML
            return errorBody.trim();
        } catch (Exception e) {
            Log.e(TAG, "Error while parsing the error message: " + e.getMessage());
            return "An unknown error occurred.";
        }
    }

    /**
     * Wrapper class to handle API responses.
     */
    public static class ApiResponse<T> {
        public final T data;
        public final boolean isSuccess;
        public final String message;

        public ApiResponse(T data, boolean isSuccess, String message) {
            this.data = data;
            this.isSuccess = isSuccess;
            this.message = message;
        }
    }
}
