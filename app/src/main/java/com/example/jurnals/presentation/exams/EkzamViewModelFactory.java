package com.example.jurnals.presentation.exams;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.jurnals.data.repository.EkzamRepository;

public class EkzamViewModelFactory implements ViewModelProvider.Factory {

    private final EkzamRepository repository;

    public EkzamViewModelFactory(EkzamRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(EkzamViewModel.class)) {
            return (T) new EkzamViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}