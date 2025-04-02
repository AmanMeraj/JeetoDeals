package com.deals.jeetodeals.Fragments.HomeFragment;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.deals.jeetodeals.Model.DrawResponse;
import com.deals.jeetodeals.Model.ShopResponse;
import com.deals.jeetodeals.Model.WinnerResponse;
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
    private static volatile boolean isHandlingSessionExpiry = false;
    private static final Object SESSION_LOCK = new Object();
    private final ApiRequest apiRequest;

    public static final int ERROR_SESSION_EXPIRED = 403;

    public HomeRepository() {
        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
    }

    public LiveData<ApiResponse<ArrayList<HomeResponse>>> homeGet( String type, int category) {
        final MutableLiveData<ApiResponse<ArrayList<HomeResponse>>> liveData = new MutableLiveData<>();

        Call<ArrayList<HomeResponse>> call = apiRequest.getHome( type, category);
        call.enqueue(new Callback<ArrayList<HomeResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<HomeResponse>> call, @NonNull Response<ArrayList<HomeResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, 0));
                } else if (response.code() == ERROR_SESSION_EXPIRED) {
                    handleSessionExpiry(liveData);
                } else {
                    handleErrorResponse(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<HomeResponse>> call, @NonNull Throwable t) {
                handleNetworkFailure(call, t, liveData);
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<ArrayList<DrawResponse>>> getDraw() {
        final MutableLiveData<ApiResponse<ArrayList<DrawResponse>>> liveData = new MutableLiveData<>();

        Call<ArrayList<DrawResponse>> call = apiRequest.getDraw();
        call.enqueue(new Callback<ArrayList<DrawResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<DrawResponse>> call, @NonNull Response<ArrayList<DrawResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, 0));
                } else if (response.code() == ERROR_SESSION_EXPIRED) {
                    handleSessionExpiry(liveData);
                } else {
                    handleErrorResponse(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<DrawResponse>> call, @NonNull Throwable t) {
                handleNetworkFailure(call, t, liveData);
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<ArrayList<WinnerResponse>>> getWinner() {
        final MutableLiveData<ApiResponse<ArrayList<WinnerResponse>>> liveData = new MutableLiveData<>();

        Call<ArrayList<WinnerResponse>> call = apiRequest.getWinner();
        call.enqueue(new Callback<ArrayList<WinnerResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<WinnerResponse>> call, @NonNull Response<ArrayList<WinnerResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, 0));
                } else if (response.code() == ERROR_SESSION_EXPIRED) {
                    handleSessionExpiry(liveData);
                } else {
                    handleErrorResponse(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<WinnerResponse>> call, @NonNull Throwable t) {
                handleNetworkFailure(call, t, liveData);
            }
        });

        return liveData;
    }

    public LiveData<ApiResponse<List<ShopResponse>>> shop( Map<String, String> filters, int id, int page, int perPage) {
        final MutableLiveData<ApiResponse<List<ShopResponse>>> liveData = new MutableLiveData<>();

        Call<List<ShopResponse>> call = apiRequest.getShop( filters, id, page, perPage);
        call.enqueue(new Callback<List<ShopResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ShopResponse>> call, @NonNull Response<List<ShopResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, 0));
                } else if (response.code() == ERROR_SESSION_EXPIRED) {
                    handleSessionExpiry(liveData);
                } else {
                    handleErrorResponse(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ShopResponse>> call, @NonNull Throwable t) {
                handleNetworkFailure(call, t, liveData);
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<List<ShopResponse>>> shopWithoutCategory(Map<String, String> params, int page, int perPage) {
        final MutableLiveData<ApiResponse<List<ShopResponse>>> liveData = new MutableLiveData<>();

        // Call the API without providing a category ID
        Call<List<ShopResponse>> call = apiRequest.getShopWithoutCategory(params, page, perPage);

        call.enqueue(new Callback<List<ShopResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ShopResponse>> call, @NonNull Response<List<ShopResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, 0));
                } else if (response.code() == ERROR_SESSION_EXPIRED) {
                    handleSessionExpiry(liveData);
                } else {
                    handleErrorResponse(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ShopResponse>> call, @NonNull Throwable t) {
                handleNetworkFailure(call, t, liveData);
            }
        });

        return liveData;
    }


    // Centralized session expiry handling
    private <T> void handleSessionExpiry(MutableLiveData<ApiResponse<T>> liveData) {
        synchronized (SESSION_LOCK) {
            if (!isHandlingSessionExpiry) {
                isHandlingSessionExpiry = true;
                Log.w(TAG, "Session expired, notifying UI");
                liveData.setValue(new ApiResponse<>(null, false, "Session expired", ERROR_SESSION_EXPIRED));
            } else {
                // Don't notify again if already handling
                Log.d(TAG, "Already handling session expiry, suppressing duplicate notification");
            }
        }
    }

    // Reset session handling flag once logout/re-login process is complete
    public static void resetSessionExpiryFlag() {
        synchronized (SESSION_LOCK) {
            isHandlingSessionExpiry = false;
            Log.d(TAG, "Session expiry flag reset");
        }
    }

    // Centralized error handling
    private <T> void handleErrorResponse(Response<?> response, MutableLiveData<ApiResponse<T>> liveData) {
        try {
            String errorMessage = "An error occurred";
            if (response.errorBody() != null) {
                errorMessage = extractDynamicErrorMessage(response.errorBody().string());
            }
            Log.e(TAG, "API error: " + response.code() + " - " + errorMessage);
            liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
        } catch (Exception e) {
            Log.e(TAG, "Error parsing error response: " + e.getMessage());
            liveData.setValue(new ApiResponse<>(null, false, "An unknown error occurred", -1));
        }
    }

    // Centralized network failure handling
    private <T> void handleNetworkFailure(Call<?> call, Throwable t, MutableLiveData<ApiResponse<T>> liveData) {
        Log.e(TAG, "API call failed: " + t.getMessage(), t);
        String errorMessage = call.isCanceled() ?
                "Request was canceled" :
                "Failed to connect. Please check your network.";
        liveData.setValue(new ApiResponse<>(null, false, errorMessage, -1));
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
        public final int errorCode;

        public ApiResponse(T data, boolean isSuccess, String message, int errorCode) {
            this.data = data;
            this.isSuccess = isSuccess;
            this.message = message;
            this.errorCode = errorCode;
        }
    }
}