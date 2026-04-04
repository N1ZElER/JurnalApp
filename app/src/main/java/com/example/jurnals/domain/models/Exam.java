package com.example.jurnals.domain.models;

import com.google.gson.annotations.SerializedName;

public class Exam {

    @SerializedName("date")
    private String date;

    @SerializedName("spec")
    private String spec;

    public String getDate() {
        return date;
    }

    public String getSpec() {
        return spec;
    }
}
