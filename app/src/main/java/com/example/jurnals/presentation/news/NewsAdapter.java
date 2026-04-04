package com.example.jurnals.presentation.news;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jurnals.data.remote.api.ApiService;
import com.example.jurnals.domain.models.New;
import com.example.jurnals.R;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.getstream.photoview.PhotoView;
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
            if (news.isExpanded()) {
                news.setExpanded(false);
                notifyItemChanged(position);
                return;
            }

            loadNewsDetails(news, position);
        });


        holder.detailImageView.setOnClickListener(v -> {
            if (news.getImageBitmap() != null) {
                showImageDialog(v, news.getImageBitmap());
                holder.detailImageView.setVisibility(View.VISIBLE);
            }
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

                            String htmlWithoutImg = removeImgTags(html);
                            Bitmap bitmap = extractBase64Image(html);

                            news.setFullText(
                                    htmlWithoutImg == null || htmlWithoutImg.trim().isEmpty()
                                            ? " "
                                            : htmlWithoutImg
                            );
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

    //Load photo
    private String removeImgTags(String html) {
        if (html == null) return "";
        return html.replaceAll("(?i)<img[^>]*>", "");
    }

    //Load photo
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


    // On All Screen
    private void showImageDialog(View view, Bitmap bitmap) {
        android.app.Dialog dialog = new android.app.Dialog(view.getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.full_image);

        PhotoView imageView;
        imageView = dialog.findViewById(R.id.fullImageView);

        imageView.setImageBitmap(bitmap);

        imageView.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView date, spec, detailTextView;
        ImageView detailImageView, fullImageView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.examDate);
            spec = itemView.findViewById(R.id.examSpec);
            detailTextView = itemView.findViewById(R.id.detailTextView);
            fullImageView = itemView.findViewById(R.id.fullImageView);
            detailImageView = itemView.findViewById(R.id.detailImageView);
        }
    }
}