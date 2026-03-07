package com.example.jurnals.Class;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
    TextInputEditText usernameInput;
    TextInputEditText passwordInput;
    MaterialButton loginButton;
    ApiService api;
    TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autarization);

        usernameInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginBtn);
        statusText = findViewById(R.id.statusText);

        // check token
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("token", null);

        if(token != null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://msapi.top-academy.ru/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(ApiService.class);

        loginButton.setOnClickListener(v -> login());
    }

    private void login() {

        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if(username.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Введите логин и пароль",Toast.LENGTH_SHORT).show();
            return;
        }

        Auth auth = new Auth(username, password);

        api.login(auth).enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    String token = response.body().getAccessToken();

                   // save token or auth
                    SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
                    prefs.edit().putString("token", token).apply();

                    Toast.makeText(
                            Autarization.this,
                            "Добро пожаловать",
                            Toast.LENGTH_SHORT
                    ).show();


                    Intent intent = new Intent(Autarization.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    Toast.makeText(
                            Autarization.this,
                            "Неверные данные",
                            Toast.LENGTH_SHORT
                    ).show();
                    statusText.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

                Toast.makeText(
                        Autarization.this,
                        "Ошибка сети",
                        Toast.LENGTH_SHORT
                ).show();

            }
        });

    }
}
