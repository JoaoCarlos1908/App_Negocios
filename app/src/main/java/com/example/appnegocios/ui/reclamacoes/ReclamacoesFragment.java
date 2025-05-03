package com.example.appnegocios.ui.reclamacoes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.appnegocios.databinding.FragmentReclamacoesBinding;

public class ReclamacoesFragment extends Fragment {

private FragmentReclamacoesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        ReclamacoesViewModel reclamacoesViewModel =
                new ViewModelProvider(this).get(ReclamacoesViewModel.class);

    binding = FragmentReclamacoesBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

        final TextView textView = binding.textReclamacoes;
        reclamacoesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}