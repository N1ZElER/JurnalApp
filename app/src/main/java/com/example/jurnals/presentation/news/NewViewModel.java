package com.example.jurnals.presentation.news;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jurnals.data.repository.NewRepository;
import com.example.jurnals.domain.models.New;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewViewModel extends ViewModel {

    private final NewRepository repository;

    private final MutableLiveData<List<New>> news = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> unauthorized = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> updatedPosition = new MutableLiveData<>();

    public NewViewModel(NewRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<New>> getNews() {
        return news;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getUnauthorized() {
        return unauthorized;
    }

    public LiveData<Integer> getUpdatedPosition() {
        return updatedPosition;
    }

    public void loadNews(String token) {
        loading.setValue(true);

        repository.loadNews(token, new NewRepository.NewCallback() {
            @Override
            public void onSuccess(List<New> newsList) {
                loading.postValue(false);
                news.postValue(newsList);
            }

            @Override
            public void onUnauthorized() {
                loading.postValue(false);
                unauthorized.postValue(true);
            }

            @Override
            public void onError(String message) {
                loading.postValue(false);
                error.postValue(message);
            }
        });
    }

    public void loadNewsDetail(String token, New newsItem, int position) {
        repository.loadNewsDetail(token, newsItem.getId_bbs(), new NewRepository.NewsDetailCallback() {
            @Override
            public void onSuccess(String html) {
                String htmlWithoutImg = removeImgTags(html);
                Bitmap bitmap = extractBase64Image(html);

                newsItem.setFullText(
                        htmlWithoutImg == null || htmlWithoutImg.trim().isEmpty()
                                ? " "
                                : htmlWithoutImg
                );
                newsItem.setImageBitmap(bitmap);
                newsItem.setExpanded(true);

                updatedPosition.postValue(position);
            }

            @Override
            public void onUnauthorized() {
                unauthorized.postValue(true);
            }

            @Override
            public void onError(String message) {
                error.postValue(message);
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
}