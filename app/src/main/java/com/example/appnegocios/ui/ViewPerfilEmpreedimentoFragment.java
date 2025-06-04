package com.example.appnegocios.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.appnegocios.ui.perfil.HorariosDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ViewPerfilEmpreedimentoFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private String idEmpreendimento;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView text_nomeEmpre;
    private Button bt_avaliar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Infla o layout XML do fragmento
        View view = inflater.inflate(R.layout.fragment_view_perfil_empreendimento, container, false);
        // Acessa o LinearLayout dentro do layout do fragment
        LinearLayout containerInteracoes = view.findViewById(R.id.containerProdutos);
        text_nomeEmpre = view.findViewById(R.id.text_nomeEmpre);

        idEmpreendimento = getArguments().getString("idEmpreendimento");

        DocumentReference documentReference = db.collection("Cliente").document(idEmpreendimento);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    text_nomeEmpre.setText(documentSnapshot.getString("nome"));
                }
            }
        });

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
                EditText edit_comentario = dialog.findViewById(R.id.edit_comentario);
                edit_comentario.setVisibility(View.VISIBLE);

                LinearLayout llbotoes = dialog.findViewById(R.id.llbotoes);
                llbotoes.setVisibility(View.VISIBLE);

                TextView tvResposta = dialog.findViewById(R.id.tvResposta);
                tvResposta.setVisibility(View.GONE);

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
                btnSalvar.setOnClickListener(view -> dialog.dismiss());

                dialog.show();
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