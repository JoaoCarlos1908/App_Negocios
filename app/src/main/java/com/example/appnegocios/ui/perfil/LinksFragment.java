package com.example.appnegocios.ui.perfil;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.appnegocios.R;
import com.example.appnegocios.databinding.FragmentLinksBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import Class.Links;

public class LinksFragment extends Fragment {

private FragmentLinksBinding binding;
private EditText nome, link;
private Button bt_adicionar;
private FirebaseFirestore db = FirebaseFirestore.getInstance();
private String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        // Infla o layout XML do fragmento
        View view = inflater.inflate(R.layout.fragment_links, container, false);

        mostrarLinks(view);
        iniciarComponentes(view);

        bt_adicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nome.getText().toString().trim().isEmpty()){
                    Snackbar snackbar = Snackbar.make(view, "Informe o nome do Link", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();
                } else if (link.getText().toString().trim().isEmpty()) {
                    Snackbar snackbar = Snackbar.make(view, "Informe o link de acesso.", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();
                }else {
                    // Cria o documento de contato
                    Links novoLink = new Links();
                    novoLink.setNomeLink(nome.getText().toString());
                    novoLink.setLink(link.getText().toString());

                    if(isLinkValido(novoLink.getLink())){
                        // Adiciona na subcoleção 'contatos' dentro do usuário 'abc123'
                        db.collection("Cliente")
                                .document(usuarioID)
                                .collection("links")
                                .add(novoLink)
                                .addOnSuccessListener(docRef -> Log.d("FIREBASE", "Link adicionado com ID: " + docRef.getId()))
                                .addOnFailureListener(e -> Log.e("FIREBASE", "Erro ao adicionar link", e));

                        mostrarLinks(view);
                    }else{
                        Snackbar snackbar = Snackbar.make(view, "Link inválido ou inseguro", Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(Color.RED);
                        snackbar.setTextColor(Color.WHITE);
                        snackbar.show();
                    }
                }
            }
        });

        return view;
    }

    private void iniciarComponentes(View view){
        nome = view.findViewById(R.id.edit_nome_link);
        link = view.findViewById(R.id.edit_link);
        bt_adicionar = view.findViewById(R.id.bt_adicionar);
    }

    private void mostrarLinks(View view) {
        db.collection("Cliente")
                .document(usuarioID)
                .collection("links")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    LinearLayout containerInteracoes = view.findViewById(R.id.containerLinks);
                    containerInteracoes.removeAllViews(); // Evita duplicação

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Links links = doc.toObject(Links.class);

                        if (links != null) {
                            View item = getLayoutInflater().inflate(R.layout.layout_view_links, containerInteracoes, false);

                            TextView text_nome = item.findViewById(R.id.text_link);
                            TextView text_link = item.findViewById(R.id.bt_acessar_link);
                            TextView url_link = item.findViewById(R.id.url_link);
                            TextView id_link = item.findViewById(R.id.id_link);
                            TextView bt_excluir = item.findViewById(R.id.bt_excluir);

                            url_link.setText(links.getLink());
                            text_nome.setText(links.getNomeLink());
                            text_link.setText(resumirLink(links.getLink()));
                            id_link.setText(doc.getId());

                            bt_excluir.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    db.collection("Cliente")
                                            .document(usuarioID)
                                            .collection("links")
                                            .document(id_link.getText().toString()) // ID do documento do contato
                                            .delete()
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("FIREBASE", "Link excluído com sucesso!");
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("FIREBASE", "Erro ao excluir link", e);
                                            });
                                    mostrarLinks(view);
                                }
                            });

                            text_link.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String url = url_link.getText().toString();

                                    // Garante que o link tem "https://" no início
                                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                        url = "http://" + url;
                                    }

                                    if(isLinkValido(url)){
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(url));
                                        startActivity(intent);
                                    }else{
                                        Snackbar snackbar = Snackbar.make(view, "Link inválido ou inseguro", Snackbar.LENGTH_SHORT);
                                        snackbar.setBackgroundTint(Color.RED);
                                        snackbar.setTextColor(Color.WHITE);
                                        snackbar.show();
                                    }
                                }
                            });
                            containerInteracoes.addView(item);
                        }
                    }
                });
    }

    public boolean isLinkValido(String urlCliente) {
        if (urlCliente == null || urlCliente.trim().isEmpty()) {
            return false;
        }

        String url = urlCliente.trim();

        // Adiciona http:// se não tiver nenhum esquema
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }

        // Verifica se bate com o padrão de URLs do Android
        if (!Patterns.WEB_URL.matcher(url).matches()) {
            return false;
        }

        // Garante que o esquema é seguro (http ou https)
        Uri uri = Uri.parse(url);
        String esquema = uri.getScheme();

        return esquema != null && (esquema.equals("http") || esquema.equals("https"));
    }

    public String resumirLink(String urlCompleta) {
        if (urlCompleta == null || urlCompleta.trim().isEmpty()) {
            return "";
        }

        // Adiciona http:// se estiver faltando para o parser funcionar
        if (!urlCompleta.startsWith("http://") && !urlCompleta.startsWith("https://")) {
            urlCompleta = "http://" + urlCompleta;
        }

        try {
            Uri uri = Uri.parse(urlCompleta);
            String host = uri.getHost();

            // Remove o "www." se existir
            if (host != null && host.startsWith("www.")) {
                host = host.substring(4);
            }

            return host != null ? host : "";
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}