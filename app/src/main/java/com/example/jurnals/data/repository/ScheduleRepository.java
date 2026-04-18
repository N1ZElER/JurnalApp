package com.example.jurnals.data.repository;

import com.example.jurnals.data.remote.api.ApiService;
import com.example.jurnals.domain.models.Lesson;
import com.example.jurnals.domain.models.Visit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleRepository {

    private final ApiService api;

    public ScheduleRepository(ApiService api) {
        this.api = api;
    }

    public interface ScheduleCallback {
        void onSuccess(List<Lesson> lessons);
        void onUnauthorized();
        void onError(String message);
    }

    public void loadSchedule(String token, String date, ScheduleCallback callback) {
        if (token == null || token.isEmpty()) {
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
                    List<Lesson> lessons = response.body();

                    if (lessons.isEmpty()) {
                        callback.onSuccess(lessons);
                        return;
                    }

                    loadVisits(token, date, lessons, callback);
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

    private void loadVisits(String token, String selectedDate, List<Lesson> lessons, ScheduleCallback callback) {
        api.getVisits("Bearer " + token).enqueue(new Callback<List<Visit>>() {
            @Override
            public void onResponse(Call<List<Visit>> call, Response<List<Visit>> response) {
                if (response.code() == 401) {
                    callback.onUnauthorized();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    List<Visit> visits = response.body();

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