package com.deals.jeetodeals.Fragments.HomeFragment;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.deals.jeetodeals.Model.ShopResponse;
import com.deals.jeetodeals.retrofit.ApiRequest;
import com.deals.jeetodeals.retrofit.RetrofitRequest;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeRepository {
    private static final String TAG = HomeRepository.class.getSimpleName();
    private final ApiRequest apiRequest;

    public HomeRepository() {
        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
    }

    public LiveData<ApiResponse<ArrayList<HomeResponse>>> homeGet(String auth, String type,int category) {
        final MutableLiveData<ApiResponse<ArrayList<HomeResponse>>> liveData = new MutableLiveData<>();

        Call<ArrayList<HomeResponse>> call = apiRequest.getHome(auth, type,category);
        call.enqueue(new Callback<ArrayList<HomeResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<HomeResponse>> call, @NonNull Response<ArrayList<HomeResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null));
                } else if (response.code() == 401) { // Handle Unauthorized
                    liveData.setValue(new ApiResponse<>(null, false, "Unauthorized"));
                } else {
                    try {
                        String errorMessage = "An error occurred";
                        if (response.errorBody() != null) {
                            errorMessage = extractDynamicErrorMessage(response.errorBody().string());
                        }
                        liveData.setValue(new ApiResponse<>(null, false, errorMessage));
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response: " + e.getMessage());
                        liveData.setValue(new ApiResponse<>(null, false, "An unknown error occurred"));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<HomeResponse>> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                String errorMessage = "Network error occurred";
                if (!call.isCanceled()) {
                    errorMessage = "Failed to connect. Please check your network.";
                }
                liveData.setValue(new ApiResponse<>(null, false, errorMessage));
            }
        });

        return liveData;
    }


    public LiveData<ApiResponse<List<ShopResponse>>> shop(String auth, Map<String, String> filters, int id, int page, int perPage) {
        final MutableLiveData<ApiResponse<List<ShopResponse>>> liveData = new MutableLiveData<>();

        // Make API call with the filters map and the other parameters
        Call<List<ShopResponse>> call = apiRequest.getShop(auth, filters, id, page, perPage);
        call.enqueue(new Callback<List<ShopResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ShopResponse>> call, @NonNull Response<List<ShopResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null));
                } else {
                    try {
                        String errorMessage = "An error occurred";
                        if (response.errorBody() != null) {
                            errorMessage = extractDynamicErrorMessage(response.errorBody().string());
                        }
                        Log.e(TAG, "API call failed with response code: " + response.code() + " and message: " + errorMessage);
                        liveData.setValue(new ApiResponse<>(null, false, errorMessage));
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response: " + e.getMessage());
                        liveData.setValue(new ApiResponse<>(null, false, "An unknown error occurred"));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ShopResponse>> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
                String errorMessage = "Network error occurred";
                if (!call.isCanceled()) {
                    errorMessage = "Failed to connect. Please check your network.";
                }
                liveData.setValue(new ApiResponse<>(null, false, errorMessage));
            }
        });

        return liveData;
    }


    private String extractDynamicErrorMessage(String errorBody) {
        try {
            if (errorBody.trim().startsWith("{")) {
                JSONObject jsonObject = new JSONObject(errorBody);
                return jsonObject.optString("message", "An error occurred");
            }
            Document document = Jsoup.parse(errorBody);
            if (document.body() != null && !document.body().text().isEmpty()) {
                return document.body().text();
            }
            return errorBody.trim();
        } catch (Exception e) {
            Log.e(TAG, "Error parsing error message: " + e.getMessage());
            return "An unknown error occurred";
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