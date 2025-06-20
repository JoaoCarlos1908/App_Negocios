package com.example.appnegocios.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

    private Button btnRespondidas, btnNaoRespondidas;
    private String idCliente = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private View view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reclamacoes_cliente, container, false);

        btnRespondidas = view.findViewById(R.id.btnRespodidas);
        btnNaoRespondidas = view.findViewById(R.id.btnNaoRespodidas);

        carregarReclamacoes(true, true);

        btnRespondidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carregarReclamacoes(false, true);
            }
        });

        btnNaoRespondidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carregarReclamacoes(false, false);
            }
        });

        return view;
    }

    private void carregarReclamacoes(Boolean exibirTudo, Boolean exibirRN) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        LinearLayout container = view.findViewById(R.id.containerReclamacoes);
        container.removeAllViews();

        db.collection("Reclamacoes")
                .whereEqualTo("idCliente", idCliente)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Reclamacoes reclamacao = doc.toObject(Reclamacoes.class);
                        reclamacao.setIdReclamacao(doc.getId());

                        String idEmp = doc.getString("idEmpreendimento");

                        if (idEmp != null) {
                            db.collection("Cliente")
                                    .document(idEmp)
                                    .get()
                                    .addOnSuccessListener(empDoc -> {
                                        String nomeEmp = empDoc.getString("nome");
                                        if (nomeEmp != null) {
                                            reclamacao.setNomeEmpre(nomeEmp);
                                        }
                                        exibirReclamacao(container, reclamacao, exibirTudo, exibirRN);
                                    });
                        }
                    }
                });
    }

    private void exibirReclamacao(LinearLayout container, Reclamacoes reclamacao, Boolean exibirTudo, Boolean exibirRN) {
        View item = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_view_reclamacoes, container, false);

        TextView tvNome = item.findViewById(R.id.tvNome);
        TextView tvComentario = item.findViewById(R.id.tvComentario);
        TextView tvResposta = item.findViewById(R.id.tvResposta);
        TextView tvRespostatext = item.findViewById(R.id.tvRespostatext);

        tvNome.setText("Empreendimento: " + reclamacao.getNomeEmpre());
        tvComentario.setText(reclamacao.getReclamacao());

        tvResposta.setOnClickListener(v -> {
            String resposta = reclamacao.getResposta();

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


        if (exibirTudo || reclamacao.getRespondida() == exibirRN) {
            container.addView(item);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}