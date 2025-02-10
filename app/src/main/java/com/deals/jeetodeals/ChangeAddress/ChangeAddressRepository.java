package com.deals.jeetodeals.ChangeAddress;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.deals.jeetodeals.Model.UpdateAddress;
import com.deals.jeetodeals.Model.UpdateAddressResponse;
import com.deals.jeetodeals.SignInScreen.SiginRepository;
import com.deals.jeetodeals.retrofit.ApiRequest;
import com.deals.jeetodeals.retrofit.RetrofitRequest;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeAddressRepository {
    private static final String TAG = SiginRepository.class.getSimpleName();
    private final ApiRequest apiRequest;

    public ChangeAddressRepository() {
        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
    }

    // Corrected LiveData type for SigninResponse
    public LiveData<ChangeAddressRepository.ApiResponse<ChangeAddressResponse>> getAddress(String auth) {
        final MutableLiveData<ChangeAddressRepository.ApiResponse<ChangeAddressResponse>> liveData = new MutableLiveData<>();

        apiRequest.GetAddress(auth).enqueue(new Callback<ChangeAddressResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChangeAddressResponse> call, @NonNull Response<ChangeAddressResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Successful response, send the response body directly
                    liveData.setValue(new ChangeAddressRepository.ApiResponse<>(response.body(), true, null)); // No message needed
                    Log.d(TAG, "Address data fetched successfully: " + response.body());
                } else {
                    // Handle unsuccessful response (error response body)
                    String errorMessage = "An unknown error occurred.";
                    if (response.errorBody() != null) {
                        try {
                            // Extract the error message dynamically from the error body
                            String errorBody = response.errorBody().string();
                            errorMessage = extractDynamicErrorMessage(errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error response: " + e.getMessage());
                        }
                    }
                    liveData.setValue(new ChangeAddressRepository.ApiResponse<>(null, false, errorMessage));
                    Log.d(TAG, "Error fetching address: " + errorMessage);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChangeAddressResponse> call, @NonNull Throwable t) {
                // Network or connection failure
                String errorMessage = "Failed to connect. Please check your network.";
                liveData.setValue(new ChangeAddressRepository.ApiResponse<>(null, false, errorMessage));
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
            }
        });

        return liveData;
    }

    public LiveData<ChangeAddressRepository.ApiResponse<UpdateAddressResponse>> updateAddress(String auth, UpdateAddress updateAddress) {
        final MutableLiveData<ChangeAddressRepository.ApiResponse<UpdateAddressResponse>> liveData = new MutableLiveData<>();

        apiRequest.UpdateAddress(auth,updateAddress).enqueue(new Callback<UpdateAddressResponse>() {
            @Override
            public void onResponse(@NonNull Call<UpdateAddressResponse> call, @NonNull Response<UpdateAddressResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Successful response, send the response body directly
                    liveData.setValue(new ChangeAddressRepository.ApiResponse<>(response.body(), true, null)); // No message needed
                    Log.d(TAG, "Address data fetched successfully: " + response.body());
                } else {
                    // Handle unsuccessful response (error response body)
                    String errorMessage = "An unknown error occurred.";
                    if (response.errorBody() != null) {
                        try {
                            // Extract the error message dynamically from the error body
                            String errorBody = response.errorBody().string();
                            errorMessage = extractDynamicErrorMessage(errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error response: " + e.getMessage());
                        }
                    }
                    liveData.setValue(new ChangeAddressRepository.ApiResponse<>(null, false, errorMessage));
                    Log.d(TAG, "Error fetching address: " + errorMessage);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UpdateAddressResponse> call, @NonNull Throwable t) {
                // Network or connection failure
                String errorMessage = "Failed to connect. Please check your network.";
                liveData.setValue(new ChangeAddressRepository.ApiResponse<>(null, false, errorMessage));
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
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
            JSONObject jsonObject = new JSONObject(errorBody);
            if (jsonObject.has("error")) {
                return jsonObject.getString("error");
            }
            return jsonObject.optString("message", "An error occurred.");
        } catch (Exception e) {
            Log.e(TAG, "Error parsing error response: " + e.getMessage());
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
