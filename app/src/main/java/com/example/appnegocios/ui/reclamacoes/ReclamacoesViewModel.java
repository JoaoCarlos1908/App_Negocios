package com.example.appnegocios.ui.reclamacoes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReclamacoesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ReclamacoesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is reclamações fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}