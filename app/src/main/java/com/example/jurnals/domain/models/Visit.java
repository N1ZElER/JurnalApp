package com.example.jurnals.domain.models;

public class Visit {
    private String date_visit;
    private int lesson_number;
    private Integer status_was;
    private Integer class_work_mark, home_work_mark, lab_work_mark;

    public String getDateVisit() {
        return date_visit;
    }

    public int getLessonNumber() {
        return lesson_number;
    }

    public Integer getStatusWas() {
        return status_was;
    }

    public Integer getClassWorkMark() {
        return class_work_mark;
    }
    public Integer getHomeWorkMark(){
        return home_work_mark;
    }
    public Integer getLabWorkMark(){
        return lab_work_mark;
    }
}