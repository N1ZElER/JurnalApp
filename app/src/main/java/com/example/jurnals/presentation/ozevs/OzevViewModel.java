package com.example.jurnals.presentation.ozevs;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jurnals.data.repository.OzevRepository;
import com.example.jurnals.domain.models.Ozev;

import java.util.List;

public class OzevViewModel extends ViewModel {

    private final OzevRepository repository;

    private final MutableLiveData<List<Ozev>> ozevs = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> unauthorized = new MutableLiveData<>(false);


    public OzevViewModel(OzevRepository repository) {
        this.repository = repository;
    }

    public MutableLiveData<List<Ozev>> getOzevs() {
        return ozevs;
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public MutableLiveData<Boolean> getUnauthorized() {
        return unauthorized;
    }

    public void loadOzevs(String token) {
        loading.setValue(true);

        repository.loadOzevs(token, new OzevRepository.OzevCallback() {
            @Override
            public void onSuccess(List<Ozev> ozevList) {
                loading.postValue(false);
                ozevs.postValue(ozevList);
            }

            @Override
            public void onUnauthorized() {
                loading.postValue(false);
                unauthorized.postValue(true);
            }

            @Override
            public void onError(String errorMessage) {
                loading.postValue(false);
                error.postValue(errorMessage);
            }
        });
    }
}
