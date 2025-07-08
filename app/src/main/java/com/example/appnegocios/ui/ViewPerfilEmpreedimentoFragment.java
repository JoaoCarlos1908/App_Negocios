package com.example.appnegocios.ui;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.graphics.Color;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.appnegocios.R;
import com.example.appnegocios.databinding.FragmentDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Class.Avaliacao;
import Class.Produto;
import Class.Reclamacoes;


public class ViewPerfilEmpreedimentoFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private String idEmpreendimento;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView text_nomeEmpre, text_avaliacoes, ic_estrelaEx, text_Res, text_endereco;
    private EditText edit_descricao;
    private Button bt_avaliar, bt_reclamar;
    private View maps, contatos, links, horas;
    private List<Produto> listaProdutos = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        idEmpreendimento = getArguments().getString("idEmpreendimento");

        // Infla o layout XML do fragmento
        View view = inflater.inflate(R.layout.fragment_view_perfil_empreendimento, container, false);

        iniciarComponentes(view);
        registrarVisualizacaoPerfil(idEmpreendimento);

        DocumentReference documentReference = db.collection("Cliente").document(idEmpreendimento);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    text_nomeEmpre.setText(documentSnapshot.getString("nome"));

                    String endereco = documentSnapshot.getString("endereco");
                    if (endereco != null && !endereco.trim().isEmpty()) {
                        text_endereco.setText("\uD83D\uDCCD " + endereco);
                    } else {
                        text_endereco.setText("📍 Endereço não informado");
                    }

                    edit_descricao.setText(documentSnapshot.getString("descricao"));
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

                    // --- Parte 1: Atualiza/Cria documento diário em 'dadosSemana' ---
                    String dataAtual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    DocumentReference docSemanaRef = db.collection("Cliente")
                            .document(idEmpreendimento)
                            .collection("dadosSemana")
                            .document(dataAtual);

                    docSemanaRef.get().addOnSuccessListener(documentSnapshot -> {
                        Map<String, Object> update = new HashMap<>();
                        update.put("avaliacoes", FieldValue.increment(1));

                        // Adiciona campos 'cliques' e 'avaliacoes' se ainda não existem
                        if (!documentSnapshot.exists() || !documentSnapshot.contains("cliques")) {
                            update.put("cliques", 0);
                        }
                        if (!documentSnapshot.exists() || !documentSnapshot.contains("contador")) {
                            update.put("contador", 0);
                        }

                        docSemanaRef.set(update, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> Log.d("FIREBASE", "View do dia registrada com sucesso"))
                                .addOnFailureListener(e -> Log.e("FIREBASE", "Erro ao registrar view diária", e));
                    });

                    // --- Parte 2: Excluir documentos mais antigos que 7 registros ---
                    CollectionReference viewsRef = db.collection("Cliente")
                            .document(idEmpreendimento)
                            .collection("dadosSemana");

                    viewsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
                        List<QueryDocumentSnapshot> documentos = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            documentos.add(doc);
                        }

                        if (documentos.size() <= 7) {
                            Log.d("FIREBASE", "Nada a excluir. Apenas " + documentos.size() + " dias registrados.");
                            return;
                        }

                        // Ordena por ID (datas no formato yyyy-MM-dd)
                        Collections.sort(documentos, Comparator.comparing(QueryDocumentSnapshot::getId));

                        int quantidadeParaRemover = documentos.size() - 7;
                        for (int i = 0; i < quantidadeParaRemover; i++) {
                            QueryDocumentSnapshot doc = documentos.get(i);
                            viewsRef.document(doc.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> Log.d("FIREBASE", "Documento " + doc.getId() + " excluído"))
                                    .addOnFailureListener(e -> Log.e("FIREBASE", "Erro ao excluir documento " + doc.getId(), e));
                        }
                    }).addOnFailureListener(e -> Log.e("FIREBASE", "Erro ao buscar documentos de views", e));
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

                EditText edit_comentario = dialog.findViewById(R.id.edit_comentario);
                edit_comentario.setVisibility(View.VISIBLE);

                LinearLayout llbotoes = dialog.findViewById(R.id.llbotoes);
                llbotoes.setVisibility(View.VISIBLE);

                TextView tvResposta = dialog.findViewById(R.id.tvResposta);
                tvResposta.setVisibility(View.GONE);

                TextView tvRespostatext = dialog.findViewById(R.id.tvRespostatext);
                tvRespostatext.setVisibility(View.GONE);

                CheckBox cb_anonimo = dialog.findViewById(R.id.cb_anonimo);
                cb_anonimo.setVisibility(View.VISIBLE);

                // Interações

                Button btnCancelar = dialog.findViewById(R.id.btnCancelar);
                btnCancelar.setOnClickListener(view -> dialog.dismiss());

                Button btnSalvar = dialog.findViewById(R.id.btnSalvar);

                btnSalvar.setOnClickListener(view -> {
                    // Coletar dados da interface
                    String comentario = edit_comentario.getText().toString().trim();
                    String idAvaliador = FirebaseAuth.getInstance().getCurrentUser().getUid(); // ou outro ID do usuário logado
                    boolean anonima = cb_anonimo.isChecked(); // ou true, dependendo de sua lógica de anonimato

                    // Criar objeto Avaliacao
                    Reclamacoes reclamacao = new Reclamacoes();
                    reclamacao.setIdEmpreendimento(idEmpreendimento);
                    reclamacao.setReclamacao(comentario);
                    reclamacao.setIdCliente(idAvaliador);
                    reclamacao.setAnonima(anonima);
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

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animarPreenchimento(maps);
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                Bundle bundle = new Bundle();
                                bundle.putString("idUser", idEmpreendimento);
                                bundle.putBoolean("exibir", false);

                                // Código que será executado após
                                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                                navController.navigate(R.id.mapsFragment, bundle);
                            }
                        },
                        250); // tempo de atraso em milissegundos
            }
        });

        contatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animarPreenchimento(contatos);
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                Bundle bundle = new Bundle();
                                bundle.putString("idUser", idEmpreendimento);
                                bundle.putBoolean("exibir", false);

                                // Código que será executado após
                                registrarClicksContatos(idEmpreendimento);

                                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                                navController.navigate(R.id.contatosFragment, bundle);
                            }
                        },
                        250); // tempo de atraso em milissegundos
            }
        });

        links.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animarPreenchimento(links);
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                Bundle bundle = new Bundle();
                                bundle.putString("idUser", idEmpreendimento);
                                bundle.putBoolean("exibir", false);

                                // Código que será executado após
                                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                                navController.navigate(R.id.linksFragment, bundle); // Use o ID correto definido no seu nav_graph
                            }
                        },
                        250); // tempo de atraso em milissegundos
            }
        });

        horas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animarPreenchimento(horas);
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                Bundle bundle = new Bundle();
                                bundle.putString("idUser", idEmpreendimento);
                                bundle.putBoolean("exibir", false);

                                // Código que será executado após
                                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                                navController.navigate(R.id.horariosFragment, bundle); // Use o ID correto definido no seu nav_graph
                            }
                        },
                        250); // tempo de atraso em milissegundos
            }
        });

        return view;
    }

    private void iniciarComponentes(View view) {
        carregarProdutosDoUsuario(view);

        text_nomeEmpre = view.findViewById(R.id.text_nomeEmpre);
        ic_estrelaEx = view.findViewById(R.id.ic_estrelaEx);
        text_Res = view.findViewById(R.id.text_Res);
        text_endereco = view.findViewById(R.id.text_endereco);
        edit_descricao = view.findViewById(R.id.edit_descricao);
        maps = view.findViewById(R.id.bt_localizacao);
        contatos = view.findViewById(R.id.bt_contatos);
        links = view.findViewById(R.id.bt_links);
        horas = view.findViewById(R.id.bt_relogio);
    }

    public void animarPreenchimento(View view) {
        int corInicial = Color.TRANSPARENT; // ou qualquer cor de início
        int corFinal = Color.parseColor("#a7a7a7"); // Cor final

        ObjectAnimator anim = ObjectAnimator.ofObject(
                view,
                "backgroundColor",
                new ArgbEvaluator(),
                corInicial,
                corFinal
        );
        anim.setDuration(250); // duração da animação em milissegundos
        anim.start();
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
                    } else {
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

                        viewProduto.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putString("idUser", idEmpreendimento);
                                bundle.putString("idProduto", produto.getIdProduto());
                                bundle.putBoolean("modExibir", true);

                                // Código que será executado após
                                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                                navController.navigate(R.id.addProdutos, bundle);
                            }
                        });

                        // Adiciona a view ao container
                        conteinerProdutos.addView(viewProduto);
                    }
                })
                .addOnFailureListener(e -> {
                    //Toast.makeText(getContext(), "Erro ao carregar produtos", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    private void registrarVisualizacaoPerfil(String idEmpreendimento) {
        if (idEmpreendimento == null || idEmpreendimento.trim().isEmpty()) {
            Log.e("Firestore", "ID do empreendimento inválido.");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Cliente").document(idEmpreendimento);

        // Atualiza contador geral de views
        docRef.update("views", FieldValue.increment(1))
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Visualização registrada com sucesso."))
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Erro ao registrar visualização: ", e);
                    docRef.set(Collections.singletonMap("visualizacoes", 1), SetOptions.merge());
                });

        // --- Parte 1: Atualiza/Cria documento diário em 'dadosSemana' ---
        String dataAtual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DocumentReference docSemanaRef = db.collection("Cliente")
                .document(idEmpreendimento)
                .collection("dadosSemana")
                .document(dataAtual);

        docSemanaRef.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> update = new HashMap<>();
            update.put("contador", FieldValue.increment(1));

            // Adiciona campos 'cliques' e 'avaliacoes' se ainda não existem
            if (!documentSnapshot.exists() || !documentSnapshot.contains("cliques")) {
                update.put("cliques", 0);
            }
            if (!documentSnapshot.exists() || !documentSnapshot.contains("avaliacoes")) {
                update.put("avaliacoes", 0);
            }

            docSemanaRef.set(update, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d("FIREBASE", "View do dia registrada com sucesso"))
                    .addOnFailureListener(e -> Log.e("FIREBASE", "Erro ao registrar view diária", e));
        });

        // --- Parte 2: Excluir documentos mais antigos que 7 registros ---
        CollectionReference viewsRef = db.collection("Cliente")
                .document(idEmpreendimento)
                .collection("dadosSemana");

        viewsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<QueryDocumentSnapshot> documentos = new ArrayList<>();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                documentos.add(doc);
            }

            if (documentos.size() <= 7) {
                Log.d("FIREBASE", "Nada a excluir. Apenas " + documentos.size() + " dias registrados.");
                return;
            }

            // Ordena por ID (datas no formato yyyy-MM-dd)
            Collections.sort(documentos, Comparator.comparing(QueryDocumentSnapshot::getId));

            int quantidadeParaRemover = documentos.size() - 7;
            for (int i = 0; i < quantidadeParaRemover; i++) {
                QueryDocumentSnapshot doc = documentos.get(i);
                viewsRef.document(doc.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> Log.d("FIREBASE", "Documento " + doc.getId() + " excluído"))
                        .addOnFailureListener(e -> Log.e("FIREBASE", "Erro ao excluir documento " + doc.getId(), e));
            }
        }).addOnFailureListener(e -> Log.e("FIREBASE", "Erro ao buscar documentos de views", e));
    }

    private void registrarClicksContatos(String idEmpreendimento) {
        if (idEmpreendimento == null || idEmpreendimento.trim().isEmpty()) {
            Log.e("Firestore", "ID do empreendimento inválido.");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Atualiza contador geral de cliques
        DocumentReference docRef = db.collection("Cliente").document(idEmpreendimento);
        docRef.update("clicksContatos", FieldValue.increment(1))
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "clicksContatos registrada com sucesso."))
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Erro ao registrar clicksContatos: ", e);
                    docRef.set(Collections.singletonMap("clicksContatos", 1), SetOptions.merge());
                });

        // Data do dia
        String dataAtual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DocumentReference docSemanaRef = db.collection("Cliente")
                .document(idEmpreendimento)
                .collection("dadosSemana")
                .document(dataAtual);

        // Verifica se visualizações e avaliações existem, e registra clique
        docSemanaRef.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> update = new HashMap<>();
            update.put("cliques", FieldValue.increment(1));

            // Se os campos 'contador' (visualizações) e 'avaliacoes' não existirem, inicializa com 0
            if (!documentSnapshot.exists() || !documentSnapshot.contains("contador")) {
                update.put("contador", 0);
            }
            if (!documentSnapshot.exists() || !documentSnapshot.contains("avaliacoes")) {
                update.put("avaliacoes", 0);
            }

            docSemanaRef.set(update, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d("FIREBASE", "clicksContatos diária registrada com sucesso"))
                    .addOnFailureListener(e -> Log.e("FIREBASE", "Erro ao registrar clique diário", e));
        });

        // Excluir dados com mais de 7 dias
        CollectionReference viewsRef = db.collection("Cliente")
                .document(idEmpreendimento)
                .collection("dadosSemana");

        viewsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<QueryDocumentSnapshot> documentos = new ArrayList<>();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                documentos.add(doc);
            }

            if (documentos.size() <= 7) {
                Log.d("FIREBASE", "Nada a excluir. Apenas " + documentos.size() + " dias registrados.");
                return;
            }

            // Ordenar os documentos pela data (ID)
            Collections.sort(documentos, Comparator.comparing(QueryDocumentSnapshot::getId));

            int quantidadeParaRemover = documentos.size() - 7;
            for (int i = 0; i < quantidadeParaRemover; i++) {
                QueryDocumentSnapshot doc = documentos.get(i);
                viewsRef.document(doc.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> Log.d("FIREBASE", "Documento " + doc.getId() + " excluído"))
                        .addOnFailureListener(e -> Log.e("FIREBASE", "Erro ao excluir documento " + doc.getId(), e));
            }
        }).addOnFailureListener(e -> Log.e("FIREBASE", "Erro ao buscar documentos de clicksContatos", e));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}