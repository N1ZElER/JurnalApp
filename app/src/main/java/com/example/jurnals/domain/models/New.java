package com.example.jurnals.domain.models;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

public class New {

    @SerializedName("id_bbs")
    private int id_bbs;

    @SerializedName("theme")
    private String theme;

    @SerializedName("time")
    private String time;

    @SerializedName("viewed")
    private boolean viewed;

    @SerializedName("text_bbs")
    private String text_bbs;


    private boolean expanded = false;

    private transient String fullText;
    private transient Bitmap imageBitmap;


    public int getId_bbs() {
        return id_bbs;
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

    public String getText_bbs() {
        return text_bbs;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public String getFullText() {
        return fullText;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }




    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }
}