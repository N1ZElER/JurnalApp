package com.example.jurnals.presentation.schedule;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jurnals.data.remote.accesToken.SessionManager;
import com.example.jurnals.data.repository.ScheduleRepository;
import com.example.jurnals.domain.models.Lesson;


import java.util.List;

public class MainViewModel extends ViewModel {

    private final ScheduleRepository repository;
    private final SessionManager sessionManager;

    private final MutableLiveData<List<Lesson>> lessons = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> unauthorized = new MutableLiveData<>(false);

    public MainViewModel(ScheduleRepository repository, SessionManager sessionManager) {
        this.repository = repository;
        this.sessionManager = sessionManager;
    }

    public LiveData<List<Lesson>> getLessons() {
        return lessons;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getUnauthorized() {
        return unauthorized;
    }

    public void loadSchedule(String date) {
        String token = sessionManager.getToken();

        loading.setValue(true);

        repository.loadSchedule(token, date, new ScheduleRepository.ScheduleCallback() {
            @Override
            public void onSuccess(List<Lesson> data) {
                loading.postValue(false);
                lessons.postValue(data);
            }

            @Override
            public void onUnauthorized() {
                loading.postValue(false);
                unauthorized.postValue(true);
            }

            @Override
            public void onError(String message) {
                loading.postValue(false);
                error.postValue(message);
            }
        });
    }
}