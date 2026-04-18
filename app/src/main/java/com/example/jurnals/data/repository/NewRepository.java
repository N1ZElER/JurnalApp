package com.example.jurnals.data.repository;

import com.example.jurnals.data.remote.api.ApiService;
import com.example.jurnals.domain.models.New;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewRepository {

    private final ApiService api;

    public NewRepository(ApiService api) {
        this.api = api;
    }

    public interface NewCallback {
        void onSuccess(List<New> news);
        void onUnauthorized();
        void onError(String message);
    }

    public interface NewsDetailCallback {
        void onSuccess(String html);
        void onUnauthorized();
        void onError(String message);
    }

    public void loadNews(String token, NewCallback callback) {
        if (token == null || token.isEmpty()) {
            callback.onUnauthorized();
            return;
        }

        api.getNews("Bearer " + token).enqueue(new Callback<List<New>>() {
            @Override
            public void onResponse(Call<List<New>> call, Response<List<New>> response) {
                if (response.code() == 401) {
                    callback.onUnauthorized();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Ошибка загрузки новостей");
                }
            }

            @Override
            public void onFailure(Call<List<New>> call, Throwable t) {
                callback.onError("Ошибка сети");
            }
        });
    }

    public void loadNewsDetail(String token, int id, NewsDetailCallback callback) {
        api.getNewsDetail("Bearer " + token, id).enqueue(new Callback<New>() {
            @Override
            public void onResponse(Call<New> call, Response<New> response) {
                if (response.code() == 401) {
                    callback.onUnauthorized();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getText_bbs());
                } else {
                    callback.onError("Ошибка загрузки деталей");
                }
            }

            @Override
            public void onFailure(Call<New> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
