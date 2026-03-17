package com.example.jurnals.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jurnals.API.ApiService;
import com.example.jurnals.Models.New;
import com.example.jurnals.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private final List<New> newsList;
    private final ApiService apiService;
    private final String token;

    public NewsAdapter(List<New> newsList, ApiService apiService, String token) {
        this.newsList = newsList;
        this.apiService = apiService;
        this.token = token;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        New news = newsList.get(position);

        holder.spec.setText(news.getTheme());
        holder.date.setText(news.getTime());

        if (news.isExpanded()) {
            holder.detailTextView.setVisibility(View.VISIBLE);
            holder.detailTextView.setText(news.getFullText());
        } else {
            holder.detailTextView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (news.isExpanded()) {
                news.setExpanded(false);
                notifyItemChanged(position);
                return;
            }

            loadNewsDetails(news,position);
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    private void loadNewsDetails(New news, int position) {
        apiService.getNewsDetail("Bearer " + token, news.getId_bbs())
                .enqueue(new Callback<New>() {
                    @Override
                    public void onResponse(Call<New> call, Response<New> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            String html = response.body().getText_bbs();

                            String cleanText = android.text.Html.fromHtml(
                                    html,
                                    android.text.Html.FROM_HTML_MODE_LEGACY
                            ).toString();

                            news.setFullText(cleanText);
                            news.setExpanded(true);

                            notifyItemChanged(position);
                        }
                    }

                    @Override
                    public void onFailure(Call<New> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
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