package com.example.jurnals;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.jurnals.API.ApiService;
import com.example.jurnals.Adapter.ScheduleAdapter;
import com.example.jurnals.Class.Autarization;
import com.example.jurnals.Class.Ekzam;
import com.example.jurnals.Class.News;
import com.example.jurnals.Models.Lesson;
import com.example.jurnals.Models.Visit;
import com.example.jurnals.Notification.NotificationHelper;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    ImageButton menu, settings;
    TextView dateText;
    RecyclerView recyclerView;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    SwipeRefreshLayout swipeRefresh;
    ApiService api;
    ScheduleAdapter adapter;
    List<Lesson> lessons;
    Integer lastStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestNotificationPermission();
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("token", null);

        if (token == null) {
            startActivity(new Intent(this, Autarization.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        menu = findViewById(R.id.menu);
        settings = findViewById(R.id.settings);
        dateText = findViewById(R.id.dateText);
        recyclerView = findViewById(R.id.recyclerView);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://msapi.top-academy.ru/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(ApiService.class);

        loadTodaySchedule();

        menu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_shedule) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }

            else if (id == R.id.nav_ekzam) {
                startActivity(new Intent(this, Ekzam.class));
            }

            else if (id == R.id.nav_news) {
                startActivity(new Intent(this, News.class));
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        swipeRefresh.setOnRefreshListener(this::loadTodaySchedule);

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadVisits();
                new android.os.Handler().postDelayed(this,30000);
            }
        },30000);
    }

    private void loadTodaySchedule() {

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("token", null);

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        api.getSchedule("Bearer " + token, date)
                .enqueue(new Callback<List<Lesson>>() {

                    @Override
                    public void onResponse(Call<List<Lesson>> call, Response<List<Lesson>> response) {

                        swipeRefresh.setRefreshing(false);

                        if (response.isSuccessful() && response.body() != null) {

                            lessons = response.body();

                            if (lessons.isEmpty()) {
                                dateText.setText("Сегодня пар нет");
                                return;
                            }

                            dateText.setText("Сегодня");

                            adapter = new ScheduleAdapter(lessons);
                            recyclerView.setAdapter(adapter);

                            loadVisits();

                        } else {
                            dateText.setText("Ошибка загрузки расписания");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Lesson>> call, Throwable t) {
                        swipeRefresh.setRefreshing(false);
                        Log.e("SCHEDULE", t.getMessage());
                        dateText.setText("Ошибка сети");
                    }
                });
    }

    private void loadVisits() {

        SharedPreferences authPrefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = authPrefs.getString("token", null);

        SharedPreferences visitPrefs = getSharedPreferences("visits", MODE_PRIVATE);

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        api.getVisits("Bearer " + token)
                .enqueue(new Callback<List<Visit>>() {

                    @Override
                    public void onResponse(Call<List<Visit>> call, Response<List<Visit>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            List<Visit> visits = response.body();

                            for (Lesson lesson : lessons) {

                                lesson.setStatusWas(null);

                                for (Visit visit : visits) {

                                    if (
                                            visit.getDateVisit().equals(today) &&
                                                    lesson.getLesson() == visit.getLessonNumber()
                                    ) {

                                        lesson.setStatusWas(visit.getStatusWas());

                                        String key = "lesson_" + lesson.getLesson();
                                        int savedStatus = visitPrefs.getInt(key, -1);

                                        if (savedStatus != visit.getStatusWas()) {

                                            if (visit.getStatusWas() == 1) {
                                                NotificationHelper.show(
                                                        MainActivity.this,
                                                        "Посещение",
                                                        "Ты отмечен на " + lesson.getLesson() + " паре"
                                                );
                                            } else {
                                                NotificationHelper.show(
                                                        MainActivity.this,
                                                        "Посещение",
                                                        "У тебя пропуск на " + lesson.getLesson() + " паре"
                                                );
                                            }

                                            visitPrefs.edit()
                                                    .putInt(key, visit.getStatusWas())
                                                    .apply();
                                        }

                                        break;
                                    }

                                }

                            }

                            adapter.notifyDataSetChanged();

                        }

                    }

                    @Override
                    public void onFailure(Call<List<Visit>> call, Throwable t) {
                        Log.e("VISITS", t.getMessage());
                    }
                });
    }

    private void requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1001
                );

            }

        }

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
                return R.id.nav_shedule;
        }
    }
}