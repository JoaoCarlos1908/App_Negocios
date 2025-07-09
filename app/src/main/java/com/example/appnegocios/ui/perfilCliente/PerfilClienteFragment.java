package com.example.appnegocios.ui.perfilCliente;


import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.appnegocios.R;
import com.example.appnegocios.databinding.FragmentPerfilBinding;
import com.example.appnegocios.ui.perfil.PerfilFragment;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PerfilClienteFragment extends Fragment {
    private FragmentPerfilBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String usuarioID, caminhoIcon;;
    private TextView text_alterFoto;
    private EditText nome, telefone, cep;
    private ImageView iconUser;
    private Button bt_editar, bt_salvar, bt_cancelar;
    private Uri selectedImageUri;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil_cliente, container, false);

        iniciarComponentes(inflater, container, view);

        iconUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(PerfilClienteFragment.this)
                        .cropSquare() // <-- Isso força o corte 1:1 (quadrado)
                        .compress(1024) // tamanho máximo em KB
                        .maxResultSize(1080, 1080) // resolução máxima
                        .start();
            }

        });

        text_alterFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(PerfilClienteFragment.this)
                        .cropSquare() // <-- Isso força o corte 1:1 (quadrado)
                        .compress(1024) // tamanho máximo em KB
                        .maxResultSize(1080, 1080) // resolução máxima
                        .start();
            }
        });

        bt_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_editar.setVisibility(View.GONE);
                bt_salvar.setVisibility(View.VISIBLE);
                bt_cancelar.setVisibility(View.VISIBLE);
                nome.setEnabled(true);
                telefone.setEnabled(true);
                cep.setEnabled(true);
                iconUser.setClickable(true);
                iconUser.setEnabled(true);
                text_alterFoto.setVisibility(view.VISIBLE);
            }
        });

        bt_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImageUri != null) {
                    salvarImagemPerfil(usuarioID);
                }

                db.collection("Cliente").document(usuarioID)
                        .update(
                                "nome", nome.getText().toString(),
                                "telefone", telefone.getText().toString(), // sem acento
                                "CEP", cep.getText().toString() // padronize campos
                        )
                        .addOnSuccessListener(aVoid -> {
                            Snackbar snackbar = Snackbar.make(v, "Perfil atualizado!", Snackbar.LENGTH_SHORT);
                            snackbar.setBackgroundTint(Color.GREEN);
                            snackbar.setTextColor(Color.BLACK);
                            snackbar.show();

                            atualizarTela(); // só chama aqui depois do sucesso
                        })
                        .addOnFailureListener(e -> {
                            Snackbar snackbar = Snackbar.make(v, "Erro: " + e.getMessage(), Snackbar.LENGTH_SHORT);
                            snackbar.setBackgroundTint(Color.RED);
                            snackbar.setTextColor(Color.WHITE);
                            snackbar.show();
                        });
            }
        });

        bt_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizarTela();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                selectedImageUri = uri;
                iconUser.setImageURI(uri); // exibe imagem escolhida
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Ação cancelada", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("Cliente").document(usuarioID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    nome.setText(documentSnapshot.getString("nome"));
                    telefone.setText(documentSnapshot.getString("telefone"));
                    cep.setText(documentSnapshot.getString("CEP"));
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
    }

    private void iniciarComponentes(LayoutInflater inflater, ViewGroup container, View view) {
        nome = view.findViewById(R.id.edit_nome);
        telefone = view.findViewById(R.id.edit_telefone);
        cep = view.findViewById(R.id.edit_cep);
        iconUser = view.findViewById(R.id.iconUser);
        iconUser.setClickable(false);
        iconUser.setEnabled(false);
        text_alterFoto = view.findViewById(R.id.text_alterFoto);
        bt_editar = view.findViewById(R.id.bt_editar);
        bt_salvar = view.findViewById(R.id.bt_salvar);
        bt_cancelar = view.findViewById(R.id.bt_cancelar);
    }

    private void atualizarTela() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.popBackStack(); // Remove o fragment atual da pilha
        navController.navigate(R.id.nav_perfilCliente); // Reinsere o mesmo fragment
    }

    private void salvarImagemPerfil(String userId) {
        if (selectedImageUri == null) {
            Log.e("FIREBASE", "selectedImageUri está nulo! Não há imagem para enviar.");
            return;
        }

        Log.d("FIREBASE", "Iniciando upload da nova imagem: " + selectedImageUri.toString());

        // Primeiro, tenta apagar a imagem anterior se houver
        if (caminhoIcon != null && !caminhoIcon.isEmpty()) {
            StorageReference antigaRef = FirebaseStorage.getInstance().getReferenceFromUrl(caminhoIcon);

            antigaRef.delete()
                    .addOnSuccessListener(aVoid -> Log.d("FIREBASE", "Imagem anterior deletada com sucesso."))
                    .addOnFailureListener(e -> Log.e("FIREBASE", "Erro ao deletar imagem anterior", e));
        }

        // Agora faz upload da nova imagem
        StorageReference novaImagemRef = FirebaseStorage.getInstance()
                .getReference()
                .child("fotos_perfil/" + userId + ".jpg");

        novaImagemRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    novaImagemRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        Log.d("FIREBASE", "URL da nova imagem: " + imageUrl);

                        // Atualiza no Firestore
                        db.collection("Cliente").document(userId)
                                .update("fotoPerfil", imageUrl)
                                .addOnSuccessListener(aVoid -> Log.d("FIREBASE", "URL da nova imagem salva no Firestore"))
                                .addOnFailureListener(e -> Log.e("FIREBASE", "Erro ao salvar nova URL no Firestore", e));
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE", "Erro ao fazer upload da nova imagem", e);
                });
    }

}