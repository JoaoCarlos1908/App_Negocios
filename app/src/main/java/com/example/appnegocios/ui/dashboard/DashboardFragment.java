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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.appnegocios.R;
import com.example.appnegocios.databinding.FragmentDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView text_nomeEmpre;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Infla o layout XML do fragmento
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        // Acessa o LinearLayout dentro do layout do fragment
        LinearLayout containerInteracoes = view.findViewById(R.id.containerInteracoes);

        text_nomeEmpre = view.findViewById(R.id.text_nomeEmpre);

        DocumentReference documentReference = db.collection("Cliente").document(usuarioID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    text_nomeEmpre.setText(documentSnapshot.getString("nome"));
                }
            }
        });

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
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                navController.navigate(R.id.dashboardPlusFragment); // Use o ID correto definido no seu nav_graph
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