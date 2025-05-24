package com.example.appnegocios.ui.produtos;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.appnegocios.R;
import com.example.appnegocios.databinding.FragmentProdutosBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Class.Produto;

public class ProdutosFragment extends Fragment {

    private FragmentProdutosBinding binding;
    private Button bt_destacar, bt_organizar, bt_adicionar;
    private List<Produto> listaProdutos = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_produtos, container, false);

        iniciarComponentes(view);
        carregarProdutosDoUsuario(view);

        bt_adicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Código que será executado após
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                navController.navigate(R.id.addProdutos); // Use o ID correto definido no seu nav_graph
            }
        });

        bt_destacar.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Função destacar em breve!", Toast.LENGTH_SHORT).show();
        });

        bt_organizar.setOnClickListener(v -> {
           mostrarMenuOrganizar(v, view);
        });



        return view;
    }

    private void iniciarComponentes(View view) {
        bt_destacar = view.findViewById(R.id.btnDestacar);
        bt_organizar = view.findViewById(R.id.btnOrganizar);
        bt_adicionar = view.findViewById(R.id.btnAdicionar);
    }

    private void mostrarMenuOrganizar(View anchor, View view) {
        PopupMenu popup = new PopupMenu(getContext(), anchor);
        popup.getMenuInflater().inflate(R.menu.menu_organizar, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_nome_az) {
                Collections.sort(listaProdutos, Comparator.comparing(Produto::getTitulo));
            } else if (id == R.id.menu_nome_za) {
                Collections.sort(listaProdutos, (p1, p2) -> p2.getTitulo().compareTo(p1.getTitulo()));
            } else if (id == R.id.menu_valor_crescente) {
                Collections.sort(listaProdutos, Comparator.comparingDouble(Produto::getValor));
            } else if (id == R.id.menu_valor_decrescente) {
                Collections.sort(listaProdutos, (p1, p2) -> Double.compare(p2.getValor(), p1.getValor()));
            }

            exibirProdutosOrdenados(view); // Mostra os produtos ordenados
            return true;
        });

        popup.show();
    }

    private void exibirProdutosOrdenados(View view) {
        LinearLayout conteinerProdutos = view.findViewById(R.id.containerContatos);
        conteinerProdutos.removeAllViews();

        for (Produto produto : listaProdutos) {
            View viewProduto = LayoutInflater.from(getContext()).inflate(R.layout.layout_view_produtos, conteinerProdutos, false);

            TextView tvNome = viewProduto.findViewById(R.id.tvNomeProduto);
            TextView tvValor = viewProduto.findViewById(R.id.tvValorProduto);
            TextView tvId = viewProduto.findViewById(R.id.idProduto);

            tvNome.setText(produto.getTitulo());
            tvValor.setText("R$ " + produto.getValor());
            tvId.setText(produto.getIdProduto());

            ImageButton btneditar = viewProduto.findViewById(R.id.btnEditar);
            btneditar.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("titulo", produto.getTitulo());
                bundle.putString("descricao", produto.getDescricao());
                bundle.putString("valor", Double.toString(produto.getValor()));
                bundle.putString("idProduto", produto.getIdProduto());

                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                navController.navigate(R.id.addProdutos, bundle);
            });

            conteinerProdutos.addView(viewProduto);
        }
    }


    private void carregarProdutosDoUsuario(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Referência ao container no layout onde os produtos serão exibidos
        LinearLayout conteinerProdutos = view.findViewById(R.id.containerContatos);

        db.collection("Cliente")
                .document(usuarioID)
                .collection("produtos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaProdutos.clear();
                    conteinerProdutos.removeAllViews(); // Limpa os produtos antes de adicionar

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Produto produto = document.toObject(Produto.class);
                        produto.setIdProduto(document.getId());
                        listaProdutos.add(produto);
                        // Infla a view personalizada de item de produto
                        View viewProduto = LayoutInflater.from(getContext()).inflate(R.layout.layout_view_produtos, conteinerProdutos, false);

                        TextView tvNome = viewProduto.findViewById(R.id.tvNomeProduto);
                        TextView tvValor = viewProduto.findViewById(R.id.tvValorProduto);
                        TextView tvId = viewProduto.findViewById(R.id.idProduto);

                        tvNome.setText(produto.getTitulo());
                        tvValor.setText("R$ " + produto.getValor());
                        tvId.setText(produto.getIdProduto());

                        ImageButton btneditar = viewProduto.findViewById(R.id.btnEditar);
                        btneditar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putString("titulo", produto.getTitulo());
                                bundle.putString("descricao", produto.getDescricao());
                                String valor = Double.toString(produto.getValor());
                                bundle.putString("valor", valor);
                                bundle.putString("idProduto", produto.getIdProduto());

                                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                                navController.navigate(R.id.addProdutos, bundle);

                            }
                        });

                        // Adiciona a view ao container
                        conteinerProdutos.addView(viewProduto);
                    }
                })
                .addOnFailureListener(e -> {
                    //Toast.makeText(getContext(), "Erro ao carregar produtos", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}