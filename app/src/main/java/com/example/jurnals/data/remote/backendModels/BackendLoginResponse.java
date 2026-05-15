package com.example.jurnals.data.remote.backendModels;

import com.google.gson.annotations.SerializedName;

public class BackendLoginResponse {

    @SerializedName("access_token")
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }
}