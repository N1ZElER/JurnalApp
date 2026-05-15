package com.example.jurnals.data.remote.api;

import com.example.jurnals.data.remote.backendModels.BackendLoginRequest;
import com.example.jurnals.data.remote.backendModels.BackendLoginResponse;
import com.example.jurnals.domain.models.Exam;
import com.example.jurnals.domain.models.Lesson;
import com.example.jurnals.domain.models.New;
import com.example.jurnals.domain.models.Ozev;
import com.example.jurnals.domain.models.Visit;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @Headers({
            "Accept-Language: ru_RU, ru",
            "Authorization: Bearer null",
            "Origin: https://journal.top-academy.ru",
            "Referer: https://journal.top-academy.ru/",
            "User-Agent: Mozilla/5.0"
    })
    @POST("api/v2/auth/login")
    Call<BackendLoginResponse> login(@Body Map<String, Object> body);

    @Headers({
            "accept-language: ru_RU, ru",
            "origin: https://journal.top-academy.ru",
            "referer: https://journal.top-academy.ru/",
            "user-agent: Mozilla/5.0"
    })
    @GET("api/v2/schedule/operations/get-by-date")
    Call<List<Lesson>> getSchedule(
            @Header("Authorization") String token,
            @Query("date_filter") String date
    );

    @Headers({
            "origin: https://journal.top-academy.ru",
            "referer: https://journal.top-academy.ru/",
            "accept-language: ru_RU, ru"
    })
    @GET("api/v2/dashboard/info/future-exams")
    Call<List<Exam>> getExam(
            @Header("Authorization") String token
    );
    @Headers({
            "origin: https://journal.top-academy.ru",
            "referer: https://journal.top-academy.ru/",
            "accept-language: ru_RU, ru"
    })
    @GET("api/v2/progress/operations/student-visits")
    Call<List<Visit>> getVisits(
            @Header("Authorization") String token
    );

    @Headers({
            "origin: https://journal.top-academy.ru",
            "referer: https://journal.top-academy.ru/",
            "accept-language: ru_RU, ru"
    })
    @GET("api/v2/news/operations/latest-news")
    Call<List<New>> getNews(
            @Header("Authorization") String token
    );

    @Headers({
            "origin: https://journal.top-academy.ru",
            "referer: https://journal.top-academy.ru/",
            "accept-language: ru_RU, ru",
            "user-agent: Mozilla/5.0"
    })
    @GET("api/v2/news/operations/detail-news")
    Call<New> getNewsDetail(
            @Header("Authorization") String token,
            @Query("news_id") int newsId
    );


    @Headers({
            "origin: https://journal.top-academy.ru",
            "referer: https://journal.top-academy.ru/",
            "accept-language: ru_RU, ru",
            "user-agent: Mozilla/5.0"
    })
    @GET("api/v2/reviews/index/list")
    Call<List<Ozev>> getOzevs
            (@Header("Authorization") String token);
}