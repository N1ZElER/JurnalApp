package com.example.jurnals.data.remote.api;

import com.example.jurnals.data.remote.backendModels.DeviceTokenRequest;


import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface MyServerApi {

    @POST("student/register")
    Call<Void> registerUser(@Body Map<String, String> request);


    @POST("api/device-token")
    Call<Void> sendDeviceToken(@Body DeviceTokenRequest request);
}