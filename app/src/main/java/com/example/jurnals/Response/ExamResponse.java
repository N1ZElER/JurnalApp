package com.example.jurnals.Response;

import com.example.jurnals.Models.Exam;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ExamResponse {

    @SerializedName("exams")
    private List<Exam> exams;

    public List<Exam> getExams() {
        return exams;
    }

}
