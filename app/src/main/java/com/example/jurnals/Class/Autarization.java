package com.example.jurnals.Class;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jurnals.API.ApiService;
import com.example.jurnals.MainActivity;
import com.example.jurnals.Models.Auth;
import com.example.jurnals.R;
import com.example.jurnals.Response.LoginResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Autarization extends AppCompatActivity {

    private TextInputEditText usernameInput, passwordInput;
    private MaterialButton loginButton;
    private TextView statusText;
    private ApiService api;
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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://msapi.top-academy.ru/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(ApiService.class);

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
            autoLogin(savedUsername, savedPassword);
        } else {
            statusText.setText("Введите данные заново");
            statusText.setVisibility(TextView.GONE);
        }
    }

    private void login() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusText.setText("Введите логин и пароль");
            statusText.setVisibility(TextView.VISIBLE);
            return;
        }

        statusText.setVisibility(TextView.GONE);
        doLogin(username, password, true);
    }

    private void autoLogin(String username, String password) {
        doLogin(username, password, false);
    }

    private void doLogin(String username, String password, boolean saveCredentials) {
        Auth auth = new Auth(username, password);

        api.login(auth).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getAccessToken();
                    long expiryTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000);

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("token", token);
                    editor.putLong("tokenExpiry", expiryTime);

                    if (saveCredentials) {
                        editor.putString("username", username);
                        editor.putString("password", password);
                    }

                    editor.apply();

                    startActivity(new Intent(Autarization.this, MainActivity.class));
                    finish();

                } else {
                    // Авто-вход не удался
                    if (!saveCredentials) {
                        statusText.setText("Авто-вход не удался, введите логин и пароль");
                        statusText.setVisibility(TextView.VISIBLE);
                    } else {
                        statusText.setText("Неверный логин или пароль");
                        statusText.setVisibility(TextView.VISIBLE);
                    }
                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                statusText.setText("Ошибка сети, проверьте подключение к интернету");
                statusText.setVisibility(TextView.VISIBLE);
            }
        });
    }
}