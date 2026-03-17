package com.example.jurnals.Adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jurnals.API.ApiService;
import com.example.jurnals.Models.New;
import com.example.jurnals.R;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            holder.detailTextView.setText(news.getFullText() == null ? "" : news.getFullText());

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
            if (news.isExpanded()) {
                news.setExpanded(false);
                notifyItemChanged(position);
                return;
            }

            loadNewsDetails(news, position);
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

                            String textOnly = Html.fromHtml(
                                    removeImgTags(html),
                                    Html.FROM_HTML_MODE_LEGACY
                            ).toString().trim();

                            Bitmap bitmap = extractBase64Image(html);

                            news.setFullText(textOnly.isEmpty() ? " " : textOnly);
                            news.setImageBitmap(bitmap);
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

    private String removeImgTags(String html) {
        if (html == null) return "";
        return html.replaceAll("(?i)<img[^>]*>", "");
    }

    private Bitmap extractBase64Image(String html) {
        if (html == null) return null;

        Pattern pattern = Pattern.compile(
                "src\\s*=\\s*\"data:image/[^;]+;base64,([^\"]+)\"",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            try {
                String base64 = matcher.group(1);
                byte[] bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
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