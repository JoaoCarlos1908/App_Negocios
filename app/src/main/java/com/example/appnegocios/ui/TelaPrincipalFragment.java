package com.example.appnegocios.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.appnegocios.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Class.Empreendimento;

public class TelaPrincipalFragment extends Fragment {

    private View view;
    private EditText editTextPesquisa;
    private Map<String, List<Empreendimento>> mapaEmpreendimentos = new HashMap<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tela_principal, container, false);
        iniciarComponentes();

        carregarEmpreendimentos();
        carregarEmpreendimentos(new FirebaseCallback() {
            @Override
            public void onCallback(List<Empreendimento> lista) {
                exibirEmpreendimentosAleatorios(lista, 10, view); // aqui você limita e exibe como quiser
            }
        });

        editTextPesquisa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarEmpreendimentos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });


        return view;
    }

    private void iniciarComponentes(){
        editTextPesquisa = view.findViewById(R.id.editTextPesquisa);
    }

    private void filtrarEmpreendimentos(String texto) {
        String termo = texto.toLowerCase().trim();
        List<Empreendimento> filtrados = new ArrayList<>();
        Set<String> idsAdicionados = new HashSet<>(); // <- evita duplicatas

        for (List<Empreendimento> lista : mapaEmpreendimentos.values()) {
            for (Empreendimento emp : lista) {
                if (idsAdicionados.contains(emp.getIdUser())) continue;

                String nome = emp.getNome() != null ? emp.getNome().toLowerCase() : "";
                String categoria = emp.getCategoria() != null ? emp.getCategoria().toLowerCase() : "";
                List<String> subcategorias = emp.getSubcategorias() != null ? emp.getSubcategorias() : new ArrayList<>();

                boolean nomeMatch = nome.contains(termo);
                boolean categoriaMatch = categoria.contains(termo);
                boolean subMatch = false;
                for (String sub : subcategorias) {
                    if (sub.toLowerCase().contains(termo)) {
                        subMatch = true;
                        break;
                    }
                }

                if (nomeMatch || categoriaMatch || subMatch) {
                    filtrados.add(emp);
                    idsAdicionados.add(emp.getIdUser()); // <- marca como já adicionado
                }
            }
        }

        // Atualiza o RecyclerView com os itens filtrados
        exibirEmpreendimentosAleatorios(filtrados, 10, view);
    }



    public interface FirebaseCallback {
        void onCallback(List<Empreendimento> listaEmpreendimentos);
    }

    private void carregarEmpreendimentos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Cliente")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mapaEmpreendimentos.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Empreendimento emp = new Empreendimento();

                        Boolean tipoConta = doc.getBoolean("TipoConta");
                        if (tipoConta != null && tipoConta) {
                            emp.setIdUser(doc.getId());
                            emp.setNome(doc.getString("nome")); // herdado de Usuario
                            emp.setCategoria(doc.getString("categoria"));

                            // Carregando subcategorias do Firestore para a classe Empreendimento
                            List<String> subcategorias = (List<String>) doc.get("subcategorias");
                            if (subcategorias != null) {
                                emp.setSubcategorias(subcategorias);
                            }

                            // Mapeando por categoria principal
                            String categoria = emp.getCategoria();
                            if (!mapaEmpreendimentos.containsKey(categoria)) {
                                mapaEmpreendimentos.put(categoria, new ArrayList<>());
                            }
                            mapaEmpreendimentos.get(categoria).add(emp);

                            // Mapeando por subcategorias também
                            if (subcategorias != null) {
                                for (String sub : subcategorias) {
                                    if (!mapaEmpreendimentos.containsKey(sub)) {
                                        mapaEmpreendimentos.put(sub, new ArrayList<>());
                                    }
                                    mapaEmpreendimentos.get(sub).add(emp);
                                }
                            }
                        } else {
                            Log.d("Empreendimentos", "Erro ao carregar empreendimentos ou conta não cadastrada como cliente");
                        }
                    }

                    Log.d("Empreendimentos", "Empreendimentos carregados com sucesso.");
                })
                .addOnFailureListener(e -> Log.e("Empreendimentos", "Erro ao carregar empreendimentos", e));
    }
    private void carregarEmpreendimentos(FirebaseCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Empreendimento> listaEmpreendimentos = new ArrayList<>();

        db.collection("Cliente")
                .whereEqualTo("TipoConta", true) // Apenas empreendimentos
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Empreendimento empreendimento = doc.toObject(Empreendimento.class);
                        String idDocumento = doc.getId(); //pega id do documento
                        empreendimento.setIdUser(idDocumento);
                        listaEmpreendimentos.add(empreendimento);
                    }
                    callback.onCallback(listaEmpreendimentos);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Erro ao buscar empreendimentos", e);
                    callback.onCallback(new ArrayList<>()); // Retorna lista vazia em caso de erro
                });
    }

    private void exibirEmpreendimentosAleatorios(List<Empreendimento> listaOriginal, int limite, View view) {
        // Embaralha e limita
        Collections.shuffle(listaOriginal);
        List<Empreendimento> listaLimitada = listaOriginal.subList(0, Math.min(limite, listaOriginal.size()));

        LinearLayout horizontalContainer = view.findViewById(R.id.linearHorizontalContainer);
        LinearLayout verticalContainer = view.findViewById(R.id.linearVerticalContainer);

        horizontalContainer.removeAllViews();
        verticalContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(view.getContext());

        for (Empreendimento emp : listaLimitada) {
            // Inflate item horizontal
            View itemHorizontal = inflater.inflate(R.layout.layout_view_negocio1, horizontalContainer, false);
            ((TextView) itemHorizontal.findViewById(R.id.text_nome)).setText(emp.getNome());
            ((TextView) itemHorizontal.findViewById(R.id.text_categoria)).setText(emp.getCategoria());
            // TODO: Defina imagem se houver

            //Função de Click visualizar perfil empreendimento.
            itemHorizontal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("idEmpreendimento", emp.getIdUser()); // Substitua pelo ID real do empreendimento

                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                    navController.navigate(R.id.nav_view_perfil_empreedimento, bundle);

                }
            });

            horizontalContainer.addView(itemHorizontal);

            // Inflate item vertical
            View itemVertical = inflater.inflate(R.layout.layout_view_negocio2, verticalContainer, false);
            ((TextView) itemVertical.findViewById(R.id.text_nome)).setText(emp.getNome());
            ((TextView) itemVertical.findViewById(R.id.text_endereco)).setText(emp.getEndereco());
            ((RatingBar) itemVertical.findViewById(R.id.estrelas)).setRating(4.0f); // exemplo fixo
            // TODO: Defina imagem se houver

            //Função de Click visualizar perfil empreendimento.
            itemVertical.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("idEmpreendimento", emp.getIdUser()); // Substitua pelo ID real do empreendimento

                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                    navController.navigate(R.id.nav_view_perfil_empreedimento, bundle);
                }
            });

            verticalContainer.addView(itemVertical);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}