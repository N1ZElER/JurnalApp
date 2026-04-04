package com.example.jurnals.data.remote.dto.response;

public class VisitResponse {

    private String date;
    private int lesson;
    private boolean present;

    public String getDate() {
        return date;
    }

    public int getLesson() {
        return lesson;
    }

    public boolean isPresent() {
        return present;
    }
}