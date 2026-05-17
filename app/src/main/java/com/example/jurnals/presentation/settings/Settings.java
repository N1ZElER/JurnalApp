package com.example.jurnals.presentation.settings;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jurnals.R;
import com.example.jurnals.databinding.ActivityMainBinding;
import com.example.jurnals.databinding.ActivitySettingsBinding;

public class Settings extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = getSharedPreferences("settings", MODE_PRIVATE);

        boolean animationsEnabled =
                prefs.getBoolean("animations", false);

        binding.animationsSwitch.setChecked(animationsEnabled);

        binding.themeSwitch.setOnClickListener(v -> {

        });

        binding.animationsSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    prefs.edit()
                            .putBoolean("animations", isChecked)
                            .apply();
                }
        );
    }
}