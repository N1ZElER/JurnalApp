package com.example.jurnals.Class;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.jurnals.API.ApiService;
import com.example.jurnals.Adapter.ExamAdapter;
import com.example.jurnals.MainActivity;
import com.example.jurnals.Models.Exam;
import com.example.jurnals.R;
import com.example.jurnals.Response.ExamResponse;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Ekzam extends AppCompatActivity {

    SwipeRefreshLayout swipeRefresh;
    ImageButton settings, menu;
    TextView dateText;
    RecyclerView recyclerView;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    private List<Exam> exams = new ArrayList<>();
    private ExamAdapter examAdapter;
    ApiService api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ekzam);

        swipeRefresh = findViewById(R.id.swipeRefresh);
        settings = findViewById(R.id.settings);
        menu = findViewById(R.id.menu);
        dateText = findViewById(R.id.dateText);
        recyclerView = findViewById(R.id.recyclerView);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);


        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        examAdapter = new ExamAdapter(exams);
        recyclerView.setAdapter(examAdapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://msapi.top-academy.ru/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(ApiService.class);


        menu.setOnClickListener(v -> {
            if (drawerLayout != null) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.nav_shedule){
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_ekzam) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(this, News.class));
            }
            else if (id == R.id.nav_auth){
                Toast.makeText(this, "Вы уже авторизованы", Toast.LENGTH_SHORT).show();
            }
            else if (id == R.id.nav_performance) {
                startActivity(new Intent(this, Performance.class));
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        loadExam();
        swipeRefresh.setOnRefreshListener(this::loadExam);
    }

    private void loadExam() {

        swipeRefresh.setRefreshing(true);

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("token", null);

        api.getExam("Bearer " + token)
                .enqueue(new Callback<List<Exam>>() {

                    @Override
                    public void onResponse(Call<List<Exam>> call, Response<List<Exam>> response) {

                        swipeRefresh.setRefreshing(false);

                        if (response.isSuccessful() && response.body() != null) {

                            List<Exam> newExams = response.body();

                            if (newExams.isEmpty()) {

                                dateText.setText("🎉 Экзаменов нет");
                                exams.clear();
                                examAdapter.notifyDataSetChanged();
                                return;
                            }

                            dateText.setText("📚 Ближайшие экзамены");

                            exams.clear();
                            exams.addAll(newExams);
                            examAdapter.notifyDataSetChanged();

                        } else {

                            dateText.setText("❌ Ошибка ответа сервера: " + response.code());

                        }
                    }

                    @Override
                    public void onFailure(Call<List<Exam>> call, Throwable t) {

                        swipeRefresh.setRefreshing(false);

                        dateText.setText("❌ Ошибка подключения");

                        Toast.makeText(
                                Ekzam.this,
                                "Ошибка: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    // Update menu
    @Override
    protected void onResume() {
        super.onResume();
        updateCheckedItem();
    }

    // Update menu
    private void updateCheckedItem() {
        MenuItem item = navigationView.getMenu().findItem(getCheckedItemId());
        if (item != null) {
            item.setChecked(true);
        }
    }

    // Update menu
    private int getCheckedItemId() {
        String currentActivity = this.getClass().getSimpleName();
        switch (currentActivity) {
            case "MainActivity":
                return R.id.nav_shedule;
            case "ExamActivity":
                return R.id.nav_ekzam;
            case "NewsActivity":
                return R.id.nav_news;
            default:
                return R.id.nav_ekzam;
        }
    }

}