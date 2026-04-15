package com.example.jurnals.presentation.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jurnals.MainActivity;
import com.example.jurnals.core.notification.MyFirebaseService;
import com.example.jurnals.data.remote.api.MyServerApi;
import com.example.jurnals.data.remote.backendModels.BackendLoginRequest;
import com.example.jurnals.data.remote.backendModels.BackendLoginResponse;
import com.example.jurnals.data.remote.client.MyServerClient;
import com.example.jurnals.databinding.ActivityAutarizationBinding;
import com.example.jurnals.databinding.ActivityMainBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorizationActivity extends AppCompatActivity {

    private static final String TAG = "AuthorizationActivity";
    private static final long DOUBLE_CLICK_DELAY = 300;

    private ActivityAutarizationBinding binding;

    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;

    private MyServerApi api;
    private SharedPreferences prefs;

    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAutarizationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        usernameInput = binding.email;
        passwordInput = binding.password;
        loginButton = binding.loginBtn;

        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        api = MyServerClient.getInstance().create(MyServerApi.class);

        checkTokenAndLogin();

        loginButton.setOnClickListener(v -> {
            long now = System.currentTimeMillis();

            if (now - lastClickTime < DOUBLE_CLICK_DELAY) {
                showStatus("Используй данные от Jurnal");
            } else {
                hideStatus();
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
            openMainActivity();
            return;
        }

        String savedUsername = prefs.getString("username", null);
        String savedPassword = prefs.getString("password", null);

        if (savedUsername != null && savedPassword != null) {
            showStatus("Сессия устарела, выполняем авто-вход...");
            doLogin(savedUsername, savedPassword, false);
        } else {
            hideStatus();
        }
    }

    private void login() {
        String username = usernameInput.getText() != null
                ? usernameInput.getText().toString().trim()
                : "";

        String password = passwordInput.getText() != null
                ? passwordInput.getText().toString().trim()
                : "";

        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Введите логин и пароль");
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
                    editor.putString("username", username);

                    if (saveLocalCredentials) {
                        editor.putString("password", password);
                    }

                    editor.apply();

                    SharedPreferences appPrefs = getSharedPreferences("app", MODE_PRIVATE);
                    appPrefs.edit().putString("username", username).apply();

                    Log.d(TAG, "Login successful. Username saved: " + username);

                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(task -> {
                                if (!task.isSuccessful()) {
                                    Log.e(TAG, "FCM getToken failed", task.getException());
                                } else {
                                    String fcmToken = task.getResult();
                                    Log.d(TAG, "CURRENT TOKEN AFTER LOGIN = " + fcmToken);
                                    Log.d(TAG, "CURRENT TOKEN LEN = " + (fcmToken == null ? 0 : fcmToken.length()));

                                    MyFirebaseService.sendCurrentTokenNow(AuthorizationActivity.this, username);
                                    MyFirebaseService.sendSavedTokenIfPossible(AuthorizationActivity.this);
                                }

                                openMainActivity();
                            });

                } else {
                    showStatus("Ошибка входа: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BackendLoginResponse> call, Throwable t) {
                showStatus("Ошибка сети: " + t.getMessage());
            }
        });
    }

    private void openMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void showStatus(String message) {
        if (binding.statusText != null) {
            binding.statusText.setText(message);
            binding.statusText.setVisibility(View.VISIBLE);
        }
    }

    private void hideStatus() {
        if (binding.statusText != null) {
            binding.statusText.setVisibility(View.GONE);
        }
    }
}