package com.example.appnegocios.ui.produtos;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.appnegocios.R;
import com.example.appnegocios.databinding.FragmentProdutosBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import Class.Produto;

public class ProdutosAddFragment extends Fragment {

    private FragmentProdutosBinding binding;
    private Button bt_adiconarVar, bt_cancelar, bt_excluir, bt_salvar;
    private EditText text_titulo, text_desc, text_valor;
    private TextView IDProduto;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View viewAdd = inflater.inflate(R.layout.fragment_add_produtos, container, false);

        iniciarComponentes(viewAdd);

        Bundle args = getArguments();
        if (args != null) {
            String titulo = args.getString("titulo", "");
            String descricao = args.getString("descricao", "");
            String valor = args.getString("valor", "");
            String idProduto = args.getString("idProduto", "");

            // Verifica se é modo edição
            if (!idProduto.isEmpty()) {
                // Preencher os campos com os dados
                text_titulo.setText(titulo);
                text_desc.setText(descricao);
                text_valor.setText(valor);
                IDProduto.setText(idProduto);

                // Você pode também ocultar o botão "Salvar" e mostrar "Atualizar", por exemplo
                bt_salvar.setText("Atualizar");
                bt_excluir.setVisibility(View.VISIBLE);
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Editar Produto/Serviço");
            }
        }

        bt_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        bt_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulo = text_titulo.getText().toString().trim();
                String descricao = text_desc.getText().toString().trim();
                String valorTexto = text_valor.getText().toString().trim();
                String id = IDProduto.getText().toString().trim();

                if (TextUtils.isEmpty(titulo) || TextUtils.isEmpty(descricao) || TextUtils.isEmpty(valorTexto)) {
                    mostrarSnackbar("Preencha todos os campos", false, viewAdd);
                    return;
                }

                Produto produto = new Produto();
                produto.setTitulo(titulo);
                produto.setDescricao(descricao);

                try {
                    produto.setValor(Double.parseDouble(valorTexto.replace(",", ".")));
                } catch (NumberFormatException e) {
                    mostrarSnackbar("Valor inválido", false, viewAdd);
                    return;
                }

                if (TextUtils.isEmpty(id)) {
                    salvarProdutoFirebase(produto, v);
                } else {
                    produto.setIdProduto(id);
                    atualizarProdutoFirebase(id, produto, viewAdd);
                }

                requireActivity().getSupportFragmentManager().popBackStack();
            }

        });


        bt_excluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                excluirProdutoFirebase(IDProduto.getText().toString(), viewAdd);
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return viewAdd;
    }

    private void iniciarComponentes(View viewAdd){
        bt_adiconarVar = viewAdd.findViewById(R.id.btnAdicionarVariacao);
        bt_cancelar = viewAdd.findViewById(R.id.btnCancelar);
        bt_excluir = viewAdd.findViewById(R.id.btnExcluir);
        bt_salvar = viewAdd.findViewById(R.id.btnSalvar);
        IDProduto = viewAdd.findViewById(R.id.idProduto);

        text_titulo = viewAdd.findViewById(R.id.etTituloProduto);
        text_desc = viewAdd.findViewById(R.id.etDescricaoProduto);
        text_valor = viewAdd.findViewById(R.id.etValorProduto);

    }

    private void mostrarSnackbar(String mensagem, boolean status, View viewAdd) {
        /*Snackbar snackbar = Snackbar.make(viewAdd, mensagem, Snackbar.LENGTH_SHORT);
        if(status){
            snackbar.setBackgroundTint(Color.BLACK);
            snackbar.setTextColor(Color.GREEN);
        }else{
            snackbar.setBackgroundTint(Color.RED);
            snackbar.setTextColor(Color.WHITE);
        }
        snackbar.show();*/
    }

    private void salvarProdutoFirebase(Produto produto, View  viewAdd) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Adiciona sem ID definido (Firebase gera automaticamente)
        db.collection("Cliente")
                .document(usuarioID)
                .collection("produtos")
                .add(produto)
                .addOnSuccessListener(documentReference -> {
                    // Após salvar, pegue o ID gerado
                    String idGerado = documentReference.getId();
                    produto.setIdProduto(idGerado);

                    // Agora atualize o documento com o ID salvo dentro dele
                    atualizarProdutoFirebase(idGerado, produto, viewAdd);

                    documentReference.set(produto)
                            .addOnSuccessListener(aVoid -> {
                                mostrarSnackbar("Produto salvo com sucesso!", true, viewAdd);
                            })
                            .addOnFailureListener(e -> {
                                mostrarSnackbar("Erro ao atualizar produto com ID", false, viewAdd);
                            });
                })
                .addOnFailureListener(e -> {
                    mostrarSnackbar("Erro ao salvar produto!", false, viewAdd);
                });
    }


    private void atualizarProdutoFirebase(String idDocumento, Produto produto, View viewAdd) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> produtoMap = new HashMap<>();
        produtoMap.put("idProduto", idDocumento);
        produtoMap.put("titulo", produto.getTitulo());
        produtoMap.put("descricao", produto.getDescricao());
        produtoMap.put("valor", produto.getValor());

        db.collection("Cliente")
                .document(usuarioID)
                .collection("produtos")
                .document(idDocumento)
                .set(produtoMap) // substitui os dados do documento
                .addOnSuccessListener(aVoid -> {
                    mostrarSnackbar("Produto atualizado com sucesso!", true, viewAdd);
                })
                .addOnFailureListener(e -> {
                    mostrarSnackbar("Erro ao atualizar produto!", false, viewAdd);
                });
    }

    private void excluirProdutoFirebase(String idProduto, View viewAdd) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Cliente")
                .document(usuarioID)
                .collection("produtos")
                .document(idProduto)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    mostrarSnackbar("Produto excluido com sucesso!", true, viewAdd);
                })
                .addOnFailureListener(e -> {
                    mostrarSnackbar("Erro ao excluir produto!", false, viewAdd);
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}