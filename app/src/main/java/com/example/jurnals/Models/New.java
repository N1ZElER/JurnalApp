package com.example.jurnals.Models;

import com.google.gson.annotations.SerializedName;

public class New {

    @SerializedName("id_bbs")
    private int idBbs;

    @SerializedName("theme")
    private String theme;

    @SerializedName("time")
    private String time;

    @SerializedName("viewed")
    private boolean viewed;


    public int getIdBbs() {
        return idBbs;
    }

    public String getTheme() {
        return theme;
    }

    public String getTime() {
        return time;
    }

    public boolean isViewed() {
        return viewed;
    }
}
