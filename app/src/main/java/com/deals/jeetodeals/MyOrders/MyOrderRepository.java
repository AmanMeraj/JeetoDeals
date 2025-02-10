package com.deals.jeetodeals.MyOrders;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.deals.jeetodeals.retrofit.ApiRequest;
import com.deals.jeetodeals.retrofit.RetrofitRequest;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// MyOrderRepository.java
public class MyOrderRepository {
    private static final String TAG = MyOrderRepository.class.getSimpleName();
    private final ApiRequest apiRequest;

    public MyOrderRepository() {
        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
    }

    public LiveData<ApiResponse<ArrayList<MyOrdersResponse>>> getOrders(String auth) {
        final MutableLiveData<ApiResponse<ArrayList<MyOrdersResponse>>> liveData = new MutableLiveData<>();

        apiRequest.getOrders(auth).enqueue(new Callback<ArrayList<MyOrdersResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<MyOrdersResponse>> call, @NonNull Response<ArrayList<MyOrdersResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null));
                } else {
                    handleErrorResponse(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<MyOrdersResponse>> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Failed to connect. Please check your network."));
            }
        });

        return liveData;
    }

    private void handleErrorResponse(Response<?> response, MutableLiveData<ApiResponse<ArrayList<MyOrdersResponse>>> liveData) {
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                String errorMessage = extractDynamicErrorMessage(errorBody);
                liveData.setValue(new ApiResponse<>(null, false, errorMessage));
            } else {
                liveData.setValue(new ApiResponse<>(null, false, "An unknown error occurred."));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing error response: " + e.getMessage());
            liveData.setValue(new ApiResponse<>(null, false, "An unknown error occurred."));
        }
    }

    private String extractDynamicErrorMessage(String errorBody) {
        try {
            if (errorBody.trim().startsWith("{")) {
                JSONObject jsonObject = new JSONObject(errorBody);
                return jsonObject.optString("message", "An error occurred.");
            }
            Document document = Jsoup.parse(errorBody);
            return document.body() != null ? document.body().text().trim() : "An unknown error occurred.";
        } catch (Exception e) {
            Log.e(TAG, "Error while parsing the error message: " + e.getMessage());
            return "An unknown error occurred.";
        }
    }

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

// MyOrderViewModel.java

