package com.example.appnegocios.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnegocios.R;
import com.example.appnegocios.ui.dashboard.DashboardViewModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Class.CategoriaAdapter;
import Class.SubcategoriaAdapter;

public class CategoriasFragment extends Fragment {

    private RecyclerView containerLateral;
    private LinearLayout conteinerTop;
    private RecyclerView recyclerHorizontal;


    private Map<String, List<String>> mapaSubcategorias = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_categorias, container, false);

        containerLateral = view.findViewById(R.id.recyclerLateral);
        recyclerHorizontal = view.findViewById(R.id.recyclerHorizontal);


        carregarCategorias();

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

                        containerLateral.setLayoutManager(new LinearLayoutManager(getContext()));
                        CategoriaAdapter adapter = new CategoriaAdapter(nomesCategorias, this::exibirSubcategorias);
                        containerLateral.setAdapter(adapter);

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
                // 👇 Aqui você pode carregar os cards ou conteúdo relacionado à subcategoria
                Log.d("Subcategoria", "Selecionada: " + sub);
            });

            recyclerHorizontal.setLayoutManager(
                    new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
            );
            recyclerHorizontal.setAdapter(subAdapter);
        } else {
            recyclerHorizontal.setAdapter(null); // limpa o RecyclerView se não houver subcategorias
        }
    }
}

