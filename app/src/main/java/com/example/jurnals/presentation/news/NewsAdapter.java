package com.example.jurnals.presentation.news;

import android.graphics.Bitmap;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jurnals.R;
import com.example.jurnals.domain.models.New;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    public interface OnNewsClickListener {
        void onNewsClick(New news, int position);
        void onImageClick(Bitmap bitmap);
    }

    private final List<New> newsList;
    private final OnNewsClickListener listener;

    public NewsAdapter(List<New> newsList, OnNewsClickListener listener) {
        this.newsList = newsList;
        this.listener = listener;
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

        holder.detailTextView.setMovementMethod(LinkMovementMethod.getInstance());
        holder.detailTextView.setLinksClickable(true);

        if (news.isExpanded()) {
            holder.detailTextView.setVisibility(View.VISIBLE);
            holder.detailTextView.setText(
                    Html.fromHtml(
                            news.getFullText() == null ? "" : news.getFullText(),
                            Html.FROM_HTML_MODE_LEGACY
                    )
            );

            if (news.getImageBitmap() != null) {
                holder.detailImageView.setVisibility(View.VISIBLE);
                holder.detailImageView.setImageBitmap(news.getImageBitmap());
            } else {
                holder.detailImageView.setVisibility(View.GONE);
                holder.detailImageView.setImageDrawable(null);
            }
        } else {
            holder.detailTextView.setVisibility(View.GONE);
            holder.detailImageView.setVisibility(View.GONE);
            holder.detailImageView.setImageDrawable(null);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNewsClick(news, position);
            }
        });

        holder.detailImageView.setOnClickListener(v -> {
            if (news.getImageBitmap() != null && listener != null) {
                listener.onImageClick(news.getImageBitmap());
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public void updateItem(int position) {
        notifyItemChanged(position);
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView date, spec, detailTextView;
        ImageView detailImageView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.examDate);
            spec = itemView.findViewById(R.id.examSpec);
            detailTextView = itemView.findViewById(R.id.detailTextView);
            detailImageView = itemView.findViewById(R.id.detailImageView);
        }
    }
}