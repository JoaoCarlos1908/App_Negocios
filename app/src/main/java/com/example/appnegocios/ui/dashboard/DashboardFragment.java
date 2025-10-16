package com.example.appnegocios.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.appnegocios.R;
import com.example.appnegocios.databinding.FragmentDashboardBinding;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView text_nomeEmpre, text_avaliacoes, text_Res, quant_views, quant_cliks;
    private LinearLayout containerInteracoes;
    private DocumentReference documentReference;
    private Button btnAbrirOutroFragmento;
    private ShapeableImageView iconUser;
    private String caminhoIcon;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Infla o layout XML do fragmento
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        iniciarComponentes(view);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    text_nomeEmpre.setText(documentSnapshot.getString("nome"));

                    Long views = documentSnapshot.getLong("views");
                    if (views != null) {quant_views.setText(String.valueOf(views));}
                    else {quant_views.setText("0");}

                    Long clicksContatos = documentSnapshot.getLong("clicksContatos");
                    if (clicksContatos != null) {quant_cliks.setText(String.valueOf(clicksContatos));}
                    else {quant_cliks.setText("0");}

                    caminhoIcon = documentSnapshot.getString("fotoPerfil");

                    if (isAdded() && caminhoIcon != null && !caminhoIcon.isEmpty()) {
                        Glide.with(requireContext())
                                .load(caminhoIcon)
                                .placeholder(R.drawable.ic_perfil)
                                .error(R.drawable.ic_perfil)
                                .into(iconUser);
                    }
                }
            }
        });

        carregarInteracoes();

        btnAbrirOutroFragmento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                navController.navigate(R.id.dashboardPlusFragment); // Use o ID correto definido no seu nav_graph
            }
        });

        return view;
    }

    private void iniciarComponentes(View view){
        // Acessa o LinearLayout dentro do layout do fragment
        containerInteracoes = view.findViewById(R.id.containerInteracoes);

        text_nomeEmpre = view.findViewById(R.id.text_nomeEmpre);
        text_avaliacoes = view.findViewById(R.id.text_avaliacoes);
        text_Res = view.findViewById(R.id.text_Res);
        quant_views = view.findViewById(R.id.quant_views);
        quant_cliks = view.findViewById(R.id.quant_cliks);
        btnAbrirOutroFragmento = view.findViewById(R.id.bt_detalhes);
        iconUser = view.findViewById(R.id.iconUser);

        exibirTaxasAvaliacoesReclamacoes(usuarioID);

        documentReference = db.collection("Cliente").document(usuarioID);
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
                    } else {
                        Log.d("MÉDIA", "Sem avaliações para esse empreendimento.");
                        text_avaliacoes.setText("#");
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

    private void carregarInteracoes() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String usuarioId = auth.getCurrentUser().getUid();

        db.collection("Cliente")
                .document(usuarioId)
                .collection("Interacoes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    containerInteracoes.removeAllViews(); // limpa antes de carregar

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        String descricao = doc.getString("descricao");
                        String tempo = doc.getString("tempo");
                        String categoria = doc.getString("categoria");

                        // Infla o layout de item individual
                        View item = getLayoutInflater().inflate(R.layout.layout_view_interacao, containerInteracoes, false);

                        ImageView icCategoria = item.findViewById(R.id.icCategoria);
                        TextView tvDescricao = item.findViewById(R.id.tvDescricao);
                        TextView tvTempo = item.findViewById(R.id.tvTempo);
                        TextView tvDetalhes = item.findViewById(R.id.tvDetalhes);

                        // Preenche os campos
                        tvDescricao.setText(descricao != null ? descricao : "Sem descrição");
                        tvTempo.setText(tempo != null ? tempo : "Sem tempo");

                        // Exemplo: muda o ícone baseado na categoria
                        if (categoria != null) {
                            switch (categoria) {
                                case "pefil":
                                    icCategoria.setImageResource(R.drawable.ic_perfil);
                                    break;
                                case "chat":
                                    icCategoria.setImageResource(R.drawable.ic_balao_fala);
                                    break;
                                case "avaliacao":
                                    icCategoria.setImageResource(R.drawable.ic_estrela);
                                    break;
                                case "reclamacao":
                                    icCategoria.setImageResource(R.drawable.ic_negativo);
                                    break;
                                default:
                                    icCategoria.setImageResource(R.drawable.ic_sino);
                                    break;
                            }
                        } else {
                            icCategoria.setImageResource(R.drawable.ic_sino);
                        }

                        // Adiciona ao container
                        containerInteracoes.addView(item);
                    }

                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(getContext(), "Nenhuma interação encontrada.", Toast.LENGTH_SHORT).show();
                    }

                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE", "Erro ao carregar interações", e);
                    Toast.makeText(getContext(), "Erro ao carregar interações", Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}