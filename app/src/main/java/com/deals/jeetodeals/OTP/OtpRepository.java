package com.deals.jeetodeals.OTP;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.deals.jeetodeals.retrofit.ApiRequest;
import com.deals.jeetodeals.retrofit.RetrofitRequest;
import com.deals.jeetodeals.OTP.OtpResponse;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpRepository {

    private static final String TAG = OtpRepository.class.getSimpleName();
    private final ApiRequest apiRequest;

    public OtpRepository() {
        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
    }

    public LiveData<ApiResponse<OtpResponse>> otpPost(Otp otp){
        final MutableLiveData<ApiResponse<OtpResponse>> data = new MutableLiveData<>();

        apiRequest.postOtp(otp)
                .enqueue(new Callback<OtpResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<OtpResponse> call, @NonNull Response<OtpResponse> response) {
                        Log.e(TAG, "onResponse :: " + response);
                        if (response.isSuccessful() && response.body() != null) {
                            // Success case: pass the data back in ApiResponse
                            data.setValue(new ApiResponse<>(response.body(), true, null));
                        } else if (response.errorBody() != null) {
                            try {
                                // Extract the error message from the response body
                                String errorBody = response.errorBody().string();
                                String errorMessage = extractDynamicErrorMessage(errorBody);
                                data.setValue(new ApiResponse<>(null, false, errorMessage));
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error response: " + e.getMessage());
                                data.setValue(new ApiResponse<>(null, false, "An unknown error occurred."));
                            }
                        } else {
                            data.setValue(new ApiResponse<>(null, false, "An unknown error occurred."));
                        }
                    }

                    @Override
                    public void onFailure(Call<OtpResponse> call, Throwable t) {
                        Log.e(TAG, "API call failed: " + t.getMessage());
                        data.setValue(new ApiResponse<>(null, false, "Failed to connect. Please check your network."));
                    }
                });

        return data;
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

            // Try to parse as HTML
            Document document = Jsoup.parse(errorBody);
            if (document.body() != null && !document.body().text().isEmpty()) {
                return document.body().text(); // Extract visible text from HTML
            }

            // If neither JSON nor HTML, return plain text
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
