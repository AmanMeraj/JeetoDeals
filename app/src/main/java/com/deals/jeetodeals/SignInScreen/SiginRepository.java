package com.deals.jeetodeals.SignInScreen;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.deals.jeetodeals.Model.Login;
import com.deals.jeetodeals.Model.User;
import com.deals.jeetodeals.retrofit.ApiRequest;
import com.deals.jeetodeals.retrofit.RetrofitRequest;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SiginRepository {
    private static final String TAG = SiginRepository.class.getSimpleName();
    private final ApiRequest apiRequest;

    public SiginRepository() {
        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
    }

    // Corrected LiveData type for SigninResponse
    public LiveData<ApiResponse<SigninResponse>> loginPost(Login login) {
        final MutableLiveData<ApiResponse<SigninResponse>> liveData = new MutableLiveData<>();

        apiRequest.postLogin(login).enqueue(new Callback<SigninResponse>() {
            @Override
            public void onResponse(@NonNull Call<SigninResponse> call, @NonNull Response<SigninResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Extract the message explicitly from SigninResponse
                    response.body().setMessage("Logged in Successfully!");
                    String successMessage = response.body().getMessage(); // Ensure SigninResponse has a getMessage() method
                    liveData.setValue(new ApiResponse<>(response.body(), true, successMessage));
                } else if (response.errorBody() != null) {
                    try {
                        // Extract the error message dynamically
                        String errorBody = response.errorBody().string();
                        String errorMessage = extractDynamicErrorMessage(errorBody);
                        liveData.setValue(new ApiResponse<>(null, false, errorMessage));
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response: " + e.getMessage());
                        liveData.setValue(new ApiResponse<>(null, false, "An unknown error occurred."));
                    }
                } else {
                    liveData.setValue(new ApiResponse<>(null, false, "An unknown error occurred."));
                }
            }

            @Override
            public void onFailure(@NonNull Call<SigninResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Failed to connect. Please check your network."));
            }
        });

        return liveData;
    }


    // Corrected LiveData type for ForgotPassResponse
    public LiveData<ApiResponse<ForgotPassResponse>> forgotPass(User user) {
        final MutableLiveData<ApiResponse<ForgotPassResponse>> liveData = new MutableLiveData<>();

        apiRequest.ForgotPassword(user).enqueue(new Callback<ForgotPassResponse>() {
            @Override
            public void onResponse(@NonNull Call<ForgotPassResponse> call, @NonNull Response<ForgotPassResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Extract the message field explicitly
                    String successMessage = response.body().getMessage(); // Ensure ForgotPassResponse has a getMessage() method
                    liveData.setValue(new ApiResponse<>(response.body(), true, successMessage));
                } else if (response.errorBody() != null) {
                    try {
                        // Extract the error message dynamically
                        String errorBody = response.errorBody().string();
                        String errorMessage = extractDynamicErrorMessage(errorBody);
                        liveData.setValue(new ApiResponse<>(null, false, errorMessage));
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response: " + e.getMessage());
                        liveData.setValue(new ApiResponse<>(null, false, "An unknown error occurred."));
                    }
                } else {
                    liveData.setValue(new ApiResponse<>(null, false, "An unknown error occurred."));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ForgotPassResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Failed to connect. Please check your network."));
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
