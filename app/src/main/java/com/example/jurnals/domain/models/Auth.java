package com.example.jurnals.domain.models;

import com.google.gson.annotations.SerializedName;

public class Auth {

    @SerializedName("application_key")
    private String applicationKey;

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("id_city")
    private Integer idCity;

    public Auth(String username, String password) {
        this.applicationKey = "6a56a5df2667e65aab73ce76d1dd737f7d1faef9c52e8b8c55ac75f565d8e8a6";
        this.username = username;
        this.password = password;
        this.idCity = null;
    }
}