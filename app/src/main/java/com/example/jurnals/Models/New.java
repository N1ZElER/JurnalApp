package com.example.jurnals.Models;

import com.google.gson.annotations.SerializedName;

public class New {

    @SerializedName("id_bbs")
    private int news_id;

    @SerializedName("theme")
    private String theme;

    @SerializedName("time")
    private String time;

    @SerializedName("text_bbs")
    private String content;

    public int getNewsId() {
        return news_id;
    }

    public String getTheme() {
        return theme;
    }

    public String getTime() {
        return time;
    }

    public String getContent() { return content; }
}