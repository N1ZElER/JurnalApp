package com.example.jurnals.presentation.ozevs;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.jurnals.data.repository.OzevRepository;


public class OzevViewModelFactory implements ViewModelProvider.Factory {

    private final OzevRepository repository;

    public OzevViewModelFactory(OzevRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(OzevViewModel.class)) {
            return (T) new OzevViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
