package com.example.jurnals.data.remote.backendModels;

import com.google.gson.annotations.SerializedName;

public class BackendLoginRequest {

    @SerializedName("application_key")
    public String applicationKey;

    @SerializedName("id_city")
    public Object idCity;

    @SerializedName("username")
    public String username;

    @SerializedName("password")
    public String password;

    public BackendLoginRequest(String username, String password) {
        this.applicationKey = "6a56a5df2667e65aab73ce76d1dd737f7d1faef9c52e8b8c55ac75f565d8e8a6";
        this.idCity = null;
        this.username = username;
        this.password = password;
    }
}