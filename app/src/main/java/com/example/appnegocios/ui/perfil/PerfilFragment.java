package com.example.appnegocios.ui.perfil;

import static android.app.Activity.RESULT_OK;
import static androidx.core.app.ActivityCompat.recreate;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.appnegocios.FormCadastro;
import com.example.appnegocios.R;
import com.example.appnegocios.databinding.FragmentPerfilBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.common.subtyping.qual.Bottom;

import java.util.HashMap;
import java.util.Map;

public class PerfilFragment extends Fragment {
    private FragmentPerfilBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private TextView editFoto, text_categoria, text_alterFoto;
    private EditText nome, desc, endereco;
    private Button bt_editar, bt_salvar, bt_cancelar;
    private View maps, contatos, links, horas, categoria;
    private ShapeableImageView iconUser;
    private String caminhoIcon;
    private Uri selectedImageUri;
    private ListenerRegistration perfilListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        iniciarComponentes(inflater, container, view);

        bt_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_editar.setVisibility(View.GONE);
                bt_salvar.setVisibility(View.VISIBLE);
                bt_cancelar.setVisibility(View.VISIBLE);
                nome.setEnabled(true);
                desc.setEnabled(true);
                endereco.setEnabled(true);
                iconUser.setClickable(true);
                iconUser.setEnabled(true);
                editFoto.setVisibility(View.VISIBLE);
                text_alterFoto.setVisibility(View.VISIBLE);
                iconUser.setClickable(true);
            }
        });

        iconUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(PerfilFragment.this)
                        .cropSquare() // <-- Isso força o corte 1:1 (quadrado)
                        .compress(1024) // tamanho máximo em KB
                        .maxResultSize(1080, 1080) // resolução máxima
                        .start();
            }

        });

        text_alterFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(PerfilFragment.this)
                        .cropSquare() // <-- Isso força o corte 1:1 (quadrado)
                        .compress(1024) // tamanho máximo em KB
                        .maxResultSize(1080, 1080) // resolução máxima
                        .start();
            }
        });

        bt_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> dadosUsuario = new HashMap<>();
                dadosUsuario.put("nome", nome.getText().toString());
                dadosUsuario.put("descricao", desc.getText().toString());
                dadosUsuario.put("endereco", endereco.getText().toString());

                if (selectedImageUri != null) {
                    salvarImagemPerfil(usuarioID);
                }

                db.collection("Cliente").document(usuarioID)
                        .update(dadosUsuario)
                        .addOnSuccessListener(aVoid -> {
                            Snackbar snackbar = Snackbar.make(v, "Perfil atualizado!", Snackbar.LENGTH_SHORT);
                            snackbar.setBackgroundTint(Color.GREEN);
                            snackbar.setTextColor(Color.BLACK);
                            snackbar.show();
                            atualizarTela();
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

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animarPreenchimento(maps);
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                Bundle bundle = new Bundle();
                                bundle.putString("idUser", usuarioID);
                                bundle.putBoolean("exibir", true);

                                // Código que será executado após
                                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                                navController.navigate(R.id.mapsFragment, bundle); // Use o ID correto definido no seu nav_graph
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
                                bundle.putString("idUser", usuarioID);
                                bundle.putBoolean("exibir", true);

                                // Código que será executado após
                                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                                navController.navigate(R.id.contatosFragment, bundle); // Use o ID correto definido no seu nav_graph
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
                                bundle.putString("idUser", usuarioID);
                                bundle.putBoolean("exibir", true);

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
                                bundle.putString("idUser", usuarioID);
                                bundle.putBoolean("exibir", true);

                                // Código que será executado após
                                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                                navController.navigate(R.id.horariosFragment, bundle); // Use o ID correto definido no seu nav_graph
                            }
                        },
                        250); // tempo de atraso em milissegundos
            }
        });

        categoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoriasDialogFragment dialog = new CategoriasDialogFragment();
                dialog.show(getParentFragmentManager(), "Dialog");
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
        perfilListener = documentReference.addSnapshotListener((documentSnapshot, error) -> {
            if (documentSnapshot != null) {
                nome.setText(documentSnapshot.getString("nome"));
                desc.setText(documentSnapshot.getString("descricao"));
                endereco.setText(documentSnapshot.getString("endereco"));
                String text = documentSnapshot.getString("categoria");
                text_categoria.setText("Categoria: " + text);
                text_categoria.setText(inserirQuebraEntrePalavras(text_categoria.getText().toString(), 25));
                caminhoIcon = documentSnapshot.getString("fotoPerfil");

                if (isAdded() && caminhoIcon != null && !caminhoIcon.isEmpty()) {
                    Glide.with(requireContext())
                            .load(caminhoIcon)
                            .placeholder(R.drawable.ic_perfil)
                            .error(R.drawable.ic_perfil)
                            .into(iconUser);
                }
            }
        });
    }

    private void iniciarComponentes(LayoutInflater inflater, ViewGroup container, View view) {

        nome = view.findViewById(R.id.edit_nome);
        desc = view.findViewById(R.id.edit_descricao);
        endereco = view.findViewById(R.id.edit_endereco);
        iconUser = view.findViewById(R.id.iconUser);
        iconUser.setEnabled(false);
        editFoto = view.findViewById(R.id.text_alterFoto);
        bt_editar = view.findViewById(R.id.bt_editar);
        bt_salvar = view.findViewById(R.id.bt_salvar);
        bt_cancelar = view.findViewById(R.id.bt_cancelar);
        maps = view.findViewById(R.id.bt_localizacao);
        contatos = view.findViewById(R.id.bt_contatos);
        links = view.findViewById(R.id.bt_links);
        horas = view.findViewById(R.id.bt_relogio);
        categoria = view.findViewById(R.id.bt_categoria);
        text_categoria = view.findViewById(R.id.text_categoria);
        text_alterFoto = view.findViewById(R.id.text_alterFoto);
    }

    private void atualizarTela() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.popBackStack(); // Remove o fragment atual da pilha
        navController.navigate(R.id.nav_perfil); // Reinsere o mesmo fragment
    }

    public String inserirQuebraEntrePalavras(String texto, int limite) {
        StringBuilder resultado = new StringBuilder();
        String[] palavras = texto.split(" ");
        int linhaAtual = 0;

        for (String palavra : palavras) {
            if (linhaAtual + palavra.length() > limite) {
                resultado.append("\n");
                linhaAtual = 0;
            }
            resultado.append(palavra).append(" ");
            linhaAtual += palavra.length() + 1; // +1 por causa do espaço
        }

        return resultado.toString().trim();
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


    @Override
    public void onStop() {
        super.onStop();
        if (perfilListener != null) {
            perfilListener.remove();
            perfilListener = null;
        }
    }


}