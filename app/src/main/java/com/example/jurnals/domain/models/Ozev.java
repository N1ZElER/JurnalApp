package com.example.jurnals.domain.models;

public class Ozev {

    private String date;
    private String full_spec;
    private String message;
    private String spec;
    private String teacher;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFull_spec() {
        return full_spec;
    }

    public void setFull_spec(String full_spec) {
        this.full_spec = full_spec;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
}