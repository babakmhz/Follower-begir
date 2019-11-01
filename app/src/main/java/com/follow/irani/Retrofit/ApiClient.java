package com.follow.irani.Retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL = "https://followeriraniinstagram.com/";
    private static final String TOKEN_APP = "EDMQFLDW-T2LUGDX5-4XLWDQ6H-V9A99K4H";
    private static final String Content_Type = "application/json";
    public static Retrofit retrofit = null;


    public static Retrofit getClient() {

        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                        //return null;
                    })
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return retrofit;
    }

}
