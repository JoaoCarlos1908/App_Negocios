package com.example.appnegocios.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;
import java.util.Map;

import Class.CategoriaAdapter;
import Class.SubcategoriaAdapter;
import Class.Empreendimento;
import Class.EmpreendimentoAdapter;

public class CategoriasFragment extends Fragment {

    private RecyclerView recyclerLateral, recyclerHorizontal, recyclerConteudo;
    private Map<String, List<Empreendimento>> mapaEmpreendimentos = new HashMap<>();


    private Map<String, List<String>> mapaSubcategorias = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_categorias, container, false);

        recyclerLateral = view.findViewById(R.id.recyclerLateral);
        recyclerHorizontal = view.findViewById(R.id.recyclerHorizontal);
        recyclerConteudo = view.findViewById(R.id.recyclerConteudo);


        carregarCategorias();
        carregarEmpreendimentos();

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
            EmpreendimentoAdapter adapter = new EmpreendimentoAdapter(lista);
            recyclerConteudo.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerConteudo.setAdapter(adapter);
        } else {
            recyclerConteudo.setAdapter(null);
        }
    }

    private void carregarEmpreendimentos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("empreendimentos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mapaEmpreendimentos.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Empreendimento emp = new Empreendimento();

                        emp.setNome(doc.getString("nome")); // herdado de Usuario
                        emp.setDescricao(doc.getString("descrição"));
                        emp.setEndereco(doc.getString("Endereço"));
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
                    }

                    Log.d("Empreendimentos", "Empreendimentos carregados com sucesso.");
                })
                .addOnFailureListener(e -> Log.e("Empreendimentos", "Erro ao carregar empreendimentos", e));
    }



}

