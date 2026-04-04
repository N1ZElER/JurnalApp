package com.example.jurnals.presentation.news;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.jurnals.presentation.exams.Ekzam;
import com.example.jurnals.data.remote.api.ApiService;
import com.example.jurnals.data.remote.client.RetrofitClient;
import com.example.jurnals.MainActivity;
import com.example.jurnals.domain.models.New;
import com.example.jurnals.R;
import com.example.jurnals.presentation.ozevs.Ozevs;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class News extends AppCompatActivity {

    SwipeRefreshLayout swipeRefresh;
    ImageButton settings, menu;
    TextView dateText;
    RecyclerView recyclerView;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ApiService api;
    NewsAdapter newsAdapter;
    Context context;
    private List<New> newsList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        swipeRefresh = findViewById(R.id.swipeRefresh);
        settings = findViewById(R.id.settings);
        menu = findViewById(R.id.menu);
        dateText = findViewById(R.id.dateText);
        recyclerView = findViewById(R.id.recyclerView);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        context = this;

        newsList = new ArrayList<>();


        api = RetrofitClient.getInstance().create(ApiService.class);

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("token", null);

        newsAdapter = new NewsAdapter(newsList, api, token);
        recyclerView.setAdapter(newsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        menu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_shedule) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_ekzam) {
                startActivity(new Intent(this, Ekzam.class));
            } else if (id == R.id.nav_news) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (id == R.id.nav_auth) {
                Toast.makeText(this, "Вы уже авторизованы", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_performance) {
                Toast.makeText(this, "Пока в разработке", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_ozevs) {
                startActivity(new Intent(this, Ozevs.class));
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        dateText.setText("Объявления");

        loadNews();
        swipeRefresh.setOnRefreshListener(this::loadNews);
    }

    private void loadNews() {
        swipeRefresh.setEnabled(false);
        swipeRefresh.setRefreshing(true);

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("token", null);

        api.getNews("Bearer " + token).enqueue(new Callback<List<New>>() {
            @Override
            public void onResponse(Call<List<New>> call, Response<List<New>> response) {
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<New> newNews = response.body();

                    if (newNews.isEmpty()) {
                        dateText.setText("Объявлений нет");
                        newsList.clear();
                        newsAdapter.notifyDataSetChanged();
                        return;
                    }

                    dateText.setText("Последние объявления");

                    newsList.clear();
                    newsList.addAll(newNews);
                    newsAdapter.notifyDataSetChanged();

                } else {
                    dateText.setText("❌ Ошибка ответа сервера: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<New>> call, Throwable t) {
                swipeRefresh.setEnabled(false);
                swipeRefresh.setRefreshing(false);
                dateText.setText("❌ Ошибка подключения");
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
        if (item != null) item.setChecked(true);
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
                return R.id.nav_news;
        }
    }
}