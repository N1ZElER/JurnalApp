package com.example.jurnals.data.remote.dto.response;

import com.example.jurnals.domain.models.Exam;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ExamResponse {

    @SerializedName("exams")
    private List<Exam> exams;

    public List<Exam> getExams() {
        return exams;
    }

}
