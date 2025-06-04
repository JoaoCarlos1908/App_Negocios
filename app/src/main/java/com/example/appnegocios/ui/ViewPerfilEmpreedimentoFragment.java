package com.example.appnegocios.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appnegocios.R;
import com.example.appnegocios.databinding.FragmentDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import Class.Avaliacao;
import Class.Produto;
import Class.Reclamacoes;


public class ViewPerfilEmpreedimentoFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private String idEmpreendimento;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView text_nomeEmpre, text_avaliacoes, ic_estrelaEx, text_Res;
    private Button bt_avaliar, bt_reclamar;
    private List<Produto> listaProdutos = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Infla o layout XML do fragmento
        View view = inflater.inflate(R.layout.fragment_view_perfil_empreendimento, container, false);
        // Acessa o LinearLayout dentro do layout do fragment
        LinearLayout containerInteracoes = view.findViewById(R.id.containerProdutos);

        idEmpreendimento = getArguments().getString("idEmpreendimento");

        carregarProdutosDoUsuario(view);

        text_nomeEmpre = view.findViewById(R.id.text_nomeEmpre);
        ic_estrelaEx = view.findViewById(R.id.ic_estrelaEx);
        text_Res = view.findViewById(R.id.text_Res);

        DocumentReference documentReference = db.collection("Cliente").document(idEmpreendimento);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    text_nomeEmpre.setText(documentSnapshot.getString("nome"));
                }
            }
        });

        text_avaliacoes = view.findViewById(R.id.text_avaliacoes);
        exibirTaxasAvaliacoesReclamacoes(idEmpreendimento);

        bt_avaliar = view.findViewById(R.id.bt_avaliar);
        bt_avaliar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(requireContext());
                dialog.setContentView(R.layout.layout_view_avaliacao);
                dialog.setCancelable(true); // ou false se quiser forçar interação

                // Escurece fundo e impede toque fora
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setDimAmount(0.5f); // escurece o fundo

                // Acessar e modificar componentes:
                TextView tvNome = dialog.findViewById(R.id.tvNome);
                tvNome.setVisibility(View.GONE);

                EditText edit_comentario = dialog.findViewById(R.id.edit_comentario);
                edit_comentario.setVisibility(View.VISIBLE);

                LinearLayout llbotoes = dialog.findViewById(R.id.llbotoes);
                llbotoes.setVisibility(View.VISIBLE);

                TextView tvResposta = dialog.findViewById(R.id.tvResposta);
                tvResposta.setVisibility(View.GONE);

                EditText editComentario = dialog.findViewById(R.id.edit_comentario);
                editComentario.setVisibility(View.VISIBLE);

                CheckBox cb_anonimo = dialog.findViewById(R.id.cb_anonimo);
                cb_anonimo.setVisibility(View.VISIBLE);

                // Interações
                RatingBar ratingBar = dialog.findViewById(R.id.ratingBar);
                ratingBar.setIsIndicator(false);
                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        if (fromUser) {
                            Toast.makeText(getContext(), "Você avaliou com " + (int) rating + " estrelas!", Toast.LENGTH_SHORT).show();

                            // Aqui você pode salvar essa avaliação no Firebase, por exemplo
                        }
                    }
                });

                Button btnCancelar = dialog.findViewById(R.id.btnCancelar);
                btnCancelar.setOnClickListener(view -> dialog.dismiss());

                Button btnSalvar = dialog.findViewById(R.id.btnSalvar);

                btnSalvar.setOnClickListener(view -> {
                    // Coletar dados da interface
                    int estrelas = (int) ratingBar.getRating();
                    String comentario = editComentario.getText().toString().trim();
                    String idAvaliador = FirebaseAuth.getInstance().getCurrentUser().getUid(); // ou outro ID do usuário logado
                    boolean anonima = cb_anonimo.isChecked(); // ou true, dependendo de sua lógica de anonimato

                    // Criar objeto Avaliacao
                    Avaliacao avaliacao = new Avaliacao();
                    avaliacao.setIdEmpreendimento(idEmpreendimento);
                    avaliacao.setEstrelas(estrelas);
                    avaliacao.setDescricao(comentario);
                    avaliacao.setIdAvaliador(idAvaliador);
                    avaliacao.setAnonima(anonima);
                    avaliacao.setResposta(""); // resposta ainda não existe

                    // Salvar no Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Avaliacoes")
                            .add(avaliacao)
                            .addOnSuccessListener(documentReference -> {
                                // Define o ID da avaliação no objeto e atualiza, se necessário
                                String idAvaliacao = documentReference.getId();
                                avaliacao.setIdAvaliacao(idAvaliacao);

                                documentReference.set(avaliacao) // atualiza com o ID
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "Avaliação salva com sucesso!", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getContext(), "Erro ao atualizar ID da avaliação: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Erro ao salvar avaliação: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                });

                dialog.show();
            }
        });

        bt_reclamar = view.findViewById(R.id.bt_reclamar);
        bt_reclamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(requireContext());
                dialog.setContentView(R.layout.layout_view_reclamacoes);
                dialog.setCancelable(true); // ou false se quiser forçar interação

                // Escurece fundo e impede toque fora
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setDimAmount(0.5f); // escurece o fundo

                // Acessar e modificar componentes:
                TextView tvNome = dialog.findViewById(R.id.tvNome);
                tvNome.setVisibility(View.GONE);

                EditText edit_reclamacao = dialog.findViewById(R.id.edit_reclamacao);
                edit_reclamacao.setVisibility(View.VISIBLE);

                LinearLayout llbotoes = dialog.findViewById(R.id.llbotoes);
                llbotoes.setVisibility(View.VISIBLE);

                TextView tvResposta = dialog.findViewById(R.id.tvResposta);
                tvResposta.setVisibility(View.GONE);

                EditText editReclamacao = dialog.findViewById(R.id.edit_reclamacao);
                editReclamacao.setVisibility(View.VISIBLE);

                CheckBox cb_anonimo = dialog.findViewById(R.id.cb_anonimo);
                cb_anonimo.setVisibility(View.VISIBLE);

                // Interações

                Button btnCancelar = dialog.findViewById(R.id.btnCancelar);
                btnCancelar.setOnClickListener(view -> dialog.dismiss());

                Button btnSalvar = dialog.findViewById(R.id.btnSalvar);

                btnSalvar.setOnClickListener(view -> {
                    // Coletar dados da interface
                    String comentario = editReclamacao.getText().toString().trim();
                    String idAvaliador = FirebaseAuth.getInstance().getCurrentUser().getUid(); // ou outro ID do usuário logado
                    boolean anonima = cb_anonimo.isChecked(); // ou true, dependendo de sua lógica de anonimato

                    // Criar objeto Avaliacao
                    Reclamacoes reclamacao = new Reclamacoes();
                    reclamacao.setIdEmpreendimento(idEmpreendimento);
                    reclamacao.setDescricao(comentario);
                    reclamacao.setIdCliente(idAvaliador);
                    reclamacao.setAnonimo(anonima);
                    reclamacao.setRespondida(false);
                    reclamacao.setResposta(""); // resposta ainda não existe

                    // Salvar no Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Reclamacoes")
                            .add(reclamacao)
                            .addOnSuccessListener(documentReference -> {
                                // Define o ID da avaliação no objeto e atualiza, se necessário
                                String idAvaliacao = documentReference.getId();
                                reclamacao.setIdReclamacao(idAvaliacao);

                                documentReference.set(reclamacao) // atualiza com o ID
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "Reclamação salva com sucesso!", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getContext(), "Erro ao atualizar ID da reclamação: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Erro ao salvar reclamação: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                });

                dialog.show();
            }
        });

        return view;
    }

    private void exibirTaxasAvaliacoesReclamacoes(String idEmpreendimento) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Primeiro: calcular média de avaliações
        db.collection("Avaliacoes")
                .whereEqualTo("idEmpreendimento", idEmpreendimento)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalEstrelas = 0;
                    int totalAvaliacoes = 0;

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Long estrelas = doc.getLong("estrelas");

                        if (estrelas != null) {
                            totalEstrelas += estrelas;
                            totalAvaliacoes++;
                        }
                    }

                    if (totalAvaliacoes > 0) {
                        double media = (double) totalEstrelas / totalAvaliacoes;
                        Log.d("MÉDIA", "Média de estrelas: " + media);
                        text_avaliacoes.setText(String.format("%.1f", media));
                        ic_estrelaEx.setText("Total avaliações: " + totalAvaliacoes);
                    } else {
                        Log.d("MÉDIA", "Sem avaliações para esse empreendimento.");
                        text_avaliacoes.setText("#");
                        ic_estrelaEx.setText("Sem Avaliações");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ERRO", "Erro ao buscar avaliações", e);
                });

        // Segundo: calcular porcentagem de reclamações respondidas
        db.collection("Reclamacoes")
                .whereEqualTo("idEmpreendimento", idEmpreendimento)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalReclamacoes = 0;
                    int reclamacoesRespondidas = 0;

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Boolean respondida = doc.getBoolean("respondida");

                        if (respondida != null) {
                            totalReclamacoes++;
                            if (respondida) {
                                reclamacoesRespondidas++;
                            }
                        }
                    }

                    if (totalReclamacoes > 0) {
                        double porcentagem = ((double) reclamacoesRespondidas / totalReclamacoes) * 100;
                        Log.d("RECLAMACOES", "Porcentagem respondidas: " + porcentagem + "%");
                        // Aqui você pode exibir o resultado onde quiser, exemplo:
                        text_Res.setText(String.format("%.1f%%", porcentagem));
                    }else{
                        text_Res.setText("100%");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ERRO", "Erro ao buscar reclamações", e);
                });
    }


    private void carregarProdutosDoUsuario(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Referência ao container no layout onde os produtos serão exibidos
        LinearLayout conteinerProdutos = view.findViewById(R.id.containerProdutos);

        db.collection("Cliente")
                .document(idEmpreendimento)
                .collection("produtos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaProdutos.clear();
                    conteinerProdutos.removeAllViews(); // Limpa os produtos antes de adicionar

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Produto produto = document.toObject(Produto.class);
                        produto.setIdProduto(document.getId());
                        listaProdutos.add(produto);
                        // Infla a view personalizada de item de produto
                        View viewProduto = LayoutInflater.from(getContext()).inflate(R.layout.layout_view_produtos, conteinerProdutos, false);

                        TextView tvNome = viewProduto.findViewById(R.id.tvNomeProduto);
                        TextView tvValor = viewProduto.findViewById(R.id.tvValorProduto);
                        TextView tvId = viewProduto.findViewById(R.id.idProduto);

                        tvNome.setText(produto.getTitulo());
                        tvValor.setText("R$ " + produto.getValor());
                        tvId.setText(produto.getIdProduto());

                        ImageButton btneditar = viewProduto.findViewById(R.id.btnEditar);
                        btneditar.setVisibility(View.GONE);
                        // Adiciona a view ao container
                        conteinerProdutos.addView(viewProduto);
                    }
                })
                .addOnFailureListener(e -> {
                    //Toast.makeText(getContext(), "Erro ao carregar produtos", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}