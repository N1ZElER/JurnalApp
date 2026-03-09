package com.example.jurnals.API;

import com.example.jurnals.Class.News;
import com.example.jurnals.Models.Auth;
import com.example.jurnals.Models.Exam;
import com.example.jurnals.Models.Lesson;
import com.example.jurnals.Models.New;
import com.example.jurnals.Models.Visit;
import com.example.jurnals.Response.LoginResponse;
import com.example.jurnals.Response.VisitResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @Headers({
            "accept-language: ru_RU, ru",
            "authorization: Bearer null",
            "origin: https://journal.top-academy.ru",
            "referer: https://journal.top-academy.ru/",
            "user-agent: Mozilla/5.0"
    })
    @POST("api/v2/auth/login")
    Call<LoginResponse> login(@Body Auth auth);


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





}