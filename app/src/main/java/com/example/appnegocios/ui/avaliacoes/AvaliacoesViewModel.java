package com.example.appnegocios.ui.avaliacoes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AvaliacoesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AvaliacoesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is avaliações e comentarios fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}