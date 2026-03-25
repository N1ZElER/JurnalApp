package com.example.jurnals.Class;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jurnals.API.MyServerApi;
import com.example.jurnals.BackendModels.BackendLoginRequest;
import com.example.jurnals.BackendModels.BackendLoginResponse;
import com.example.jurnals.Client.MyServerClient;
import com.example.jurnals.MainActivity;
import com.example.jurnals.Notification.MyFirebaseService;
import com.example.jurnals.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Autarization extends AppCompatActivity {

    private static final String TAG = "Autarization";

    private TextInputEditText usernameInput, passwordInput;
    private MaterialButton loginButton;
    private TextView statusText;
    private MyServerApi api;
    private SharedPreferences prefs;
    private long lastClickTime = 0;
    private static final long DOUBLE_CLICK_DELAY = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autarization);

        usernameInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginBtn);
        statusText = findViewById(R.id.statusText);

        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        api = MyServerClient.getInstance().create(MyServerApi.class);

        checkTokenAndLogin();

        loginButton.setOnClickListener(v -> {
            long now = System.currentTimeMillis();
            if (now - lastClickTime < DOUBLE_CLICK_DELAY) {
                statusText.setText("Используй данные от Jurnal");
                statusText.setVisibility(TextView.VISIBLE);
            } else {
                statusText.setVisibility(TextView.GONE);
                login();
            }
            lastClickTime = now;
        });
    }

    private void checkTokenAndLogin() {
        String token = prefs.getString("token", null);
        long tokenExpiry = prefs.getLong("tokenExpiry", 0);
        long now = System.currentTimeMillis();

        if (token != null && now < tokenExpiry) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        String savedUsername = prefs.getString("username", null);
        String savedPassword = prefs.getString("password", null);

        if (savedUsername != null && savedPassword != null) {
            statusText.setText("Сессия устарела, выполняем авто-вход...");
            statusText.setVisibility(TextView.VISIBLE);
            doLogin(savedUsername, savedPassword, false);
        } else {
            statusText.setVisibility(TextView.GONE);
        }
    }

    private void login() {
        String username = usernameInput.getText() != null ? usernameInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";

        if (username.isEmpty() || password.isEmpty()) {
            statusText.setText("Введите логин и пароль");
            statusText.setVisibility(TextView.VISIBLE);
            return;
        }

        doLogin(username, password, true);
    }

    private void doLogin(String username, String password, boolean saveLocalCredentials) {
        BackendLoginRequest request = new BackendLoginRequest(username, password);

        api.login(request).enqueue(new Callback<BackendLoginResponse>() {
            @Override
            public void onResponse(Call<BackendLoginResponse> call, Response<BackendLoginResponse> response) {

                if (response.isSuccessful() && response.body() != null && response.body().isOk()) {
                    String token = response.body().getAccessToken();
                    long expiryTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000);

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("token", token);
                    editor.putLong("tokenExpiry", expiryTime);

                    if (saveLocalCredentials) {
                        editor.putString("username", username);
                        editor.putString("password", password);
                    } else {
                        editor.putString("username", username);
                    }

                    editor.apply();

                    SharedPreferences appPrefs = getSharedPreferences("app", MODE_PRIVATE);
                    appPrefs.edit().putString("username", username).apply();

                    Log.d(TAG, "login ok, username saved to auth + app prefs: " + username);

                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(task -> {
                                if (!task.isSuccessful()) {
                                    Log.e(TAG, "FCM getToken failed", task.getException());
                                } else {
                                    String fcmToken = task.getResult();
                                    Log.d(TAG, "CURRENT TOKEN AFTER LOGIN = " + fcmToken);
                                    Log.d(TAG, "CURRENT TOKEN LEN = " + (fcmToken == null ? 0 : fcmToken.length()));

                                    MyFirebaseService.sendCurrentTokenNow(Autarization.this, username);
                                    MyFirebaseService.sendSavedTokenIfPossible(Autarization.this);
                                }

                                startActivity(new Intent(Autarization.this, MainActivity.class));
                                finish();
                            });

                } else {
                    statusText.setText("Ошибка входа: " + response.code());
                    statusText.setVisibility(TextView.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<BackendLoginResponse> call, Throwable t) {
                statusText.setText("Ошибка сети: " + t.getMessage());
                statusText.setVisibility(TextView.VISIBLE);
            }
        });
    }
}