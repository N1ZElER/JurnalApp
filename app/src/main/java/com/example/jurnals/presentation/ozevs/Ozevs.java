package com.example.jurnals.presentation.ozevs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.jurnals.data.remote.api.ApiService;
import com.example.jurnals.data.remote.client.RetrofitClient;
import com.example.jurnals.data.repository.EkzamRepository;
import com.example.jurnals.data.repository.OzevRepository;
import com.example.jurnals.databinding.ActivityAutarizationBinding;
import com.example.jurnals.databinding.ActivityNewsBinding;
import com.example.jurnals.databinding.ActivityOzevsBinding;
import com.example.jurnals.domain.models.Ozev;
import com.example.jurnals.presentation.exams.Ekzam;
import com.example.jurnals.MainActivity;
import com.example.jurnals.R;
import com.example.jurnals.presentation.exams.EkzamViewModel;
import com.example.jurnals.presentation.exams.EkzamViewModelFactory;
import com.example.jurnals.presentation.exams.ExamAdapter;
import com.example.jurnals.presentation.news.News;
import com.example.jurnals.presentation.settings.Settings;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ozevs extends AppCompatActivity {

    private ActivityOzevsBinding binding;
    OzevsAdapter ozevsAdapter;
    private List<Ozev> ozevs = new ArrayList<>();
    private OzevViewModel viewModel;
    private String token;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOzevsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.swipeRefresh.setEnabled(false);

        SharedPreferences preferences = getSharedPreferences("auth", MODE_PRIVATE);
        token = preferences.getString("token", null);

        setupRecycler();
        setupViewModel();
        setupObservers();
        setupListeners();

        loadOzev();

        if(token != null) {
            viewModel.loadOzevs(token);
        } else {
            Toast.makeText(this, "Пожалуйста, перезайдите в приложение", Toast.LENGTH_SHORT).show();
        }

        binding.settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Ozevs.this, Settings.class);
                startActivity(intent);
            }
        });

    }


    private void setupRecycler() {
        binding.recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        ozevsAdapter = new OzevsAdapter(ozevs);
        binding.recyclerView.setAdapter(ozevsAdapter);


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
        OzevRepository repository = new OzevRepository(api);
        OzevViewModelFactory factory = new OzevViewModelFactory(repository);

        viewModel = new ViewModelProvider(this, factory).get(OzevViewModel.class);
    }

    private void setupObservers() {
        viewModel.getOzevs().observe(this, ozevList -> {

            if (animationsEnabled()) {
                binding.recyclerView.scheduleLayoutAnimation();
            }

            if (ozevList != null) {
                ozevs.clear();
                ozevs.addAll(ozevList);
                ozevsAdapter.notifyDataSetChanged();
            }
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

    private void setupListeners(){
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
        binding.swipeRefresh.setOnRefreshListener(this::loadOzev);
    }

    private void loadOzev() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("token", null);
        viewModel.loadOzevs(token);
    }

    private boolean animationsEnabled() {

        SharedPreferences prefs =
                getSharedPreferences("settings", MODE_PRIVATE);

        return prefs.getBoolean("animations", true);
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
            case "OzevsActivity":
                return R.id.nav_ozevs;
            default:
                return R.id.nav_ozevs;
        }
    }
}