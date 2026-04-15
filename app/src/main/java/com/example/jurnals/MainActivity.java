package com.example.jurnals;



import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.jurnals.data.remote.api.ApiService;
import com.example.jurnals.data.remote.client.RetrofitClient;
import com.example.jurnals.databinding.ActivityMainBinding;
import com.example.jurnals.presentation.ozevs.Ozevs;
import com.example.jurnals.presentation.schedule.ScheduleAdapter;
import com.example.jurnals.presentation.auth.AuthorizationActivity;
import com.example.jurnals.presentation.exams.Ekzam;
import com.example.jurnals.presentation.news.News;
import com.example.jurnals.domain.models.Lesson;
import com.example.jurnals.domain.models.Visit;
import com.example.jurnals.presentation.schedule.ScheduleRepository;
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

    private ActivityMainBinding binding;
    ApiService api;
    ScheduleAdapter adapter;
    private ScheduleRepository repository;
    String selectedDate;
    private BottomSheetBehavior<MaterialCardView> bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("token", null);

        if (token == null) {
            startActivity(new Intent(this, AuthorizationActivity.class));
            finish();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestNotificationPermission();



        setupBottomSheet();
        setupHandleClick();


        binding.dateText.setText("Сегодня");
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        api = RetrofitClient.getInstance().create(ApiService.class);

        adapter = new ScheduleAdapter();
        binding.recyclerView.setAdapter(adapter);

        repository = new ScheduleRepository(this,api);

        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        loadSchedule(selectedDate);

        binding.menu.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));

        binding.navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_shedule) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            } else if (id == R.id.nav_ekzam) {
                startActivity(new Intent(this, Ekzam.class));
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(this, News.class));
            } else if (id == R.id.nav_auth){
                Toast.makeText(this, "Вы уже авторизованы", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_performance) {
                Toast.makeText(this,"Пока в разработке", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_ozevs) {
                startActivity(new Intent(this, Ozevs.class));
            }

            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });


        binding.swipeRefresh.setOnRefreshListener(() -> loadSchedule(selectedDate));

        binding.calendarBtn.setOnClickListener(v -> {

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

                binding.dateText.setText(displayDate);

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
        binding.swipeRefresh.setEnabled(false);
        binding.swipeRefresh.setRefreshing(true);


        repository.loadSchedule(date, new ScheduleRepository.ScheduleCallback() {
            @Override
            public void onSuccess(List<Lesson> lessons) {
                binding.swipeRefresh.setRefreshing(false);
                adapter.setData(lessons);

                if (lessons == null || lessons.isEmpty()) {
                    binding.dateText.setText("Пар нет");
                }
            }

            @Override
            public void onUnauthorized() {
                binding.swipeRefresh.setRefreshing(false);
                redirectToAuth();
            }

            @Override
            public void onError(String message) {
                binding.swipeRefresh.setRefreshing(false);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                binding.dateText.setText("❌ Ошибка подключения");
            }
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

        Intent intent = new Intent(MainActivity.this, AuthorizationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    private void setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.contentCard);

        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setDraggable(true);


        bottomSheetBehavior.setFitToContents(false);


        bottomSheetBehavior.setSkipCollapsed(false);

        binding.contentCard.post(() -> {
            View parent = (View) binding.contentCard.getParent();
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
        binding.dragHandle.setOnClickListener(v -> {
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
        MenuItem item = binding.navigationView.getMenu().findItem(getCheckedItemId());
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