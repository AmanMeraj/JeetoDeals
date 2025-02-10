package com.deals.jeetodeals.Fragments;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.deals.jeetodeals.Fragments.HomeFragment.HomeRepository;
import com.deals.jeetodeals.Fragments.HomeFragment.HomeResponse;
import com.deals.jeetodeals.Model.AddItems;
import com.deals.jeetodeals.Model.BannerResponse;
import com.deals.jeetodeals.Model.CartResponse;
import com.deals.jeetodeals.Model.Category;
import com.deals.jeetodeals.Model.ShopResponse;
import com.deals.jeetodeals.Model.TicketResponse;
import com.deals.jeetodeals.Model.WalletResponse;
import com.deals.jeetodeals.retrofit.ApiRequest;
import com.deals.jeetodeals.retrofit.RetrofitRequest;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentsRepository {
    private static final String TAG = FragmentsRepository.class.getSimpleName();
    private final ApiRequest apiRequest;
    private static String nonce; // Global nonce storage

    public FragmentsRepository() {
        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
    }

    /**
     * Fetches cart data and extracts nonce from headers.
     *
     * @param auth Authorization token.
     * @return LiveData containing ApiResponse wrapped in CartResponse.
     */
    public LiveData<ApiResponse<CartResponse>> cart(String auth) {
        final MutableLiveData<ApiResponse<CartResponse>> liveData = new MutableLiveData<>();

        apiRequest.getCart(auth).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(@NonNull Call<CartResponse> call, @NonNull Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Extract nonce from headers and store it globally
                    String nonceHeader = response.headers().get("nonce");
                    if (nonceHeader != null) {
                        nonce = nonceHeader;
                        Log.d(TAG, "Nonce updated: " + nonce);
                    }

                    // Success: Return response body
                    liveData.setValue(new ApiResponse<>(response.body(), true, null));
                } else {
                    handleErrorResponse(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CartResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Failed to connect. Please check your network."));
            }
        });

        return liveData;
    }



    /**
     * Add's Item in the cart by sending the nonce in the header
     * @param auth Authorization token.
     * @param addItems This is the parameter with id and quantity sent as a body
     * @return LiveData containing ApiResponse wrapped in CartResponse.
     */

    public LiveData<ApiResponse<CartResponse>> addItem(String auth, String nonce, AddItems addItems) {
        final MutableLiveData<ApiResponse<CartResponse>> liveData = new MutableLiveData<>();

        apiRequest.AddToCart(auth,nonce,addItems).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(@NonNull Call<CartResponse> call, @NonNull Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Success: Return response body
                    liveData.setValue(new ApiResponse<>(response.body(), true, null));
                } else {
                    handleErrorResponse(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CartResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Failed to connect. Please check your network."));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<BannerResponse>> banner(String auth) {
        final MutableLiveData<ApiResponse<BannerResponse>> liveData = new MutableLiveData<>();

        apiRequest.getBanner(auth).enqueue(new Callback<BannerResponse>() {
            @Override
            public void onResponse(@NonNull Call<BannerResponse> call, @NonNull Response<BannerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Success: Return response body
                    liveData.setValue(new ApiResponse<>(response.body(), true, null));
                } else {
                    handleErrorResponse5(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BannerResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Failed to connect. Please check your network."));
            }
        });

        return liveData;
    }


    public LiveData<ApiResponse<ArrayList<WalletResponse>>> getWallet(String auth) {
        final MutableLiveData<ApiResponse<ArrayList<WalletResponse>>> liveData = new MutableLiveData<>();

        apiRequest.GetWallet(auth).enqueue(new Callback<ArrayList<WalletResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<WalletResponse>> call, @NonNull Response<ArrayList<WalletResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Success: Return response body
                    liveData.setValue(new ApiResponse<>(response.body(), true, null));
                } else {
                    handleErrorResponse2(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<WalletResponse>> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Failed to connect. Please check your network."));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<ArrayList<Category>>> category(String auth) {
        final MutableLiveData<ApiResponse<ArrayList<Category>>> liveData = new MutableLiveData<>();

        apiRequest.getCategory(auth).enqueue(new Callback<ArrayList<Category>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Category>> call, @NonNull Response<ArrayList<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Success: Return response body
                    liveData.setValue(new ApiResponse<>(response.body(), true, null));
                } else {
                    handleErrorResponse6(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Category>> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Failed to connect. Please check your network."));
            }
        });

        return liveData;
    }

    /**
     * Add's Item in the cart by sending the nonce in the header
     * @param auth Authorization token.
     * @param addItems This is the parameter with  key but only key is being sent here to remove the item from the cart
     * @return LiveData containing ApiResponse wrapped in CartResponse.
     */

    public LiveData<ApiResponse<CartResponse>> removeItem(String auth, String nonce, AddItems addItems) {
        final MutableLiveData<ApiResponse<CartResponse>> liveData = new MutableLiveData<>();

        apiRequest.RemoveItem(auth,nonce,addItems).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(@NonNull Call<CartResponse> call, @NonNull Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Success: Return response body
                    liveData.setValue(new ApiResponse<>(response.body(), true, null));
                } else {
                    handleErrorResponse(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CartResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Failed to connect. Please check your network."));
            }
        });

        return liveData;
    }

    /**
     * Add's Item in the cart by sending the nonce in the header
     * @param auth Authorization token.
     * @param addItems This is the parameter with quantity and key but only key is being sent here to remove the item from the cart
     * @return LiveData containing ApiResponse wrapped in CartResponse.
     */
    public LiveData<ApiResponse<CartResponse>> updateItem(String auth, String nonce, AddItems addItems) {
        final MutableLiveData<ApiResponse<CartResponse>> liveData = new MutableLiveData<>();

        apiRequest.UpdateItem(auth,nonce,addItems).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(@NonNull Call<CartResponse> call, @NonNull Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Success: Return response body
                    liveData.setValue(new ApiResponse<>(response.body(), true, null));
                } else {
                    handleErrorResponse(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CartResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Failed to connect. Please check your network."));
            }
        });

        return liveData;
    }
    public MutableLiveData<ApiResponse<Integer>> getCurrentBalance(String auth) {
        MutableLiveData<ApiResponse<Integer>> liveData = new MutableLiveData<>();

        // API call to get current balance
        apiRequest.GetBalance(auth).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, "Balance fetched successfully"));
                } else {
                    handleErrorResponse3(response, liveData);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.e("FragmentsRepository", "Error: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Network error occurred"));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<TicketResponse>> getCustomerTickets(String auth, String customerId) {
        final MutableLiveData<ApiResponse<TicketResponse>> liveData = new MutableLiveData<>();

        apiRequest.GetCustomerTickets(auth, customerId).enqueue(new Callback<TicketResponse>() {
            @Override
            public void onResponse(@NonNull Call<TicketResponse> call, @NonNull Response<TicketResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null));
                } else {
                    handleErrorResponse4(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TicketResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Failed to connect. Please check your network."));
            }
        });

        return liveData;
    }

    /**
     * Extracts an error message and updates LiveData accordingly.
     */
    private void handleErrorResponse(Response<?> response, MutableLiveData<ApiResponse<CartResponse>> liveData) {
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
    private void handleErrorResponse4(Response<?> response, MutableLiveData<ApiResponse<TicketResponse>> liveData) {
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
    private void handleErrorResponse5(Response<?> response, MutableLiveData<ApiResponse<BannerResponse>> liveData) {
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
    /**
    * handleErrorResponse2 handles the response of wallet or any api Which has a type ArrayList<Response>
     */
    private void handleErrorResponse2(Response<?> response, MutableLiveData<ApiResponse<ArrayList<WalletResponse>>> liveData) {
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
    private void handleErrorResponse6(Response<?> response, MutableLiveData<ApiResponse<ArrayList<Category>>> liveData) {
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
    private void handleErrorResponse3(Response<Integer> response, MutableLiveData<ApiResponse<Integer>> liveData) {
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



    /**
     * Extracts a dynamic error message from JSON or HTML.
     */
    private String extractDynamicErrorMessage(String errorBody) {
        try {
            if (errorBody.trim().startsWith("{")) { // Check if JSON
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

    /**
     * Returns the stored nonce.
     */
    public static String getNonce() {
        return nonce;
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
