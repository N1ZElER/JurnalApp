package com.example.jurnals.Class;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.jurnals.API.ApiService;
import com.example.jurnals.MainActivity;
import com.example.jurnals.R;
import com.google.android.material.navigation.NavigationView;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Performance extends AppCompatActivity {

    ImageButton menu, settings;
    TextView dateText;
    RecyclerView recyclerView;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    SwipeRefreshLayout swipeRefresh;
    ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance);

        menu = findViewById(R.id.menu);
        settings = findViewById(R.id.settings);
        dateText = findViewById(R.id.dateText);
        recyclerView = findViewById(R.id.recyclerView);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        swipeRefresh = findViewById(R.id.swipeRefresh);



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://msapi.top-academy.ru/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(ApiService.class);


        menu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_shedule) {
                startActivity(new Intent(this, MainActivity.class));
            }

            else if (id == R.id.nav_ekzam) {
                startActivity(new Intent(this, Ekzam.class));
            }

            else if (id == R.id.nav_news) {
                startActivity(new Intent(this, News.class));
            }

            else if (id == R.id.nav_auth){
                Toast.makeText(this, "Вы уже авторизованы", Toast.LENGTH_SHORT).show();
            }

            else if (id == R.id.nav_performance) {
                startActivity(new Intent(this, Performance.class));
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
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
            case "PerfomActivity":
                return R.id.nav_performance;
            default:
                return R.id.nav_performance;
        }
    }
}