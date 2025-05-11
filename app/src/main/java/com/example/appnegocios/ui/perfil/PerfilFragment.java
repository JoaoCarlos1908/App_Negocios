package com.example.appnegocios.ui.perfil;

import static androidx.core.app.ActivityCompat.recreate;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.appnegocios.R;
import com.example.appnegocios.databinding.FragmentPerfilBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.checkerframework.common.subtyping.qual.Bottom;

public class PerfilFragment extends Fragment {
    private FragmentPerfilBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String usuarioID;
    private TextView editFoto;
    private EditText nome, desc, endereco;
    private ImageView iconUser;
    private Button bt_editar, bt_salvar, bt_cancelar;
    private View maps;

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
                editFoto.setVisibility(view.VISIBLE);
            }
        });

        bt_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Cliente").document(usuarioID)
                        .update(
                                "nome", nome.getText().toString(),
                                "descrição", desc.getText().toString(), // sem acento
                                "endereco", endereco.getText().toString() // padronize campos
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


        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                animarPreenchimento(maps);

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                // Código que será executado após
                                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                                navController.navigate(R.id.mapsFragment); // Use o ID correto definido no seu nav_graph
                            }
                        },
                        250); // tempo de atraso em milissegundos
            }
        });

        return view;
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
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null){
                    nome.setText(documentSnapshot.getString("nome"));
                    desc.setText(documentSnapshot.getString("descrição"));
                    endereco.setText(documentSnapshot.getString("Endereço"));
                }
            }
        });
    }

    private void iniciarComponentes(LayoutInflater inflater, ViewGroup container, View view){
        nome = view.findViewById(R.id.edit_nome);
        desc = view.findViewById(R.id.edit_descricao);
        endereco = view.findViewById(R.id.edit_endereco);
        iconUser = view.findViewById(R.id.iconUser);
        editFoto = view.findViewById(R.id.text_alterFoto);
        bt_editar = view.findViewById(R.id.bt_editar);
        bt_salvar = view.findViewById(R.id.bt_salvar);
        bt_cancelar = view.findViewById(R.id.bt_cancelar);
        maps = view.findViewById(R.id.bt_localizacao);
    }

    private void atualizarTela(){
        NavController navController = NavHostFragment.findNavController(this);
        navController.popBackStack(); // Remove o fragment atual da pilha
        navController.navigate(R.id.nav_perfil); // Reinsere o mesmo fragment
    }

}