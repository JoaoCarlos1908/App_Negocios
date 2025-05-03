package com.example.appnegocios.ui.configuracoes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ConfiguracoesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ConfiguracoesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Configurações fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}