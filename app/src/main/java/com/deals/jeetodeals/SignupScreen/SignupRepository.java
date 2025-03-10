package com.deals.jeetodeals.SignupScreen;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.deals.jeetodeals.Model.Login;
import com.deals.jeetodeals.Model.Signup;
import com.deals.jeetodeals.SignupScreen.ExistsResponse;
import com.deals.jeetodeals.SignupScreen.SignupResponse;
import com.deals.jeetodeals.retrofit.ApiRequest;
import com.deals.jeetodeals.retrofit.RetrofitRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.json.JSONObject;


public class SignupRepository {
    private static final String TAG = SignupRepository.class.getSimpleName();
    private final ApiRequest apiRequest;

    public SignupRepository() {
        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
    }

    public LiveData<ApiResponse<ExistsResponse>> signupPost(Signup signup) {
        final MutableLiveData<ApiResponse<ExistsResponse>> liveData = new MutableLiveData<>();

        apiRequest.postSignup(signup).enqueue(new Callback<ExistsResponse>() {
            @Override
            public void onResponse(@NonNull Call<ExistsResponse> call, @NonNull Response<ExistsResponse> response) {

                Log.d("AMAN", "onResponse: "+response.code());
                if (response.isSuccessful() && response.body() != null) {
                    // Success case: pass the data back
                    liveData.setValue(new ApiResponse<>(response.body(), true, null));
                } else if (response.errorBody() != null) {
                    try {
                        // Extract the error message from the response
                        String errorBody = response.errorBody().string();
                        String errorMessage = extractDynamicErrorMessage(errorBody);
                        Log.d("AMAN", "onResponse:try "+errorMessage);
                        liveData.setValue(new ApiResponse<>(null, false, errorMessage));
                    } catch (Exception e) {
                        Log.d("AMAN", "onResponse: exception");
                        Log.e("AMAN", "Error parsing error response: " + e.getMessage());
                        liveData.setValue(new ApiResponse<>(null, false, "An unknown error occurred."));
                    }
                } else {
                    Log.d("AMAN", "onResponse: else");
                    liveData.setValue(new ApiResponse<>(null, false, "An unknown error occurred."));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ExistsResponse> call, @NonNull Throwable t) {
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
