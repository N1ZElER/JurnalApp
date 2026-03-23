package com.example.jurnals.Models;

public class BackendLoginRequest {
    private String username;
    private String password;

    public BackendLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}