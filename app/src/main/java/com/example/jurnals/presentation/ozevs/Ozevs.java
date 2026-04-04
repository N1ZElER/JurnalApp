package com.example.jurnals.presentation.ozevs;

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

import com.example.jurnals.data.remote.api.ApiService;
import com.example.jurnals.data.remote.client.RetrofitClient;
import com.example.jurnals.domain.models.Ozev;
import com.example.jurnals.presentation.exams.Ekzam;
import com.example.jurnals.MainActivity;
import com.example.jurnals.R;
import com.example.jurnals.presentation.news.News;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ozevs extends AppCompatActivity {

    SwipeRefreshLayout swipeRefresh;
    ImageButton settings, menu;
    TextView dateText;
    RecyclerView recyclerView;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ApiService api;
    OzevsAdapter ozevsAdapter;
    Context context;
    private List<Ozev> ozevsList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ozevs);

        swipeRefresh = findViewById(R.id.swipeRefresh);
        settings = findViewById(R.id.settings);
        menu = findViewById(R.id.menu);
        dateText = findViewById(R.id.dateText);
        recyclerView = findViewById(R.id.recyclerView);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        context = this;

        api = RetrofitClient.getInstance().create(ApiService.class);

        ozevsAdapter = new OzevsAdapter(api, ozevsList);
        recyclerView.setAdapter(ozevsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadOzevs();

        menu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_shedule) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_ekzam) {
                startActivity(new Intent(this, Ekzam.class));
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(this, News.class));
            } else if (id == R.id.nav_auth) {
                Toast.makeText(this, "Вы уже авторизованы", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_performance) {
                Toast.makeText(this, "Пока в разработке", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_ozevs) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void loadOzevs() {
        swipeRefresh.setEnabled(false);
        swipeRefresh.setRefreshing(true);

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("token", null);

        if (token == null) {
            swipeRefresh.setRefreshing(false);
            dateText.setText("Токен не найден");
            return;
        }

        api.getOzevs("Bearer " + token).enqueue(new Callback<List<Ozev>>() {
            @Override
            public void onResponse(Call<List<Ozev>> call,
                                   Response<List<Ozev>> response) {
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Ozev> newOzevs = response.body();

                    if (newOzevs.isEmpty()) {
                        dateText.setText("Пока что нету отзывов");
                        ozevsList.clear();
                        ozevsAdapter.notifyDataSetChanged();
                        return;
                    }

                    ozevsList.clear();
                    ozevsList.addAll(newOzevs);
                    ozevsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Ozev>> call, Throwable t) {
                swipeRefresh.setEnabled(false);
                swipeRefresh.setRefreshing(false);
                dateText.setText("❌ Ошибка подключения");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCheckedItem();
    }

    private void updateCheckedItem() {
        MenuItem item = navigationView.getMenu().findItem(getCheckedItemId());
        if (item != null) item.setChecked(true);
    }

    private int getCheckedItemId() {
        String currentActivity = this.getClass().getSimpleName();

        switch (currentActivity) {
            case "MainActivity":
                return R.id.nav_shedule;
            case "ExamActivity":
                return R.id.nav_ekzam;
            case "NewsActivity":
                return R.id.nav_news;
            case "OzevsActivity":
                return R.id.nav_ozevs;
            default:
                return R.id.nav_ozevs;
        }
    }
}