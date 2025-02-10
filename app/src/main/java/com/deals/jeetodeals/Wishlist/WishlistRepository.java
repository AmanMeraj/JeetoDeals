package com.deals.jeetodeals.Wishlist;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.deals.jeetodeals.Fragments.FragmentsRepository;
import com.deals.jeetodeals.Model.WalletResponse;
import com.deals.jeetodeals.Model.Wishlist;
import com.deals.jeetodeals.retrofit.ApiRequest;
import com.deals.jeetodeals.retrofit.RetrofitRequest;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishlistRepository {

    private static final String TAG = WishlistRepository.class.getSimpleName();
    private final ApiRequest apiRequest;

    public WishlistRepository() {
        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
    }

    public LiveData<WishlistRepository.ApiResponse<ArrayList<WishlistResponse>>> wishlist(String auth) {
        final MutableLiveData<WishlistRepository.ApiResponse<ArrayList<WishlistResponse>>> liveData = new MutableLiveData<>();

        apiRequest.getWishlist(auth).enqueue(new Callback<ArrayList<WishlistResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<WishlistResponse>> call, @NonNull Response<ArrayList<WishlistResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Success: Return response body
                    liveData.setValue(new WishlistRepository.ApiResponse<>(response.body(), true, null));
                } else {
                    handleErrorResponse2(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<WishlistResponse>> call, @NonNull Throwable t) {
                Log.e("TAG", "API call failed: " + t.getMessage());
                liveData.setValue(new WishlistRepository.ApiResponse<>(null, false, "Failed to connect. Please check your network."));
            }
        });

        return liveData;
    }

    public LiveData<WishlistRepository.ApiResponse<WishlistAddResponse>> wishlistAdd(String auth, Wishlist wishlist) {
        final MutableLiveData<WishlistRepository.ApiResponse<WishlistAddResponse>> liveData = new MutableLiveData<>();
        final String SUCCESS_MESSAGE = "Item added to Wishlist Successfully";
        final String ALREADY_EXISTS_MESSAGE = "Item is already in your wishlist";

        apiRequest.addWishlist(auth, wishlist).enqueue(new Callback<WishlistAddResponse>() {
            @Override
            public void onResponse(@NonNull Call<WishlistAddResponse> call, @NonNull Response<WishlistAddResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WishlistAddResponse responseBody = response.body();

                    // Check if the response contains any indication that the item already exists
                    String message;
                    if (responseBody.getMessage() != null &&
                            responseBody.getMessage().toLowerCase().contains("already")) {
                        message = ALREADY_EXISTS_MESSAGE;
                    } else {
                        message = SUCCESS_MESSAGE;
                    }

                    liveData.setValue(new WishlistRepository.ApiResponse<>(
                            responseBody,
                            true,
                            message  // This will be either success or already exists message
                    ));
                } else {
                    handleErrorResponse(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<WishlistAddResponse> call, @NonNull Throwable t) {
                Log.e("TAG", "API call failed: " + t.getMessage());
                liveData.setValue(new WishlistRepository.ApiResponse<>(
                        null,
                        false,
                        "Failed to connect. Please check your network."
                ));
            }
        });

        return liveData;
    }
    private void handleErrorResponse2(Response<?> response, MutableLiveData<WishlistRepository.ApiResponse<ArrayList<WishlistResponse>>> liveData) {
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                String errorMessage = extractDynamicErrorMessage(errorBody);
                liveData.setValue(new WishlistRepository.ApiResponse<>(null, false, errorMessage));
            } else {
                liveData.setValue(new WishlistRepository.ApiResponse<>(null, false, "An unknown error occurred."));
            }
        } catch (Exception e) {
            Log.e("TAG", "Error parsing error response: " + e.getMessage());
            liveData.setValue(new WishlistRepository.ApiResponse<>(null, false, "An unknown error occurred."));
        }
    }
  private void handleErrorResponse(Response<?> response, MutableLiveData<WishlistRepository.ApiResponse<WishlistAddResponse>> liveData) {
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                String errorMessage = extractDynamicErrorMessage(errorBody);
                liveData.setValue(new WishlistRepository.ApiResponse<>(null, false, errorMessage));
            } else {
                liveData.setValue(new WishlistRepository.ApiResponse<>(null, false, "An unknown error occurred."));
            }
        } catch (Exception e) {
            Log.e("TAG", "Error parsing error response: " + e.getMessage());
            liveData.setValue(new WishlistRepository.ApiResponse<>(null, false, "An unknown error occurred."));
        }
    }


    private String extractDynamicErrorMessage(String errorBody) {
        try {
            if (errorBody.trim().startsWith("{")) { // Check if JSON
                JSONObject jsonObject = new JSONObject(errorBody);
                return jsonObject.optString("message", "An error occurred.");
            }
            Document document = Jsoup.parse(errorBody);
            return document.body() != null ? document.body().text().trim() : "An unknown error occurred.";
        } catch (Exception e) {
            Log.e("TAG", "Error while parsing the error message: " + e.getMessage());
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
