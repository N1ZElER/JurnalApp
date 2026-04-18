package com.example.jurnals.data.repository;

import com.example.jurnals.data.remote.api.ApiService;
import com.example.jurnals.domain.models.Exam;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EkzamRepository {

    private final ApiService api;

    public EkzamRepository(ApiService api) {
        this.api = api;
    }

    public interface EkzamCallback {
        void onSuccess(List<Exam> exams);
        void onUnauthorized();
        void onError(String message);
    }

    public void loadExam(String token, EkzamCallback callback) {
        if (token == null || token.isEmpty()) {
            callback.onUnauthorized();
            return;
        }

        api.getExam("Bearer " + token).enqueue(new Callback<List<Exam>>() {
            @Override
            public void onResponse(Call<List<Exam>> call, Response<List<Exam>> response) {
                if (response.code() == 401) {
                    callback.onUnauthorized();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Ошибка загрузки информации об экзамене");
                }
            }

            @Override
            public void onFailure(Call<List<Exam>> call, Throwable t) {
                callback.onError("Ошибка сети");
            }
        });
    }
}