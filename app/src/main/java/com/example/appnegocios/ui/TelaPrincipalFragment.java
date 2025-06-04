package com.example.appnegocios.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnegocios.R;
import com.example.appnegocios.databinding.FragmentAvaliacoesBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Class.Empreendimento;

public class TelaPrincipalFragment extends Fragment {

    private FragmentAvaliacoesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tela_principal, container, false);

        carregarEmpreendimentos(new FirebaseCallback() {
            @Override
            public void onCallback(List<Empreendimento> lista) {
                exibirEmpreendimentosAleatorios(lista, 10, view); // aqui você limita e exibe como quiser
            }
        });


        return view;
    }

    public interface FirebaseCallback {
        void onCallback(List<Empreendimento> listaEmpreendimentos);
    }

    private void carregarEmpreendimentos(FirebaseCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Empreendimento> listaEmpreendimentos = new ArrayList<>();

        db.collection("Cliente")
                .whereEqualTo("TipoConta", true) // Apenas empreendimentos
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Empreendimento empreendimento = doc.toObject(Empreendimento.class);
                        String idDocumento = doc.getId(); //pega id do documento
                        empreendimento.setIdUser(idDocumento);
                        listaEmpreendimentos.add(empreendimento);
                    }
                    callback.onCallback(listaEmpreendimentos);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Erro ao buscar empreendimentos", e);
                    callback.onCallback(new ArrayList<>()); // Retorna lista vazia em caso de erro
                });
    }


    private void exibirEmpreendimentosAleatorios(List<Empreendimento> listaOriginal, int limite, View view) {
        // Embaralha e limita
        Collections.shuffle(listaOriginal);
        List<Empreendimento> listaLimitada = listaOriginal.subList(0, Math.min(limite, listaOriginal.size()));

        LinearLayout horizontalContainer = view.findViewById(R.id.linearHorizontalContainer);
        LinearLayout verticalContainer = view.findViewById(R.id.linearVerticalContainer);

        horizontalContainer.removeAllViews();
        verticalContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(view.getContext());

        for (Empreendimento emp : listaLimitada) {
            // Inflate item horizontal
            View itemHorizontal = inflater.inflate(R.layout.layout_view_negocio1, horizontalContainer, false);
            ((TextView) itemHorizontal.findViewById(R.id.text_nome)).setText(emp.getNome());
            ((TextView) itemHorizontal.findViewById(R.id.text_categoria)).setText(emp.getCategoria());
            // TODO: Defina imagem se houver

            //Função de Click visualizar perfil empreendimento.
            itemHorizontal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("idEmpreendimento", emp.getIdUser()); // Substitua pelo ID real do empreendimento

                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                    navController.navigate(R.id.nav_view_perfil_empreedimento, bundle);

                }
            });

            horizontalContainer.addView(itemHorizontal);

            // Inflate item vertical
            View itemVertical = inflater.inflate(R.layout.layout_view_negocio2, verticalContainer, false);
            ((TextView) itemVertical.findViewById(R.id.text_nome)).setText(emp.getNome());
            ((TextView) itemVertical.findViewById(R.id.text_endereco)).setText(emp.getEndereco());
            ((RatingBar) itemVertical.findViewById(R.id.estrelas)).setRating(4.0f); // exemplo fixo
            // TODO: Defina imagem se houver

            //Função de Click visualizar perfil empreendimento.
            itemVertical.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("idEmpreendimento", emp.getIdUser()); // Substitua pelo ID real do empreendimento

                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                    navController.navigate(R.id.nav_view_perfil_empreedimento, bundle);
                }
            });

            verticalContainer.addView(itemVertical);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}