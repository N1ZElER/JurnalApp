package com.example.jurnals.presentation.news;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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

import com.example.jurnals.data.repository.EkzamRepository;
import com.example.jurnals.data.repository.NewRepository;
import com.example.jurnals.databinding.ActivityEkzamBinding;
import com.example.jurnals.databinding.ActivityNewsBinding;
import com.example.jurnals.presentation.exams.Ekzam;
import com.example.jurnals.data.remote.api.ApiService;
import com.example.jurnals.data.remote.client.RetrofitClient;
import com.example.jurnals.MainActivity;
import com.example.jurnals.domain.models.New;
import com.example.jurnals.R;
import com.example.jurnals.presentation.exams.EkzamViewModel;
import com.example.jurnals.presentation.exams.EkzamViewModelFactory;
import com.example.jurnals.presentation.exams.ExamAdapter;
import com.example.jurnals.presentation.ozevs.Ozevs;
import com.example.jurnals.presentation.settings.Settings;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class News extends AppCompatActivity {

    private ActivityNewsBinding binding;
    NewsAdapter newsAdapter;
    private List<New> news = new ArrayList<>();
    private NewViewModel viewModel;
    private String token;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        binding = ActivityNewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.swipeRefresh.setEnabled(false);

        SharedPreferences preferences = getSharedPreferences("auth", MODE_PRIVATE);
        token = preferences.getString("token", null);




        setupRecycler();
        setupViewModel();
        setupObservers();
        setupListeners();

        if(token != null) {
            viewModel.loadNews(token);
        } else {
            Toast.makeText(this, "Пожалуйста, авторизуйтесь", Toast.LENGTH_SHORT).show();
        }

        binding.dateText.setText("Объявления");
    }

    private void setupRecycler() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        newsAdapter = new NewsAdapter(news, new NewsAdapter.OnNewsClickListener() {
            @Override
            public void onNewsClick(New news, int position) {
                if (news.isExpanded()) {
                    news.setExpanded(false);
                    newsAdapter.updateItem(position);
                } else {
                    viewModel.loadNewsDetail(token, news, position);
                }
            }

            @Override
            public void onImageClick(Bitmap bitmap) {
                showImageDialog(bitmap);
            }
        });

        binding.recyclerView.setAdapter(newsAdapter);


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
        NewRepository repository = new NewRepository(api);
        NewViewModelFactory factory = new NewViewModelFactory(repository);

        viewModel = new ViewModelProvider(this, factory).get(NewViewModel.class);
    }

    private void setupObservers(){
        viewModel.getNews().observe(this, news -> {

            if (animationsEnabled()) {
                binding.recyclerView.scheduleLayoutAnimation();
            }

            if (news != null) {
                news.clear();
                news.addAll(news);
                newsAdapter.notifyDataSetChanged();
            }
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

        viewModel.getUpdatedPosition().observe(this, position -> {
            if (position != null) {
                newsAdapter.updateItem(position);
            }
        });
    }


    private void showImageDialog(Bitmap bitmap) {
        android.app.Dialog dialog = new android.app.Dialog(
                this,
                android.R.style.Theme_Black_NoTitleBar_Fullscreen
        );
        dialog.setContentView(R.layout.full_image);

        io.getstream.photoview.PhotoView imageView = dialog.findViewById(R.id.fullImageView);
        imageView.setImageBitmap(bitmap);
        imageView.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void setupListeners(){
        binding.menu.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));

        binding.navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_shedule) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_ekzam) {
                startActivity(new Intent(this, Ekzam.class));
            } else if (id == R.id.nav_news) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
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

        binding.settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(News.this, Settings.class);
                startActivity(intent);
            }
        });

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
            default:
                return R.id.nav_news;
        }
    }
}