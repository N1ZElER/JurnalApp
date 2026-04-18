package com.example.jurnals.presentation.news;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.jurnals.data.repository.NewRepository;

public class NewViewModelFactory implements ViewModelProvider.Factory {

    private final NewRepository repository;

    public NewViewModelFactory(NewRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NewViewModel.class)) {
            return (T) new NewViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}