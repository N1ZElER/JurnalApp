package com.example.jurnals.data.remote.api;



import com.example.jurnals.data.remote.backendModels.BackendLoginRequest;
import com.example.jurnals.data.remote.backendModels.BackendLoginResponse;
import com.example.jurnals.data.remote.backendModels.DeviceTokenRequest;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface MyServerApi {

    @Headers({
            "origin: https://journal.top-academy.ru",
            "referer: https://journal.top-academy.ru/",
            "accept-language: ru_RU, ru",
            "user-agent: Mozilla/5.0"
    })
    @POST("/api/login")
    Call<BackendLoginResponse> login(@Body BackendLoginRequest request);

    @POST("/api/device-token")
    Call<Void> sendDeviceToken(@Body  DeviceTokenRequest request);
}