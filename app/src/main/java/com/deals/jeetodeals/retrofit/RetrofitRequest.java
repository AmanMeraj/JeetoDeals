package com.deals.jeetodeals.retrofit;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitRequest {


    private static Retrofit retrofit;
    public static Retrofit getRetrofitInstance() {


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Response response = chain.proceed(request);

                        Gson gson = new Gson();
                        String json = gson.toJson(response.message());
                        String json2 = gson.toJson(request.body());
                        Log.d("TAG", "getReview: "+json);

                        Log.d("INTERCEPT", "intercept: "+json2);

                        // todo deal with the issues the way you need to
                        if (response.code() == 500) {
                            return response;
                        }
                        return response;
                    }
                })
                .build();
        if (retrofit == null) {


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

}
