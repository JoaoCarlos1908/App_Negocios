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

    private View view;
    private Button btnFiltrar;
    private String idCliente = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_avaliacoes_cliente, container, false);

        btnFiltrar = view.findViewById(R.id.btnFiltrar);

        carregarAvaliacoes();

        btnFiltrar.setOnClickListener(v -> mostrarMenuFiltrar(v));

        return view;
    }

    private void carregarAvaliacoes() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        LinearLayout container = view.findViewById(R.id.containerAvaliacoes);
        container.removeAllViews(); // Limpar antes de exibir

        // Busca apenas as avaliações feitas pelo usuário logado
        db.collection("Avaliacoes")
                .whereEqualTo("idAvaliador", idCliente) // ← Aqui é o filtro correto
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Avaliacao avaliacao = doc.toObject(Avaliacao.class);
                        avaliacao.setIdAvaliacao(doc.getId());

                        String idEmp = avaliacao.getIdEmpreendimento();
                        if (idEmp != null) {
                            db.collection("Cliente")
                                    .document(idEmp)
                                    .get()
                                    .addOnSuccessListener(empDoc -> {
                                        String nomeEmp = empDoc.getString("nome");
                                        if (nomeEmp != null) {
                                            avaliacao.setNomeEmpre(nomeEmp); // agora esse campo armazena o nome do empreendimento
                                        }
                                        exibirAvaliacao(container, avaliacao);
                                    })
                                    .addOnFailureListener(e -> {
                                        exibirAvaliacao(container, avaliacao); // mesmo que falhe, ainda mostra
                                    });
                        } else {
                            exibirAvaliacao(container, avaliacao);
                        }

                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erro ao carregar avaliações", Toast.LENGTH_SHORT).show();
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
        tvNome.setText("Empreendimento: " + avaliacao.getNomeEmpre());
        tvComentario.setText(avaliacao.getDescricao());

        tvResposta.setOnClickListener(v -> {
            String resposta = avaliacao.getResposta();

            if (resposta != null && !resposta.isEmpty()) {
                if (tvRespostatext.getVisibility() == View.VISIBLE) {
                    tvRespostatext.setVisibility(View.GONE);
                } else {
                    tvRespostatext.setText(resposta);
                    tvRespostatext.setTextColor(getResources().getColor(android.R.color.black)); // cor normal
                    tvRespostatext.setVisibility(View.VISIBLE);
                }
            } else {
                tvRespostatext.setText("Sem resposta");
                tvRespostatext.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                tvRespostatext.setVisibility(View.VISIBLE);
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

            if (id == R.id.menu_todas) {
                carregarAvaliacoes();
            } else {
                int estrelas = 0;
                if (id == R.id.menu_5_estrelas) estrelas = 5;
                else if (id == R.id.menu_4_estrelas) estrelas = 4;
                else if (id == R.id.menu_3_estrelas) estrelas = 3;
                else if (id == R.id.menu_2_estrelas) estrelas = 2;
                else if (id == R.id.menu_1_estrelas) estrelas = 1;

                carregarAvaliacoesFiltradas(estrelas);
            }

            return true;
        });

        popup.show();
    }

    private void carregarAvaliacoesFiltradas(int estrelas) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        LinearLayout container = view.findViewById(R.id.containerAvaliacoes);
        container.removeAllViews(); // Limpa as avaliações anteriores

        db.collection("Avaliacoes")
                .whereEqualTo("idAvaliador", idCliente) // ← Correção aqui
                .whereEqualTo("estrelas", estrelas)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Avaliacao avaliacao = doc.toObject(Avaliacao.class);
                        avaliacao.setIdAvaliacao(doc.getId());

                        String idEmp = avaliacao.getIdEmpreendimento();
                        if (idEmp != null) {
                            db.collection("Cliente")
                                    .document(idEmp)
                                    .get()
                                    .addOnSuccessListener(empDoc -> {
                                        String nomeEmp = empDoc.getString("nome");
                                        if (nomeEmp != null) {
                                            avaliacao.setNomeEmpre(nomeEmp); // agora esse campo armazena o nome do empreendimento
                                        }
                                        exibirAvaliacao(container, avaliacao);
                                    })
                                    .addOnFailureListener(e -> {
                                        exibirAvaliacao(container, avaliacao); // mesmo que falhe, ainda mostra
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
    }
}