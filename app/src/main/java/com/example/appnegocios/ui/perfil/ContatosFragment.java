package com.example.appnegocios.ui.perfil;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.appnegocios.R;
import com.example.appnegocios.databinding.FragmentContatosBinding;
import com.example.appnegocios.ui.dashboard.DashboardViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import Class.Contato;

public class ContatosFragment extends Fragment {

private FragmentContatosBinding binding;
private EditText tipo, contato;
private Button bt_adicionar;
private FirebaseFirestore db = FirebaseFirestore.getInstance();
private String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        // Infla o layout XML do fragmento
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        mostrarContatos(view);
        iniciarComponentes(view);

        bt_adicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tipo.getText().toString().trim().isEmpty()){
                    Snackbar snackbar = Snackbar.make(view, "Informe o tipo do contato", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();
                } else if (contato.getText().toString().trim().isEmpty()) {
                    Snackbar snackbar = Snackbar.make(view, "Informe o contato.", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();
                }else {
                    // Cria o documento de contato
                    Contato novoContato = new Contato();
                    novoContato.setTipo(tipo.getText().toString());
                    novoContato.setContato(contato.getText().toString());

                    // Adiciona na subcoleção 'contatos' dentro do usuário 'abc123'
                    db.collection("Cliente")
                            .document(usuarioID)
                            .collection("contatos")
                            .add(novoContato)
                            .addOnSuccessListener(docRef -> Log.d("FIREBASE", "Contato adicionado com ID: " + docRef.getId()))
                            .addOnFailureListener(e -> Log.e("FIREBASE", "Erro ao adicionar contato", e));

                    mostrarContatos(view);
                }

            }
        });

        return view;
    }

    private void iniciarComponentes(View view){
        tipo = view.findViewById(R.id.edit_tipo_contato);
        contato = view.findViewById(R.id.edit_contato);
        bt_adicionar = view.findViewById(R.id.bt_adicionar);
    }

    private void mostrarContatos(View view) {
        db.collection("Cliente")
                .document(usuarioID)
                .collection("contatos")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    LinearLayout containerInteracoes = view.findViewById(R.id.containerContatos);
                    containerInteracoes.removeAllViews(); // Evita duplicação

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Contato contato = doc.toObject(Contato.class);

                        if (contato != null) {
                            View item = getLayoutInflater().inflate(R.layout.layout_view_contatos, containerInteracoes, false);

                            TextView text_tipo = item.findViewById(R.id.text_tipo);
                            TextView text_contato = item.findViewById(R.id.text_contato);
                            TextView id_contato = item.findViewById(R.id.id_contato);
                            TextView bt_excluir = item.findViewById(R.id.bt_excluir);

                            text_tipo.setText(contato.getTipo());
                            text_contato.setText(contato.getContato());
                            id_contato.setText(doc.getId());

                            bt_excluir.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    db.collection("Cliente")
                                            .document(usuarioID)
                                            .collection("contatos")
                                            .document(id_contato.getText().toString()) // ID do documento do contato
                                            .delete()
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("FIREBASE", "Contato excluído com sucesso!");
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("FIREBASE", "Erro ao excluir contato", e);
                                            });
                                    mostrarContatos(view);
                                }
                            });

                            containerInteracoes.addView(item);
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}