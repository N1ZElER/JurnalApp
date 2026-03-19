package com.example.jurnals.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.jurnals.Models.Exam;
import com.example.jurnals.R;

import java.util.List;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ExamViewHolder> {

    private List<Exam> exams;

    public ExamAdapter(List<Exam> exams) {
        this.exams = exams;
    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exam, parent, false);

        return new ExamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {

        Exam exam = exams.get(position);

        holder.date.setText("📅 " + exam.getDate());
        holder.spec.setText(exam.getSpec());

        holder.itemView.setOnClickListener(v -> {});
    }

    @Override
    public int getItemCount() {
        return exams.size();
    }

    static class ExamViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView spec;

        public ExamViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.examDate);
            spec = itemView.findViewById(R.id.examSpec);
        }
    }
}