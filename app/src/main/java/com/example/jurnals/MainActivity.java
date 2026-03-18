package com.example.jurnals;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.jurnals.API.ApiService;
import com.example.jurnals.Adapter.ScheduleAdapter;
import com.example.jurnals.Class.Autarization;
import com.example.jurnals.Class.Ekzam;
import com.example.jurnals.Class.News;
import com.example.jurnals.Models.Lesson;
import com.example.jurnals.Models.Visit;
import com.example.jurnals.Notification.NotificationHelper;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    ImageButton menu, calendarBtn, settings;
//    ImageView settings;
    TextView dateText;
    RecyclerView recyclerView;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    SwipeRefreshLayout swipeRefresh;
    ApiService api;
    ScheduleAdapter adapter;
    List<Lesson> lessons;
    String selectedDate;
    View dragHandle;
    private BottomSheetBehavior<MaterialCardView> bottomSheetBehavior;
    private MaterialCardView contentCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("token", null);

        if (token == null) {
            startActivity(new Intent(this, Autarization.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        requestNotificationPermission();




        menu = findViewById(R.id.menu);
        settings = findViewById(R.id.settings);
        dateText = findViewById(R.id.dateText);
        recyclerView = findViewById(R.id.recyclerView);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        calendarBtn = findViewById(R.id.calendarBtn);
        dragHandle = findViewById(R.id.dragHandle);
        contentCard = findViewById(R.id.contentCard);

        setupBottomSheet();
        setupHandleClick();


        dateText.setText("Сегодня");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://msapi.top-academy.ru/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(ApiService.class);

        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        loadSchedule(selectedDate);

        menu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_shedule) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (id == R.id.nav_ekzam) {
                startActivity(new Intent(this, Ekzam.class));
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(this, News.class));
            } else if (id == R.id.nav_auth){
                Toast.makeText(this, "Вы уже авторизованы", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_performance) {
                Toast.makeText(this,"Пока в разработке", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Animated Assets
//        Glide.with(this)
//                .asGif()
//                .load(R.drawable.settings)
//                .into(settings);
//
//        settings.setOnClickListener(v -> {
//            Toast.makeText(this,"WORK",Toast.LENGTH_SHORT).show();
//        });

        swipeRefresh.setOnRefreshListener(() -> loadSchedule(selectedDate));

        calendarBtn.setOnClickListener(v -> {

            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder
                    .datePicker()
                    .setTitleText("Выберите дату")
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {

                Calendar selected = Calendar.getInstance();
                selected.setTimeInMillis(selection);

                Calendar today = Calendar.getInstance();
                Calendar tomorrow = Calendar.getInstance();
                tomorrow.add(Calendar.DAY_OF_YEAR, 1);

                String displayDate;

                if (isSameDay(selected, today)) {
                    displayDate = "Сегодня";
                } else if (isSameDay(selected, tomorrow)) {
                    displayDate = "Завтра";
                } else {
                    SimpleDateFormat viewFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                    displayDate = viewFormat.format(selected.getTime());
                }

                dateText.setText(displayDate);

                SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                selectedDate = apiFormat.format(selected.getTime());

                loadSchedule(selectedDate);
            });

            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        });
    }

    private boolean isSameDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    private void loadSchedule(String date) {
        swipeRefresh.setEnabled(false);

        swipeRefresh.setRefreshing(true);

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("token", null);

        if (token == null) return;

        api.getSchedule("Bearer " + token, date).enqueue(new Callback<List<Lesson>>() {

            @Override
            public void onResponse(Call<List<Lesson>> call, Response<List<Lesson>> response) {
                swipeRefresh.setRefreshing(false);

                if (response.code() == 401) {
                    redirectToAuth();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {

                    lessons = response.body();

                    if (lessons.isEmpty()) {
                        dateText.setText("Пар нет");
                    }

                    if (adapter == null) {
                        adapter = new ScheduleAdapter(lessons);
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.setData(lessons);
                        adapter.notifyDataSetChanged();
                    }

                    loadVisits();

                } else {
                    Toast.makeText(MainActivity.this, "Ошибка загрузки расписания", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Lesson>> call, Throwable t) {
                swipeRefresh.setEnabled(false);
                swipeRefresh.setRefreshing(false);
                Toast.makeText(MainActivity.this, "Ошибка сети, проверте подключение к интернету", Toast.LENGTH_SHORT).show();
                dateText.setText("❌ Ошибка подключения");
            }
        });
    }

    private void loadVisits() {

        SharedPreferences authPrefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = authPrefs.getString("token", null);

        if (token == null) {
            redirectToAuth();
            return;
        }

        SharedPreferences visitPrefs = getSharedPreferences("visits", MODE_PRIVATE);

        api.getVisits("Bearer " + token).enqueue(new Callback<List<Visit>>() {

            @Override
            public void onResponse(Call<List<Visit>> call, Response<List<Visit>> response) {

                if (response.code() == 401) {
                    redirectToAuth();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {

                    List<Visit> visits = response.body();

                    if (lessons == null) return;

                    for (Lesson lesson : lessons) {
                        lesson.setStatusWas(null);
                        lesson.setClassWorkMark(null);
                        lesson.setHomeWorkMark(null);
                        lesson.setLabWorkMark(null);

                        for (Visit visit : visits) {

                            String visitDate = visit.getDateVisit();
                            if (visitDate == null || visitDate.length() < 10) continue;
                            visitDate = visitDate.substring(0, 10);

                            if (visitDate.equals(selectedDate) && lesson.getLesson() == visit.getLessonNumber()) {

                                lesson.setStatusWas(visit.getStatusWas());
                                lesson.setClassWorkMark(visit.getClassWorkMark());
                                lesson.setHomeWorkMark(visit.getHomeWorkMark());
                                lesson.setLabWorkMark(visit.getLabWorkMark());
                                String key = selectedDate + "_lesson_" + lesson.getLesson();
                                int savedStatus = visitPrefs.getInt(key, -1);

                                if (savedStatus != visit.getStatusWas()) {

                                    String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                                    if (selectedDate.equals(today)) {
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
                                    }

                                    visitPrefs.edit()
                                            .putInt(key, visit.getStatusWas())
                                            .apply();
                                }

                                break;
                            }
                        }
                    }

                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onFailure(Call<List<Visit>> call, Throwable t) {}
        });
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1001);
            }
        }
    }

    private void redirectToAuth() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("token");
        editor.remove("tokenExpiry");
        editor.apply();

        Intent intent = new Intent(MainActivity.this, Autarization.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    private void setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(contentCard);

        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setDraggable(true);


        bottomSheetBehavior.setFitToContents(false);


        bottomSheetBehavior.setSkipCollapsed(false);

        contentCard.post(() -> {
            View parent = (View) contentCard.getParent();
            int parentHeight = parent.getHeight();

            int peekHeight = (int) (parentHeight * 0.73f);
            bottomSheetBehavior.setPeekHeight(peekHeight, true);

            bottomSheetBehavior.setExpandedOffset(dpToPx(110));
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset < 0f) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }

    private void setupHandleClick() {
        dragHandle.setOnClickListener(v -> {
            int state = bottomSheetBehavior.getState();

            if (state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else if (state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
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