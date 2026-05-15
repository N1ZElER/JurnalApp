package com.example.jurnals.presentation.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jurnals.MainActivity;
import com.example.jurnals.core.notification.MyFirebaseService;
import com.example.jurnals.data.remote.api.ApiService;
import com.example.jurnals.data.remote.backendModels.BackendLoginResponse;
import com.example.jurnals.data.remote.client.RetrofitClient;
import com.example.jurnals.databinding.ActivityAutarizationBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorizationActivity extends AppCompatActivity {

    private static final long DOUBLE_CLICK_DELAY = 300;

    private ActivityAutarizationBinding binding;

    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;

    private ApiService api;
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
        api = RetrofitClient.getInstance().create(ApiService.class);

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

        Map<String, Object> body = new HashMap<>();
        body.put("application_key", "6a56a5df2667e65aab73ce76d1dd737f7d1faef9c52e8b8c55ac75f565d8e8a6");
        body.put("id_city", null);
        body.put("username", username);
        body.put("password", password);

        api.login(body).enqueue(new Callback<BackendLoginResponse>() {
            @Override
            public void onResponse(Call<BackendLoginResponse> call,
                                   Response<BackendLoginResponse> response) {

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getAccessToken() != null) {

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

                    FirebaseMessaging.getInstance()
                            .getToken()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    MyFirebaseService.sendCurrentTokenNow(
                                            AuthorizationActivity.this,
                                            username
                                    );

                                    MyFirebaseService.sendSavedTokenIfPossible(
                                            AuthorizationActivity.this
                                    );
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