package com.example.jurnals.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        holder.examDate.setText(lesson.getSubjectName());

        holder.examSpec.setText(
                lesson.getTeacherName() + "\n" +
                        lesson.getStartedAt() + " - " + lesson.getFinishedAt() + "\n" +
                        lesson.getLesson() + " пара"
        );

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

                LocalTime start = LocalTime.parse(startRaw);
                LocalTime end = LocalTime.parse(endRaw);
                LocalTime now = LocalTime.now();

                if(now.isAfter(start) && now.isBefore(end)){
                    color = holder.itemView.getResources().getColor(R.color.lesson_now);
                }

            } catch (Exception ignored){}

        }

        holder.card.setStrokeColor(color);

    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView examDate;
        TextView examSpec;
        MaterialCardView card;

        public ViewHolder(@NonNull View view) {
            super(view);

            examDate = view.findViewById(R.id.examDate);
            examSpec = view.findViewById(R.id.examSpec);
            card = view.findViewById(R.id.lessonCard);
        }
    }
}