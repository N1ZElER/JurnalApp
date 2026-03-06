package com.example.jurnals.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jurnals.Models.Lesson;
import com.example.jurnals.R;

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

        holder.examDate.setText(
                lesson.getSubjectName()
        );

        holder.examSpec.setText(
                lesson.getTeacherName() + "\n" +
                        lesson.getStartedAt() + " - " + lesson.getFinishedAt() + "\n" +
                        lesson.getLesson() + " пара"
        );
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView examDate;
        TextView examSpec;

        public ViewHolder(@NonNull View view) {
            super(view);

            examDate = view.findViewById(R.id.examDate);
            examSpec = view.findViewById(R.id.examSpec);
        }
    }
}