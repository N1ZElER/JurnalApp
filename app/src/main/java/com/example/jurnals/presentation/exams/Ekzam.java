package com.example.jurnals.presentation.exams;

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
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.jurnals.data.remote.api.ApiService;
import com.example.jurnals.data.remote.client.RetrofitClient;
import com.example.jurnals.MainActivity;
import com.example.jurnals.databinding.ActivityAutarizationBinding;
import com.example.jurnals.databinding.ActivityEkzamBinding;
import com.example.jurnals.domain.models.Exam;
import com.example.jurnals.R;
import com.example.jurnals.presentation.news.News;
import com.example.jurnals.presentation.ozevs.Ozevs;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ekzam extends AppCompatActivity {

    private ActivityEkzamBinding binding;
    private List<Exam> exams = new ArrayList<>();
    private ExamAdapter examAdapter;
    ApiService api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEkzamBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        examAdapter = new ExamAdapter(exams);
        binding.recyclerView.setAdapter(examAdapter);


        api = RetrofitClient.getInstance().create(ApiService.class);


        binding.menu.setOnClickListener(v -> {
            if (binding.drawerLayout != null) {
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        binding.navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.nav_shedule){
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_ekzam) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(this, News.class));
            }
            else if (id == R.id.nav_auth){
                Toast.makeText(this, "Вы уже авторизованы", Toast.LENGTH_SHORT).show();
            }
            else if (id == R.id.nav_performance) {
//                startActivity(new Intent(this, Performance.class));
                Toast.makeText(this,"Пока в разработке", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_ozevs) {
                startActivity(new Intent(this, Ozevs.class));
            }

            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        loadExam();

        binding.swipeRefresh.setOnRefreshListener(this::loadExam);
    }

    private void loadExam() {
        binding.swipeRefresh.setEnabled(false);

        binding.swipeRefresh.setRefreshing(true);

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("token", null);

        api.getExam("Bearer " + token)
                .enqueue(new Callback<List<Exam>>() {

                    @Override
                    public void onResponse(Call<List<Exam>> call, Response<List<Exam>> response) {

                        binding.swipeRefresh.setRefreshing(false);

                        if (response.isSuccessful() && response.body() != null) {

                            List<Exam> newExams = response.body();

                            if (newExams.isEmpty()) {

                                binding.dateText.setText("🎉 Экзаменов нет");
                                exams.clear();
                                examAdapter.notifyDataSetChanged();
                                return;
                            }

                            binding.dateText.setText("Ближайшие экзамены");

                            exams.clear();
                            exams.addAll(newExams);
                            examAdapter.notifyDataSetChanged();

                        } else {

                            binding.dateText.setText("❌ Ошибка ответа сервера: " + response.code());

                        }
                    }

                    @Override
                    public void onFailure(Call<List<Exam>> call, Throwable t) {
                        binding.swipeRefresh.setEnabled(false);

                        binding.swipeRefresh.setRefreshing(false);

                        binding.dateText.setText("❌ Ошибка подключения");
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
        MenuItem item = binding.navigationView.getMenu().findItem(getCheckedItemId());
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