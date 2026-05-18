package com.example.jurnals.presentation.exams;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.jurnals.MainActivity;
import com.example.jurnals.R;
import com.example.jurnals.data.remote.api.ApiService;
import com.example.jurnals.data.remote.client.RetrofitClient;
import com.example.jurnals.data.repository.EkzamRepository;
import com.example.jurnals.databinding.ActivityEkzamBinding;
import com.example.jurnals.domain.models.Exam;
import com.example.jurnals.presentation.news.News;
import com.example.jurnals.presentation.ozevs.Ozevs;
import com.example.jurnals.presentation.settings.Settings;

import java.util.ArrayList;
import java.util.List;

public class Ekzam extends AppCompatActivity {

    private ActivityEkzamBinding binding;
    private final List<Exam> exams = new ArrayList<>();
    private ExamAdapter examAdapter;
    private EkzamViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEkzamBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.swipeRefresh.setEnabled(false);

        setupRecycler();
        setupViewModel();
        setupObservers();
        setupListeners();

        loadExam();
    }

    private void setupRecycler() {
        binding.recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        examAdapter = new ExamAdapter(exams);
        binding.recyclerView.setAdapter(examAdapter);


        binding.recyclerView.setItemAnimator(null);

        binding.recyclerView.setLayoutAnimation(
                AnimationUtils.loadLayoutAnimation(
                        this,
                        R.anim.layout_anim
                )
        );
    }

    private void setupViewModel() {
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        EkzamRepository repository = new EkzamRepository(api);
        EkzamViewModelFactory factory = new EkzamViewModelFactory(repository);

        viewModel = new ViewModelProvider(this, factory).get(EkzamViewModel.class);
    }

    private void setupObservers() {
        viewModel.getExams().observe(this, examList -> {

            if (animationsEnabled()) {
                binding.recyclerView.scheduleLayoutAnimation();
            }

            if (examList == null || examList.isEmpty()) {
                binding.dateText.setText("🎉 Экзаменов нет");
                exams.clear();
                examAdapter.notifyDataSetChanged();
                return;
            }

            binding.dateText.setText("Ближайшие экзамены");
            exams.clear();
            exams.addAll(examList);
            examAdapter.notifyDataSetChanged();
        });

        viewModel.getLoading().observe(this, isLoading -> {

            binding.swipeRefresh.setEnabled(false);
        });

        viewModel.getError().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                binding.dateText.setText("❌ " + message);
            }
        });

        viewModel.getUnauthorized().observe(this, unauthorized -> {
            if (Boolean.TRUE.equals(unauthorized)) {
                Toast.makeText(this, "Сессия истекла", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        binding.menu.setOnClickListener(v -> {
            if (binding.drawerLayout != null) {
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        binding.navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_shedule) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_ekzam) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(this, News.class));
            } else if (id == R.id.nav_auth) {
                Toast.makeText(this, "Вы уже авторизованы", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_performance) {
                Toast.makeText(this, "Пока в разработке", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_ozevs) {
                startActivity(new Intent(this, Ozevs.class));
            }

            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        binding.swipeRefresh.setOnRefreshListener(this::loadExam);


        binding.settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Ekzam.this, Settings.class);
                startActivity(intent);
            }
        });
    }

    private void loadExam() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("token", null);
        viewModel.loadExam(token);
    }

    private boolean animationsEnabled() {

        SharedPreferences prefs =
                getSharedPreferences("settings", MODE_PRIVATE);

        return prefs.getBoolean("animations", false);
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