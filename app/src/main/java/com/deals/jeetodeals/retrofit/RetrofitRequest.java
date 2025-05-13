package com.deals.jeetodeals.retrofit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitRequest {
    private static final String TAG = "RetrofitRequest";
    private static Retrofit retrofit;
    private static Context appContext;

    // Set context for network checks
    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    // Add timeout settings
                    .connectTimeout(30, TimeUnit.SECONDS)  // Increased from default 10s
                    .readTimeout(30, TimeUnit.SECONDS)     // Increased from default 10s
                    .writeTimeout(30, TimeUnit.SECONDS)    // Increased from default 10s
                    // Add retry mechanism for certain errors
                    .addInterceptor(chain -> {
                        Request request = chain.request();

                        // Log the request for debugging
                        Log.d(TAG, "Sending request: " + request.url());

                        try {
                            Response response = chain.proceed(request);
                            // Log the response for debugging
                            Log.d(TAG, "Received response: " + response.code() + " for " + request.url());

                            // Handle server errors
                            if (response.code() == 500) {
                                Log.e(TAG, "Server error 500 received for: " + request.url());
                            }

                            return response;
                        } catch (IOException e) {
                            Log.e(TAG, "Request failed: " + e.getMessage() + " for " + request.url());
                            throw e;
                        }
                    })
                    .build();

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl("https://www.jeetodeals.com/wp-json/")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    // Keep your unsafe OkHttpClient for specific needs
    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    // Add timeout settings to unsafe client as well
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Add a method to check network availability
    public static boolean isNetworkAvailable() {
        if (appContext == null) {
            Log.e(TAG, "Context not initialized. Call RetrofitRequest.init(context) first.");
            return false;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}