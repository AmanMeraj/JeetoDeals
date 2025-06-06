package com.deals.jeetodeals.Fragments;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.deals.jeetodeals.Checkout.CheckoutResponse;
import com.deals.jeetodeals.Fragments.HomeFragment.HomeRepository;
import com.deals.jeetodeals.Fragments.HomeFragment.HomeResponse;
import com.deals.jeetodeals.Model.AddItems;
import com.deals.jeetodeals.Model.AppVersion;
import com.deals.jeetodeals.Model.BannerResponse;
import com.deals.jeetodeals.Model.CartResponse;
import com.deals.jeetodeals.Model.Category;
import com.deals.jeetodeals.Model.ChangePassword;
import com.deals.jeetodeals.Model.ChangePasswordResponse;
import com.deals.jeetodeals.Model.Checkout;
import com.deals.jeetodeals.Model.CouponResponse;
import com.deals.jeetodeals.Model.FcmResponse;
import com.deals.jeetodeals.Model.GetCheckout;
import com.deals.jeetodeals.Model.InvoiceResponse;
import com.deals.jeetodeals.Model.OrderDetailsResponse;
import com.deals.jeetodeals.Model.ShopResponse;
import com.deals.jeetodeals.Model.TicketResponse;
import com.deals.jeetodeals.Model.TrackingResponse;
import com.deals.jeetodeals.Model.User;
import com.deals.jeetodeals.Model.WalletResponse;
import com.deals.jeetodeals.Model.WishlistCreationResponse;
import com.deals.jeetodeals.retrofit.ApiRequest;
import com.deals.jeetodeals.retrofit.RetrofitRequest;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentsRepository {
    private static final String TAG = FragmentsRepository.class.getSimpleName();
    private final ApiRequest apiRequest;
    private static String nonce; // Global nonce storage
    private static volatile boolean isHandlingSessionExpiry = false;
    private static final Object SESSION_LOCK = new Object();

    public static final int ERROR_SESSION_EXPIRED = 403;

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

    public LiveData<ApiResponse<AppVersion>> version() {
        final MutableLiveData<ApiResponse<AppVersion>> liveData = new MutableLiveData<>();

        apiRequest.getAppVersion().enqueue(new Callback<AppVersion>() {
            @Override
            public void onResponse(@NonNull Call<AppVersion> call, @NonNull Response<AppVersion> response) {
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
                    handleErrorResponse7(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AppVersion> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Failed to connect. Please check your network."));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<GetCheckout>> checkout(String auth,String nonce) {
        final MutableLiveData<ApiResponse<GetCheckout>> liveData = new MutableLiveData<>();

        apiRequest.getCheckout(auth,nonce).enqueue(new Callback<GetCheckout>() {
            @Override
            public void onResponse(@NonNull Call<GetCheckout> call, @NonNull Response<GetCheckout> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Extract nonce from headers and store it globally

                    // Success: Return response body
                    liveData.setValue(new ApiResponse<>(response.body(), true, null));
                } else {
                    handleErrorResponse8(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetCheckout> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Failed to connect. Please check your network."));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<WishlistCreationResponse>> createWishlist(String auth) {
        final MutableLiveData<ApiResponse<WishlistCreationResponse>> liveData = new MutableLiveData<>();

        apiRequest.createWishlist(auth).enqueue(new Callback<WishlistCreationResponse>() {
            @Override
            public void onResponse(@NonNull Call<WishlistCreationResponse> call, @NonNull Response<WishlistCreationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Extract nonce from headers and store it globally

                    // Success: Return response body
                    liveData.setValue(new ApiResponse<>(response.body(), true, null));
                } else {
                    handleErrorResponse10(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<WishlistCreationResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Failed to connect. Please check your network."));
            }
        });

        return liveData;
    }

    public LiveData<ApiResponse<CouponResponse>> applyCoupon(String authToken, String nonce, String couponCode) {
        MutableLiveData<ApiResponse<CouponResponse>> liveData = new MutableLiveData<>();

        apiRequest.applyCoupon(authToken, nonce, couponCode).enqueue(new Callback<CouponResponse>() {
            @Override
            public void onResponse(@NonNull Call<CouponResponse> call, @NonNull Response<CouponResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null));
                } else if (response.code() == 400) {
                    String errorMsg = "Failed to apply coupon.";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(errorJson);
                            String message = jsonObject.optString("message");
                            String code = jsonObject.optString("code");

                            if ("woocommerce_rest_cart_coupon_error".equals(code)) {
                                errorMsg = message;  // e.g., "Coupon code \"watch100\" has already been applied."
                            } else if ("rest_invalid_param".equals(code)) {
                                errorMsg = "Your coupon code is invalid.";
                            } else {
                                errorMsg = message;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.e(TAG, errorMsg);
                    liveData.setValue(new ApiResponse<>(null, false, errorMsg));
                } else {
                    String errorMsg = "There is an issue applying the coupon, please try again later.";
                    Log.e(TAG, errorMsg);
                    liveData.setValue(new ApiResponse<>(null, false, errorMsg));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CouponResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Coupon API failed: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Network failure: " + t.getMessage()));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<ChangePasswordResponse>> changePass(String auth, ChangePassword changePassword) {
        final MutableLiveData<ApiResponse<ChangePasswordResponse>> liveData = new MutableLiveData<>();

        apiRequest.changePassword(auth,changePassword).enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChangePasswordResponse> call, @NonNull Response<ChangePasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Extract nonce from headers and store it globally

                    // Success: Return response body
                    liveData.setValue(new ApiResponse<>(response.body(), true, null));
                } else {
                    handleErrorResponse13(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChangePasswordResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Failed to connect. Please check your network."));
            }
        });

        return liveData;
    }


    public LiveData<ApiResponse<ResponseBody>> removeCoupon(String auth, String nonce) {
        final MutableLiveData<ApiResponse<ResponseBody>> liveData = new MutableLiveData<>();

        apiRequest.removeCoupon(auth, nonce).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, "Coupon removed successfully"));
                } else {
                    handleErrorResponseDelCoupon(response, liveData);

                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("CouponRepository", "API call failed: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Failed to connect. Please check your network."));
            }
        });

        return liveData;
    }


    public LiveData<ApiResponse<OrderDetailsResponse>> orderDetails(String auth, int orderId) {
        final MutableLiveData<ApiResponse<OrderDetailsResponse>> liveData = new MutableLiveData<>();

        apiRequest.getOrderDetails(auth, orderId).enqueue(new Callback<OrderDetailsResponse>() {
            @Override
            public void onResponse(@NonNull Call<OrderDetailsResponse> call, @NonNull Response<OrderDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null));
                } else {
                    handleOrderDetailsErrorResponse(response, liveData); // Your standard error handler
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderDetailsResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error fetching order details: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Something went wrong. Please try again."));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<TrackingResponse>> trackingDetails(String auth, int orderId) {
        final MutableLiveData<ApiResponse<TrackingResponse>> liveData = new MutableLiveData<>();

        apiRequest.getTracking(auth, orderId).enqueue(new Callback<TrackingResponse>() {
            @Override
            public void onResponse(@NonNull Call<TrackingResponse> call, @NonNull Response<TrackingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null));
                } else {
                    handleOrderDetailsErrorResponse2(response, liveData); // Your standard error handler
                }
            }

            @Override
            public void onFailure(@NonNull Call<TrackingResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error fetching order details: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Something went wrong. Please try again."));
            }
        });

        return liveData;
    }

    public LiveData<ApiResponse<CheckoutResponse>> checkoutPost(String auth,String nonce, Checkout checkout) {
        final MutableLiveData<ApiResponse<CheckoutResponse>> liveData = new MutableLiveData<>();

        apiRequest.checkout(auth,nonce,checkout).enqueue(new Callback<CheckoutResponse>() {
            @Override
            public void onResponse(@NonNull Call<CheckoutResponse> call, @NonNull Response<CheckoutResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Extract nonce from headers and store it globally

                    // Success: Return response body
                    liveData.setValue(new ApiResponse<>(response.body(), true, null));
                } else {
                    handleErrorResponse11(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CheckoutResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Failed to connect. Please check your network."));
            }
        });

        return liveData;
    }

    public LiveData<ApiResponse<Void>> deleteCart(String auth, String nonce) {
        final MutableLiveData<ApiResponse<Void>> liveData = new MutableLiveData<>();

        apiRequest.deleteItemInCart(auth, nonce).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                ApiResponse<Void> apiResponse;

                if (response.code() == 200) {
                    apiResponse = new ApiResponse<>(null, true, "successful");
                } else if (response.code() == ERROR_SESSION_EXPIRED) {
                    // Session expired, handle appropriately
                    synchronized (SESSION_LOCK) {
                        if (!isHandlingSessionExpiry) {
                            isHandlingSessionExpiry = true;
                            liveData.setValue(new ApiResponse<>(null, false, "Your Login has expired, please login again."));
                        } else {
                            // Don't notify again if already handling
                            Log.d(TAG, "Already handling session expiry, suppressing duplicate notification");
                        }
                    }
                    return; // Exit early as we already handled session expiry
                } else {
                    apiResponse = new ApiResponse<>(null, false, "Error code: " + response.code());
                }

                liveData.setValue(apiResponse);
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                ApiResponse<Void> errorResponse = new ApiResponse<>(null, false, "Network error: " + t.getMessage());
                liveData.setValue(errorResponse);
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
    public LiveData<ApiResponse<BannerResponse>> banner() {
        final MutableLiveData<ApiResponse<BannerResponse>> liveData = new MutableLiveData<>();

        apiRequest.getBanner().enqueue(new Callback<BannerResponse>() {
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
    public LiveData<ApiResponse<FcmResponse>> fcm(String auth, User user) {
        final MutableLiveData<ApiResponse<FcmResponse>> liveData = new MutableLiveData<>();

        apiRequest.fcm(auth,user).enqueue(new Callback<FcmResponse>() {
            @Override
            public void onResponse(@NonNull Call<FcmResponse> call, @NonNull Response<FcmResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Success: Return response body
                    liveData.setValue(new ApiResponse<>(response.body(), true, null));
                } else {
                    handleErrorResponse9(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<FcmResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Failed to connect. Please check your network."));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<InvoiceResponse>> Invoice(String auth, int id) {
        final MutableLiveData<ApiResponse<InvoiceResponse>> liveData = new MutableLiveData<>();

        apiRequest.getInvoice(auth, String.valueOf(id)).enqueue(new Callback<InvoiceResponse>() {
            @Override
            public void onResponse(@NonNull Call<InvoiceResponse> call, @NonNull Response<InvoiceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Success: Return response body
                    liveData.setValue(new ApiResponse<>(response.body(), true, response.message()));
                } else {
                    handleErrorResponse14(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<InvoiceResponse> call, @NonNull Throwable t) {
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
                } else if (response.code() == ERROR_SESSION_EXPIRED) {
                    // Session expired, handle appropriately
                    synchronized (SESSION_LOCK) {
                        if (!isHandlingSessionExpiry) {
                            isHandlingSessionExpiry = true;
                            liveData.setValue(new ApiResponse<>(null, false, "Your Login has expired, please login again."));
                        } else {
                            // Don't notify again if already handling
                            Log.d(TAG, "Already handling session expiry, suppressing duplicate notification");
                        }
                    }
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
    public LiveData<ApiResponse<ArrayList<Category>>> category() {
        final MutableLiveData<ApiResponse<ArrayList<Category>>> liveData = new MutableLiveData<>();

        apiRequest.getCategory().enqueue(new Callback<ArrayList<Category>>() {
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
    private void handleErrorResponseDelCoupon(Response<?> response, MutableLiveData<ApiResponse<ResponseBody>> liveData) {
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
    private void handleOrderDetailsErrorResponse(Response<?> response, MutableLiveData<ApiResponse<OrderDetailsResponse>> liveData) {
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
    private void handleOrderDetailsErrorResponse2(Response<?> response, MutableLiveData<ApiResponse<TrackingResponse>> liveData) {
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

    private void handleErrorResponse7(Response<?> response, MutableLiveData<ApiResponse<AppVersion>> liveData) {
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
    private void handleErrorResponse8(Response<?> response, MutableLiveData<ApiResponse<GetCheckout>> liveData) {
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
    private void handleErrorResponse10(Response<?> response, MutableLiveData<ApiResponse<WishlistCreationResponse>> liveData) {
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
    private void handleErrorResponse13(Response<?> response, MutableLiveData<ApiResponse<ChangePasswordResponse>> liveData) {
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
    private void handleErrorResponse11(Response<?> response, MutableLiveData<ApiResponse<CheckoutResponse>> liveData) {
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
    private void handleErrorResponse9(Response<?> response, MutableLiveData<ApiResponse<FcmResponse>> liveData) {
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
    private void handleErrorResponse14(Response<?> response, MutableLiveData<ApiResponse<InvoiceResponse>> liveData) {
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

    private <T> void handleSessionExpiry(MutableLiveData<HomeRepository.ApiResponse<T>> liveData) {
        synchronized (SESSION_LOCK) {
            if (!isHandlingSessionExpiry) {
                isHandlingSessionExpiry = true;
                liveData.setValue(new HomeRepository.ApiResponse<>(null, false, "Your Login has expired, please login again.", ERROR_SESSION_EXPIRED));
            } else {
                // Don't notify again if already handling
                Log.d(TAG, "Already handling session expiry, suppressing duplicate notification");
            }
        }
    }
}
