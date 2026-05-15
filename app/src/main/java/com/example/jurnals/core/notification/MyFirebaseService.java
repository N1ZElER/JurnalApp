package com.example.jurnals.core.notification;

import androidx.annotation.NonNull;

import com.example.jurnals.core.notification.NotificationHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {

        String title = "Journal";
        String body = "Update";

        if (message.getNotification() != null) {
            title = message.getNotification().getTitle();
            body = message.getNotification().getBody();
        }

        NotificationHelper.show(this, title, body);
    }
}