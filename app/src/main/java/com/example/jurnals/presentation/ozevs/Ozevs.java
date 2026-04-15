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
import com.example.jurnals.databinding.ActivityAutarizationBinding;
import com.example.jurnals.databinding.ActivityNewsBinding;
import com.example.jurnals.databinding.ActivityOzevsBinding;
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

    private ActivityOzevsBinding binding;
    ApiService api;
    OzevsAdapter ozevsAdapter;
    Context context;
    private List<Ozev> ozevsList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOzevsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        api = RetrofitClient.getInstance().create(ApiService.class);

        ozevsAdapter = new OzevsAdapter(api, ozevsList);
        binding.recyclerView.setAdapter(ozevsAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadOzevs();

        binding.menu.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));

        binding.navigationView.setNavigationItemSelectedListener(item -> {
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
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            }

            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void loadOzevs() {
        binding.swipeRefresh.setEnabled(false);
        binding.swipeRefresh.setRefreshing(true);

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("token", null);

        if (token == null) {
            binding.swipeRefresh.setRefreshing(false);
            binding.dateText.setText("Токен не найден");
            return;
        }

        api.getOzevs("Bearer " + token).enqueue(new Callback<List<Ozev>>() {
            @Override
            public void onResponse(Call<List<Ozev>> call,
                                   Response<List<Ozev>> response) {
                binding.swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Ozev> newOzevs = response.body();

                    if (newOzevs.isEmpty()) {
                        binding.dateText.setText("Пока что нету отзывов");
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
                binding.swipeRefresh.setEnabled(false);
                binding.swipeRefresh.setRefreshing(false);
                binding.dateText.setText("❌ Ошибка подключения");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCheckedItem();
    }

    private void updateCheckedItem() {
        MenuItem item = binding.navigationView.getMenu().findItem(getCheckedItemId());
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