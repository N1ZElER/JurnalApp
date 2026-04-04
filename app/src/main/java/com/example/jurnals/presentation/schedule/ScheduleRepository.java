package com.example.jurnals.presentation.schedule;

import static android.content.Context.MODE_PRIVATE;

import static androidx.core.content.ContextCompat.startActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.jurnals.MainActivity;
import com.example.jurnals.data.remote.api.ApiService;
import com.example.jurnals.data.remote.api.MyServerApi;
import com.example.jurnals.data.remote.client.RetrofitClient;
import com.example.jurnals.domain.models.Lesson;
import com.example.jurnals.domain.models.Visit;
import com.example.jurnals.presentation.auth.Autarization;

import org.jetbrains.annotations.Async;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleRepository {

    private final Context context;
    private final ApiService api;
    private List<Lesson> lessons;
    private String selectedDate;


    public ScheduleRepository(Context context, ApiService api) {
        this.context = context;
        this.api = api;
    }

    public interface ScheduleCallback {
        void onSuccess(List<Lesson> lessons);
        void onUnauthorized();
        void onError(String message);
    }


    public void loadSchedule(String date, ScheduleCallback callback) {
        this.selectedDate = date;

        SharedPreferences prefs = context.getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("token", null);

        if (token == null) {
            callback.onUnauthorized();
            return;
        }

        api.getSchedule("Bearer " + token, date).enqueue(new Callback<List<Lesson>>() {
            @Override
            public void onResponse(Call<List<Lesson>> call, Response<List<Lesson>> response) {
                if (response.code() == 401) {
                    callback.onUnauthorized();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    lessons = response.body();

                    if (lessons.isEmpty()) {
                        callback.onSuccess(lessons);
                        return;
                    }
                    loadVisits(callback);

                } else {
                    callback.onError("Ошибка загрузки расписания");
                }

            }

            @Override
            public void onFailure(Call<List<Lesson>> call, Throwable t) {
                callback.onError("Ошибка сети");
            }
        });
    }


    private void loadVisits(ScheduleCallback callback) {
        SharedPreferences authPrefs = context.getSharedPreferences("auth", MODE_PRIVATE);
        String token = authPrefs.getString("token", null);

        if (token == null) {
            callback.onUnauthorized();
            return;
        }

        api.getVisits("Bearer " + token).enqueue(new Callback<List<Visit>>() {
            @Override
            public void onResponse(Call<List<Visit>> call, Response<List<Visit>> response) {
                if (response.code() == 401) {
                    callback.onUnauthorized();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    List<Visit> visits = response.body();

                    if (lessons == null) {
                        callback.onError("Занятия не загружены");
                        return;
                    }

                    Map<String, Visit> visitMap = new HashMap<>();

                    for (Visit visit : visits) {
                        String visitDate = visit.getDateVisit();
                        if (visitDate == null || visitDate.length() < 10) continue;

                        visitDate = visitDate.substring(0, 10);
                        String key = visitDate + "_" + visit.getLessonNumber();
                        visitMap.put(key, visit);
                    }

                    for (Lesson lesson : lessons) {
                        lesson.setStatusWas(null);
                        lesson.setClassWorkMark(null);
                        lesson.setHomeWorkMark(null);
                        lesson.setLabWorkMark(null);

                        String key = selectedDate + "_" + lesson.getLesson();
                        Visit visit = visitMap.get(key);

                        if (visit != null) {
                            lesson.setStatusWas(visit.getStatusWas());
                            lesson.setClassWorkMark(visit.getClassWorkMark());
                            lesson.setHomeWorkMark(visit.getHomeWorkMark());
                            lesson.setLabWorkMark(visit.getLabWorkMark());
                        }
                    }

                    callback.onSuccess(lessons);
                } else {
                    callback.onError("Ошибка загрузки посещаемости");
                }
            }

            @Override
            public void onFailure(Call<List<Visit>> call, Throwable t) {
                callback.onError("Ошибка сети при загрузке посещаемости");
            }
        });
    }
}
