package com.example.jurnals.Adapter;

import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jurnals.Models.Exam;
import com.example.jurnals.Models.Lesson;
import com.example.jurnals.R;
import com.google.android.material.card.MaterialCardView;

import java.time.LocalTime;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    List<Lesson> lessons;

    public ScheduleAdapter(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Lesson lesson = lessons.get(position);
        Integer class_mark = lesson.getClassWorkMark();
        Integer home_mark = lesson.getHomeWorkMark();
        Integer lab_mark = lesson.getLabWorkMark();

        holder.examDate.setText(lesson.getSubjectName());

        holder.examSpec.setText(
                lesson.getTeacherName() + "\n" +
                        lesson.getStartedAt() + " - " + lesson.getFinishedAt() + "\n" +
                        lesson.getLesson() + " пара"
        );



       // class mark
        if(class_mark != null && class_mark > 0){
            holder.examClassGrade.setVisibility(View.VISIBLE);
            holder.examClassGrade.setText(String.valueOf(class_mark));
            holder.examClassGrade.setTextColor(Color.DKGRAY);
        }else{
            holder.examClassGrade.setVisibility(View.GONE);
        }


        //home mark
        if(home_mark != null && home_mark > 0){
            holder.examHomeGrade.setVisibility(View.VISIBLE);
            holder.examHomeGrade.setText(String.valueOf(home_mark));
            holder.examHomeGrade.setTextColor(Color.RED);
        }else{
            holder.examHomeGrade.setVisibility(View.GONE);
        }

        // lab mark
        if(lab_mark != null && lab_mark > 0){
            holder.examLabGrade.setVisibility(View.VISIBLE);
            holder.examLabGrade.setText(String.valueOf(lab_mark));
            holder.examLabGrade.setTextColor(Color.YELLOW);
        }else{
            holder.examLabGrade.setVisibility(View.GONE);
        }


        int color;

        if (lesson.getStatusWas() != null) {

            if (lesson.getStatusWas() == 1) {
                color = holder.itemView.getResources().getColor(R.color.lesson_present);
            } else {
                color = holder.itemView.getResources().getColor(R.color.lesson_absent);
            }

        } else {

            color = holder.itemView.getResources().getColor(R.color.lesson_default);

            try {

                String startRaw = lesson.getStartedAt().substring(0,5);
                String endRaw = lesson.getFinishedAt().substring(0,5);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    LocalTime start = LocalTime.parse(startRaw);
                    LocalTime end = LocalTime.parse(endRaw);
                    LocalTime now = LocalTime.now();

//                    if(now.isAfter(start) && now.isBefore(end)){
//                        color = holder.itemView.getResources().getColor(R.color.lesson_now);
//                    }
                }

            } catch (Exception ignored){}

        }

        holder.card.setStrokeColor(color);



        holder.itemView.setOnClickListener(v -> {});
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public void setData(List<Lesson> newLessons) {
        this.lessons = newLessons;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView examDate, examClassGrade, examHomeGrade, examLabGrade;
        TextView examSpec;
        MaterialCardView card;

        public ViewHolder(@NonNull View view) {
            super(view);

            examDate = view.findViewById(R.id.examDate);
            examSpec = view.findViewById(R.id.examSpec);
            examClassGrade = view.findViewById(R.id.examClassGrade);
            card = view.findViewById(R.id.lessonCard);
            examHomeGrade = view.findViewById(R.id.examHomeGrade);
            examLabGrade = view.findViewById(R.id.examLabGrade);
        }
    }
}