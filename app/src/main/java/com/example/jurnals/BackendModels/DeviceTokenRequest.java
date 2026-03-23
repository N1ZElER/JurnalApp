package com.example.jurnals.BackendModels;

public class DeviceTokenRequest {
    private String fcmToken;

    public DeviceTokenRequest(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}