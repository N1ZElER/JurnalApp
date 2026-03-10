package com.example.jurnals.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jurnals.Models.New;
import com.example.jurnals.R;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private final List<New> newsList;

    public NewsAdapter(List<New> newsList) {
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        New news = newsList.get(position);

        holder.spec.setText(news.getTheme());
        holder.date.setText(news.getTime());
        holder.detailTextView.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView date, spec, detailTextView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.examDate);
            spec = itemView.findViewById(R.id.examSpec);
            detailTextView = itemView.findViewById(R.id.detailTextView);
        }
    }
}