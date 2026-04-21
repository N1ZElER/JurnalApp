package com.example.jurnals.data.repository;

import com.example.jurnals.data.remote.api.ApiService;
import com.example.jurnals.domain.models.New;
import com.example.jurnals.domain.models.Ozev;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OzevRepository {
    private final ApiService api;


    public OzevRepository(ApiService api) {
        this.api = api;
    }

    public interface OzevCallback {
        void onSuccess(List<Ozev> ozevs);
        void onUnauthorized();
        void onError(String message);
    }

    public void loadOzevs(String token, OzevRepository.OzevCallback callback) {
        if (token == null || token.isEmpty()) {
            callback.onUnauthorized();
            return;
        }

        api.getOzevs("Bearer " + token).enqueue(new Callback<List<Ozev>>() {
            @Override
            public void onResponse(Call<List<Ozev>> call, Response<List<Ozev>> response) {
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
            public void onFailure(Call<List<Ozev>> call, Throwable t) {
                callback.onError("Ошибка сети");
            }
        });
    }
}
