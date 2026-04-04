package com.example.jurnals.data.remote.client;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyServerClient {
    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://194.113.209.238:3001/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}