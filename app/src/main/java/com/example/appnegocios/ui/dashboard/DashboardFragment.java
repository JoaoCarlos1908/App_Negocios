package com.example.appnegocios.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.appnegocios.R;
import com.example.appnegocios.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Infla o layout XML do fragmento
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Acessa o LinearLayout dentro do layout do fragment
        LinearLayout containerInteracoes = view.findViewById(R.id.containerInteracoes);


        for (int x = 0; x < 8; x++) {
            // Infla o layout de item individual
            View item = getLayoutInflater().inflate(R.layout.layout_view_interacao, containerInteracoes, false);

            ImageView icCategoria = item.findViewById(R.id.icCategoria);
            TextView tvDescricao = item.findViewById(R.id.tvDescricao);
            TextView tvTempo = item.findViewById(R.id.tvTempo);

            // Adiciona o item ao LinearLayout
            containerInteracoes.addView(item);
        }

        Button btnAbrirOutroFragmento = view.findViewById(R.id.bt_detalhes);
        btnAbrirOutroFragmento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NavController navController = NavHostFragment.findNavController(DashboardFragment.this);
                navController.navigate(R.id.dashboardPlusFragment);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}