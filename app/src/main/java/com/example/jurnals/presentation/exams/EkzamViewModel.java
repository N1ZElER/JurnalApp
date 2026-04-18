package com.example.jurnals.presentation.exams;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jurnals.data.repository.EkzamRepository;
import com.example.jurnals.domain.models.Exam;

import java.util.List;

public class EkzamViewModel extends ViewModel {

    private final EkzamRepository repository;

    private final MutableLiveData<List<Exam>> exams = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> unauthorized = new MutableLiveData<>(false);

    public EkzamViewModel(EkzamRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<Exam>> getExams() {
        return exams;
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

    public void loadExam(String token) {
        loading.setValue(true);

        repository.loadExam(token, new EkzamRepository.EkzamCallback() {
            @Override
            public void onSuccess(List<Exam> examList) {
                loading.postValue(false);
                exams.postValue(examList);
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