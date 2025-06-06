package com.example.appnegocios.ui;

import android.os.Bundle;
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

import com.example.appnegocios.R;
import com.example.appnegocios.databinding.FragmentAvaliacoesBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import Class.Avaliacao;

public class AvaliacoesClienteFragment extends Fragment {

    private FragmentAvaliacoesBinding binding;
    private View view;
    private Button btnFiltrar;
    private String idCliente = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_avaliacoes_cliente, container, false);

        btnFiltrar = view.findViewById(R.id.btnFiltrar);

        carregarAvaliacoes(idCliente);

        btnFiltrar.setOnClickListener(v -> mostrarMenuFiltrar(v));

        return view;
    }

    private void carregarAvaliacoes(String idCliente) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        LinearLayout container = view.findViewById(R.id.containerAvaliacoes);
        container.removeAllViews(); // Limpar antes de exibir

        db.collection("Avaliacoes")
                .whereEqualTo("idCliente", idCliente)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Avaliacao avaliacao = doc.toObject(Avaliacao.class);
                        avaliacao.setIdAvaliacao(doc.getId());


                            db.collection("Cliente")
                                    .document(idCliente)
                                    .get()
                                    .addOnSuccessListener(userDoc -> {
                                        String nome = userDoc.getString("nome");
                                        if (nome != null) {
                                            avaliacao.setNomeAvaliador(nome);
                                        }
                                        exibirAvaliacao(container, avaliacao);
                                    });

                    }
                })
                .addOnFailureListener(e -> {
                    //Toast.makeText(this, "Erro ao carregar avaliações", Toast.LENGTH_SHORT).show();
                });
    }

    private void exibirAvaliacao(LinearLayout container, Avaliacao avaliacao) {
        View item = LayoutInflater.from(view.getContext()).inflate(R.layout.layout_view_avaliacao, container, false);

        RatingBar ratingBar = item.findViewById(R.id.ratingBar);
        TextView tvNome = item.findViewById(R.id.tvNome);
        TextView tvComentario = item.findViewById(R.id.tvComentario);
        TextView tvResposta = item.findViewById(R.id.tvResposta);
        TextView tvRespostatext = item.findViewById(R.id.tvRespostatext);

        ratingBar.setRating(avaliacao.getEstrelas());
        tvNome.setText("Para: " + avaliacao.getNomeAvaliador());
        tvComentario.setText(avaliacao.getDescricao());

        tvResposta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (avaliacao.getResposta() != null && !avaliacao.getResposta().isEmpty()) {
                    tvRespostatext.setText(avaliacao.getResposta());
                    tvRespostatext.setVisibility(View.VISIBLE);
                    tvResposta.setVisibility(View.GONE);
                } else {
                    tvRespostatext.setVisibility(View.GONE);
                    tvResposta.setVisibility(View.VISIBLE);
                }
            }
        });

        tvRespostatext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvRespostatext.setVisibility(View.GONE);
                tvResposta.setVisibility(View.VISIBLE);
            }
        });

        container.addView(item);
    }

    private void mostrarMenuFiltrar(View anchor) {
        PopupMenu popup = new PopupMenu(getContext(), anchor);
        popup.getMenuInflater().inflate(R.menu.menu_filtrar_avaliacoes, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            String idCliente = getArguments() != null ? getArguments().getString("idCliente") : "";

            if (id == R.id.menu_todas) {
                carregarAvaliacoes(idCliente);
            } else {
                int estrelas = 0;
                if (id == R.id.menu_5_estrelas) estrelas = 5;
                else if (id == R.id.menu_4_estrelas) estrelas = 4;
                else if (id == R.id.menu_3_estrelas) estrelas = 3;
                else if (id == R.id.menu_2_estrelas) estrelas = 2;
                else if (id == R.id.menu_1_estrelas) estrelas = 1;

                carregarAvaliacoesFiltradas(idCliente, estrelas);
            }

            return true;
        });

        popup.show();
    }

    private void carregarAvaliacoesFiltradas(String idCliente, int estrelas) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        LinearLayout container = view.findViewById(R.id.containerAvaliacoes);
        container.removeAllViews(); // Limpa as avaliações anteriores

        db.collection("Avaliacoes")
                .whereEqualTo("idCliente", idCliente)
                .whereEqualTo("estrelas", estrelas)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Avaliacao avaliacao = doc.toObject(Avaliacao.class);
                        avaliacao.setIdAvaliacao(doc.getId());

                        String idAvaliador = doc.getString("idAvaliador");

                        if (!avaliacao.isAnonima()) {
                            db.collection("Cliente")
                                    .document(idAvaliador)
                                    .get()
                                    .addOnSuccessListener(userDoc -> {
                                        String nome = userDoc.getString("nome");
                                        if (nome != null) {
                                            avaliacao.setNomeAvaliador(nome);
                                        }
                                        exibirAvaliacao(container, avaliacao);
                                    });
                        } else {
                            exibirAvaliacao(container, avaliacao);
                        }
                    }

                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(getContext(), "Nenhuma avaliação com " + estrelas + " estrelas.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erro ao carregar avaliações filtradas", Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}