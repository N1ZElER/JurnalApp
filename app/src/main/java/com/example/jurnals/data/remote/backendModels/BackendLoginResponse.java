package com.example.jurnals.data.remote.backendModels;

import com.google.gson.annotations.SerializedName;

public class BackendLoginResponse {

    @SerializedName("ok")
    private boolean ok;

    @SerializedName("accessToken")
    private String accessToken;

    public boolean isOk() {
        return ok;
    }

    public String getAccessToken() {
        return accessToken;
    }
}