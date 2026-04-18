package com.example.jurnals.presentation.schedule;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.jurnals.data.remote.accesToken.SessionManager;
import com.example.jurnals.data.repository.ScheduleRepository;

public class MainViewModelFactory implements ViewModelProvider.Factory {

    private final ScheduleRepository repository;
    private final SessionManager sessionManager;

    public MainViewModelFactory(ScheduleRepository repository,
                                SessionManager sessionManager) {
        this.repository = repository;
        this.sessionManager = sessionManager;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(repository, sessionManager);
        }

        throw new IllegalArgumentException(
                "Unknown ViewModel class: " + modelClass.getName()
        );
    }
}