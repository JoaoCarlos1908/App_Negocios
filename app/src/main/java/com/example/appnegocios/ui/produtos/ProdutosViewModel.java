package com.example.appnegocios.ui.produtos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProdutosViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ProdutosViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is produtos e serviços fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}