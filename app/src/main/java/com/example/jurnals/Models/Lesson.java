package com.example.jurnals.Models;

public class Lesson {

    private String teacher_name;
    private String subject_name;
    private int lesson;
    private String started_at;
    private String finished_at;
    private Integer statusWas;
    private Integer classWorkMark ,homeWorkMark, labWorkMark;

    public String getTeacherName() {
        return teacher_name;
    }

    public String getSubjectName() {
        return subject_name;
    }

    public int getLesson() {
        return lesson;
    }

    public String getStartedAt() {
        return started_at;
    }

    public String getFinishedAt() {
        return finished_at;
    }

    public Integer getStatusWas() {
        return statusWas;
    }

    public void setStatusWas(Integer statusWas) {
        this.statusWas = statusWas;
    }

    public Integer getClassWorkMark() {
        return classWorkMark;
    }

    public void setClassWorkMark(Integer classWorkMark) {
        this.classWorkMark = classWorkMark;
    }

    public Integer getHomeWorkMark() {
        return homeWorkMark;
    }

    public void setHomeWorkMark(Integer homeWorkMark) {
        this.homeWorkMark = homeWorkMark;
    }

    public Integer getLabWorkMark(){
        return labWorkMark;
    }
    public void setLabWorkMark(Integer labWorkMark){
        this.labWorkMark = labWorkMark;
    }

}