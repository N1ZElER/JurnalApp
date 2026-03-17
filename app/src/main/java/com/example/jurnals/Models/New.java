package com.example.jurnals.Models;

import com.google.gson.annotations.SerializedName;

public class New {

    private boolean expanded = false;
    private String fullText;

    @SerializedName("id_bbs")
    private int id_bbs;

    @SerializedName("theme")
    private String theme;

    @SerializedName("time")
    private String time;

    @SerializedName("text_bbs")
    private String text_bbs;

    public int getId_bbs() {
        return id_bbs;
    }

    public String getTheme() {
        return theme;
    }

    public String getTime() {
        return time;
    }

    public String getText_bbs() {
        return text_bbs;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }
}