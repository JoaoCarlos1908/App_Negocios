package com.example.appnegocios.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnegocios.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Class.CategoriaAdapter;
import Class.SubcategoriaAdapter;
import Class.Empreendimento;
import Class.EmpreendimentoAdapter;
import androidx.recyclerview.widget.GridLayoutManager;

public class CategoriasFragment extends Fragment {

    private RecyclerView recyclerLateral, recyclerHorizontal, recyclerConteudo;
    private Map<String, List<Empreendimento>> mapaEmpreendimentos = new HashMap<>();
    private Map<String, List<String>> mapaSubcategorias = new HashMap<>();
    private ImageView btnBuscar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categorias, container, false);
        EditText editTextPesquisa = view.findViewById(R.id.editTextPesquisa);
        btnBuscar = view.findViewById(R.id.btnBuscar);

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

        btnBuscar.setOnClickListener(v -> {
            String texto = editTextPesquisa.getText().toString().trim();
            filtrarEmpreendimentos(texto);
        });

        recyclerLateral = view.findViewById(R.id.recyclerLateral);
        recyclerHorizontal = view.findViewById(R.id.recyclerHorizontal);
        recyclerConteudo = view.findViewById(R.id.recyclerConteudo);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerConteudo.setLayoutManager(gridLayoutManager);


        carregarCategorias();
        carregarEmpreendimentos(() -> {
            if (getArguments() != null && getArguments().containsKey("categoria")) {
                String categoria = getArguments().getString("categoria");
                exibirSubcategorias(categoria);
                exibirEmpreendimentos(categoria);
            }
        });

        return view;
    }

    private void carregarCategorias() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("categorias")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<String> nomesCategorias = new ArrayList<>();
                        mapaSubcategorias.clear();

                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            String nomeCategoria = document.getId();
                            List<String> subcategorias = (List<String>) document.get("subcategorias");

                            nomesCategorias.add(nomeCategoria);
                            mapaSubcategorias.put(nomeCategoria, subcategorias != null ? subcategorias : new ArrayList<>());
                        }



                        recyclerLateral.setLayoutManager(new LinearLayoutManager(getContext()));
                        CategoriaAdapter adapter = new CategoriaAdapter(nomesCategorias, categoria -> {
                            exibirSubcategorias(categoria);
                            exibirEmpreendimentos(categoria); // Mostra os empreendimentos ao clicar na categoria

                        });
                        recyclerLateral.setAdapter(adapter);
                    } else {
                        Log.d("Categorias", "Nenhuma categoria encontrada.");
                    }

                })
                .addOnFailureListener(e -> Log.e("Categorias", "Erro ao carregar categorias", e));
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
        recyclerConteudo.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerConteudo.setAdapter(new EmpreendimentoAdapter(filtrados, this));
    }

    private void exibirSubcategorias(String categoriaSelecionada) {
        List<String> subcats = mapaSubcategorias.get(categoriaSelecionada);

        if (subcats != null && !subcats.isEmpty()) {
            SubcategoriaAdapter subAdapter = new SubcategoriaAdapter(subcats, sub -> {
                Log.d("Subcategoria", "Selecionada: " + sub);
                exibirEmpreendimentos(sub); // Mostra os empreendimentos da subcategoria
            });

            recyclerHorizontal.setLayoutManager(
                    new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
            );
            recyclerHorizontal.setAdapter(subAdapter);
        } else {
            recyclerHorizontal.setAdapter(null); // limpa o RecyclerView se não houver subcategorias
        }
    }

    private void exibirEmpreendimentos(String chave) {
        List<Empreendimento> lista = mapaEmpreendimentos.get(chave);
        if (lista != null && !lista.isEmpty()) {
            EmpreendimentoAdapter adapter = new EmpreendimentoAdapter(lista, CategoriasFragment.this);
            recyclerConteudo.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerConteudo.setAdapter(adapter);
        } else {
            recyclerConteudo.setAdapter(null);
        }
    }

    private void carregarEmpreendimentos(Runnable callback) {
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
                            emp.setNome(doc.getString("nome"));
                            emp.setCategoria(doc.getString("categoria"));

                            List<String> subcategorias = (List<String>) doc.get("subcategorias");
                            if (subcategorias != null) {
                                emp.setSubcategorias(subcategorias);
                            }

                            String categoria = emp.getCategoria();
                            if (!mapaEmpreendimentos.containsKey(categoria)) {
                                mapaEmpreendimentos.put(categoria, new ArrayList<>());
                            }
                            mapaEmpreendimentos.get(categoria).add(emp);

                            if (subcategorias != null) {
                                for (String sub : subcategorias) {
                                    if (!mapaEmpreendimentos.containsKey(sub)) {
                                        mapaEmpreendimentos.put(sub, new ArrayList<>());
                                    }
                                    mapaEmpreendimentos.get(sub).add(emp);
                                }
                            }
                        }
                    }

                    Log.d("Empreendimentos", "Empreendimentos carregados com sucesso.");

                    if (callback != null) {
                        callback.run(); // Chama o que você quiser fazer depois
                    }
                })
                .addOnFailureListener(e -> Log.e("Empreendimentos", "Erro ao carregar empreendimentos", e));
    }


}

