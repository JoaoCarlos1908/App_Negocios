package com.example.appnegocios.ui.configuracoes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.appnegocios.databinding.FragmentConfiguracoesBinding;

public class ConfiguracoesFragment extends Fragment {

private FragmentConfiguracoesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        ConfiguracoesViewModel configuracoesViewModel =
                new ViewModelProvider(this).get(ConfiguracoesViewModel.class);

    binding = FragmentConfiguracoesBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

        final TextView textView = binding.textConfiguracoes;
        configuracoesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}