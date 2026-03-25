package com.example.jurnals.Notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MyFirebaseService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseService";
    private static final String PREFS_NAME = "app";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PENDING_FCM_TOKEN = "pending_fcm_token";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.d(TAG, "onNewToken: " + token);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String username = prefs.getString(KEY_USERNAME, null);

        Log.d(TAG, "onNewToken username from prefs = " + username);

        if (username == null || username.trim().isEmpty()) {
            prefs.edit().putString(KEY_PENDING_FCM_TOKEN, token).apply();
            Log.w(TAG, "username is null, token saved locally as pending");
            return;
        }

        sendTokenToServer(username, token);
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

        Log.d(TAG, "Push received. title=" + title + ", body=" + body);
        NotificationHelper.show(this, title, body);
    }

    public static void sendSavedTokenIfPossible(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String username = prefs.getString(KEY_USERNAME, null);
        String pendingToken = prefs.getString(KEY_PENDING_FCM_TOKEN, null);

        Log.d(TAG, "sendSavedTokenIfPossible username=" + username + ", pendingToken=" + pendingToken);

        if (username == null || username.trim().isEmpty()) {
            Log.w(TAG, "sendSavedTokenIfPossible: username is empty");
            return;
        }

        if (pendingToken == null || pendingToken.trim().isEmpty()) {
            Log.d(TAG, "sendSavedTokenIfPossible: no pending token");
            return;
        }

        new Thread(() -> {
            boolean ok = sendTokenRequest(username, pendingToken);
            if (ok) {
                prefs.edit().remove(KEY_PENDING_FCM_TOKEN).apply();
                Log.d(TAG, "pending token sent and removed from prefs");
            }
        }).start();
    }

    private void sendTokenToServer(String username, String token) {
        new Thread(() -> sendTokenRequest(username, token)).start();
    }

    private static boolean sendTokenRequest(String username, String token) {
        HttpURLConnection conn = null;

        try {
            Log.d(TAG, "sendTokenRequest username=" + username);
            Log.d(TAG, "sendTokenRequest token=" + token);

            URL url = new URL("http://194.113.209.238:3001/api/device-token");
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);

            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("fcmToken", token);

            String body = json.toString();
            Log.d(TAG, "sendTokenRequest body=" + body);

            byte[] out = body.getBytes(StandardCharsets.UTF_8);

            OutputStream os = conn.getOutputStream();
            os.write(out);
            os.flush();
            os.close();

            int code = conn.getResponseCode();
            String response = readResponse(conn, code);

            Log.d(TAG, "sendTokenRequest responseCode=" + code);
            Log.d(TAG, "sendTokenRequest responseBody=" + response);

            return code >= 200 && code < 300;
        } catch (Exception e) {
            Log.e(TAG, "sendTokenRequest error", e);
            return false;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static String readResponse(HttpURLConnection conn, int code) {
        try {
            InputStream is = (code >= 200 && code < 400)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            if (is == null) return "";

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            reader.close();
            return sb.toString();
        } catch (Exception e) {
            return "readResponse error: " + e.getMessage();
        }
    }

    public static void sendCurrentTokenNow(Context context, String username) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "sendCurrentTokenNow getToken failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.d(TAG, "sendCurrentTokenNow token=" + token);
                    Log.d(TAG, "sendCurrentTokenNow tokenLen=" + (token == null ? 0 : token.length()));

                    if (token == null || token.trim().isEmpty()) {
                        Log.w(TAG, "sendCurrentTokenNow token is empty");
                        return;
                    }

                    new Thread(() -> sendTokenRequest(username, token)).start();
                });
    }
}