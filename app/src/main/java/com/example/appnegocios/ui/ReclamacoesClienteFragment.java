package com.example.appnegocios.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.appnegocios.R;
import com.example.appnegocios.databinding.FragmentAvaliacoesBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import Class.Reclamacoes;

public class ReclamacoesClienteFragment extends Fragment {

    private FragmentAvaliacoesBinding binding;
    private Button btnRespondidas, btnNaoRespondidas;
    private String idCliente = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reclamacoes_cliente, container, false);

        btnRespondidas = view.findViewById(R.id.btnRespodidas);
        btnNaoRespondidas = view.findViewById(R.id.btnNaoRespodidas);

        carregarReclamacoes(idCliente, view, true, true);

        btnRespondidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carregarReclamacoes(idCliente, view, false, true);
            }
        });

        btnNaoRespondidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carregarReclamacoes(idCliente, view, false, false);
            }
        });

        return view;
    }

    private void carregarReclamacoes(String idCliente, View view, Boolean exibirTudo, Boolean exibirRN) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        LinearLayout container = view.findViewById(R.id.containerReclamacoes);
        container.removeAllViews(); // Limpar antes de exibir

        db.collection("reclamacoes")
                .whereEqualTo("idCliente", idCliente)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Reclamacoes reclamacao = doc.toObject(Reclamacoes.class);
                        reclamacao.setIdReclamacao(doc.getId());

                        String idEmpreendimento = doc.getString("idEmpreendimento");

                        // Busca o nome do cliete se não for anônimo
                        if (!reclamacao.isAnonima()) {
                            db.collection("Cliente")
                                    .document(idEmpreendimento)
                                    .get()
                                    .addOnSuccessListener(userDoc -> {
                                        String nome = userDoc.getString("nome");
                                        if (nome != null) {
                                            reclamacao.setNomeAvaliador(nome);
                                        }
                                        exibirReclamacao(container, reclamacao, view, exibirTudo, exibirRN);
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    //Toast.makeText(this, "Erro ao carregar avaliações", Toast.LENGTH_SHORT).show();
                });
    }

    private void exibirReclamacao(LinearLayout container, Reclamacoes reclamacao, View view, Boolean exibirTudo, Boolean exibirRN) {
        View item = LayoutInflater.from(view.getContext()).inflate(R.layout.layout_view_reclamacoes, container, false);

        TextView tvNome = item.findViewById(R.id.tvNome);
        TextView tvComentario = item.findViewById(R.id.tvComentario);
        TextView tvResposta = item.findViewById(R.id.tvResposta);
        TextView tvRespostatext = item.findViewById(R.id.tvRespostatext);

        tvNome.setText("Para: " + reclamacao.getNomeAvaliador());
        tvComentario.setText(reclamacao.getDescricao());

        tvResposta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reclamacao.getResposta() != null && !reclamacao.getResposta().isEmpty()) {
                    tvRespostatext.setText(reclamacao.getResposta());
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

        if(exibirTudo){
            container.addView(item);
        } else {
            if (reclamacao.getRespondida() == exibirRN) {
                container.addView(item);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}