package com.example.jurnals.Notification;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // maybe token on server
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        String title = null;
        String body = null;

        if (message.getNotification() != null) {
            title = message.getNotification().getTitle();
            body = message.getNotification().getBody();
        }

        if (title == null && !message.getData().isEmpty()) {
            title = message.getData().get("title");
        }

        if (body == null && !message.getData().isEmpty()) {
            body = message.getData().get("body");
        }

        if (title == null) title = "Посещение";
        if (body == null) body = "Обновился статус посещения";

        NotificationHelper.show(this, title, body);
    }
}