package com.example.appnegocios.ui.produtos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.appnegocios.databinding.FragmentProdutosBinding;

public class ProdutosFragment extends Fragment {

private FragmentProdutosBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        ProdutosViewModel produtosViewModel =
                new ViewModelProvider(this).get(ProdutosViewModel.class);

    binding = FragmentProdutosBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

        final TextView textView = binding.textProfutos;
        produtosViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}