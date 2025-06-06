package com.example.appnegocios.ui.reclamacoes;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.example.appnegocios.R;
import com.example.appnegocios.databinding.FragmentAvaliacoesBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import Class.Reclamacoes;

public class ReclamacoesFragment extends Fragment {

    private FragmentAvaliacoesBinding binding;
    private Button btnRespondidas, btnNaoRespondidas;
    private String idEmpreendimento = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reclamacoes, container, false);

        btnRespondidas = view.findViewById(R.id.btnRespodidas);
        btnNaoRespondidas = view.findViewById(R.id.btnNaoRespodidas);

        carregarReclamacoes(idEmpreendimento, view, true, true);

        btnRespondidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carregarReclamacoes(idEmpreendimento, view, false, true);
            }
        });

        btnNaoRespondidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carregarReclamacoes(idEmpreendimento, view, false, false);
            }
        });

        return view;
    }

    private void carregarReclamacoes(String idEmpreendimento, View view, Boolean exibirTudo, Boolean exibirRN) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        LinearLayout container = view.findViewById(R.id.containerReclamacoes);
        container.removeAllViews(); // Limpar antes de exibir

        db.collection("Reclamacoes")
                .whereEqualTo("idEmpreendimento", idEmpreendimento)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Reclamacoes reclamacao = doc.toObject(Reclamacoes.class);
                        reclamacao.setIdReclamacao(doc.getId());

                        String idCliente = doc.getString("idCliente");
                        reclamacao.setReclamacao(doc.getString("reclamacao"));

                            db.collection("Cliente")
                                    .document(idCliente)
                                    .get()
                                    .addOnSuccessListener(userDoc -> {
                                        String nome = userDoc.getString("nome");

                                        reclamacao.setNomeAvaliador(nome);

                                        exibirReclamacao(container, reclamacao, view, exibirTudo, exibirRN);
                                    });
                    }
                })
                .addOnFailureListener(e -> {
                    //Toast.makeText(this, "Erro ao carregar avaliações", Toast.LENGTH_SHORT).show();
                });
    }

    private void exibirReclamacao(LinearLayout container, Reclamacoes reclamacao, View view, Boolean exibirTudo, Boolean exibirRN) {
        View item = LayoutInflater.from(view.getContext()).inflate(R.layout.layout_view_reclamacoes, container, false);

        //Acessar componentes
        TextView tvNome = item.findViewById(R.id.tvNome);
        EditText edit_reclamacao = item.findViewById(R.id.edit_reclamacao);
        TextView tvResposta = item.findViewById(R.id.tvResposta);
        EditText tvRespostatext = item.findViewById(R.id.tvRespostatext);
        LinearLayout llbotoes = item.findViewById(R.id.llbotoes);
        Button btnSalvar = item.findViewById(R.id.btnSalvar);
        Button btnCancear = item.findViewById(R.id.btnCancelar);

        //Editar conteudo componentes
        if(reclamacao.isAnonima()){
            tvNome.setText("Por: Anônimo");
        }else{
            tvNome.setText("Por: " + reclamacao.getNomeAvaliador());
        }

        edit_reclamacao.setText(reclamacao.getReclamacao());
        edit_reclamacao.setEnabled(false);
        edit_reclamacao.setVisibility(View.VISIBLE);

        if (reclamacao.getRespondida()){
            tvResposta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tvRespostatext.getVisibility() == View.VISIBLE) {
                        tvRespostatext.setVisibility(View.GONE);
                    } else {
                        tvRespostatext.setText(reclamacao.getResposta());
                        tvRespostatext.setVisibility(View.VISIBLE);
                        tvRespostatext.setFocusable(false);
                        tvRespostatext.setClickable(false);
                        tvRespostatext.setCursorVisible(false);
                        tvRespostatext.setKeyListener(null);
                    }
                }
            });

        }else{
            tvResposta.setText("Responder reclamação");
            tvResposta.setTextColor(Color.RED);

            tvResposta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvRespostatext.setVisibility(View.VISIBLE);
                    llbotoes.setVisibility(View.VISIBLE);
                    ConstraintLayout constraintLayout = item.findViewById(R.id.constraintLayout);
                    ConstraintSet constraintSet = new ConstraintSet();

                    constraintSet.clone(constraintLayout);

// Altera apenas o constraint TOP (deixa os outros intactos)
                    constraintSet.connect(
                            R.id.llbotoes,                          // ID do componente que será movido
                            ConstraintSet.TOP,                             // Lado a ser conectado
                            R.id.tvRespostatext,                            // Novo componente de referência
                            ConstraintSet.BOTTOM);

// Aplica as mudanças
                    constraintSet.applyTo(constraintLayout);

                }
            });
        }

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String resposta = tvRespostatext.getText().toString().trim();
                if (resposta.length() < 10) {
                    Toast.makeText(getContext(), "A resposta deve ter pelo menos 10 caracteres.", Toast.LENGTH_SHORT).show();
                } else {
                    atualizarRespostaReclamacao(reclamacao.getIdReclamacao(), resposta);
                    carregarReclamacoes(idEmpreendimento, view, true, true);
                }
            }
        });


        btnCancear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvRespostatext.setVisibility(View.GONE);
                llbotoes.setVisibility(View.GONE);
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

    private void atualizarRespostaReclamacao(String idReclamacao, String novaResposta) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Cria o mapa com a atualização
        Map<String, Object> atualizacao = new HashMap<>();
        atualizacao.put("resposta", novaResposta);
        atualizacao.put("respondida", true); // opcional, se quiser marcar como respondida

        // Atualiza o documento na coleção "Reclamacoes"
        db.collection("Reclamacoes")
                .document(idReclamacao)
                .update(atualizacao)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Resposta atualizada com sucesso!");
                    Toast.makeText(getContext(), "Resposta salva com sucesso!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Erro ao atualizar resposta", e);
                    Toast.makeText(getContext(), "Erro ao salvar resposta", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}