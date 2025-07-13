package com.example.appnegocios;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;


import Class.Empreendimento;
import Class.Cliente;
import Class.Contato;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormCadastro extends AppCompatActivity {
    private Empreendimento empreendimento;
    private Cliente cliente;
    private EditText edit_nome, edit_desc, edit_cep, edit_email, edit_confirme_email, edit_senha, edit_confirme_senha;
    private TextInputLayout layout_senha, layout_confirme_senha;
    private TextView text_alterFoto;
    private Button bt_seguir;
    private String[] menssagens = {"Preencha todos os campos", "Cadastro realizado com sucesso"};
    private String usuarioID, categoria;
    private ImageView iconUser;
    private Boolean tipoConta,control;
    private ActivityResultLauncher<Intent> imagePicklauncher;
    private Uri selectedImageUri;
    private AutoCompleteTextView autoCompleteCategorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tipoConta = getIntent().getBooleanExtra("Tipo_Conta", false);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_cadastro);

        imagePicklauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                        }
                    }
                });
        IniciarComponentes();

        bt_seguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveTeclado();

                String nome = edit_nome.getText().toString().trim();
                String cep = edit_cep.getText().toString().trim();
                String email = edit_email.getText().toString().trim();
                String confirmeEmail = edit_confirme_email.getText().toString().trim();
                String senha = edit_senha.getText().toString();
                String confirmeSenha = edit_confirme_senha.getText().toString();

                if (nome.isEmpty()) {
                    mostrarSnackbar(v, "Informe o nome do empreendimento", Color.RED);
                    return;
                }
                if(cep.isEmpty()) {
                    mostrarSnackbar(v, "Informe seu CEP", Color.RED);
                    return;
                }
                if (!control){
                    edit_nome.setVisibility(View.GONE);
                    edit_desc.setVisibility(View.GONE);
                    edit_cep.setVisibility(View.GONE);
                    autoCompleteCategorias.setVisibility(View.GONE);

                    edit_email.setVisibility(View.VISIBLE);
                    edit_confirme_email.setVisibility(View.VISIBLE);
                    layout_senha.setVisibility(View.VISIBLE);
                    layout_confirme_senha.setVisibility(View.VISIBLE);
                }

                if(control){
                    if (email.isEmpty() || senha.isEmpty()) {
                        mostrarSnackbar(v, menssagens[0], Color.RED);
                        return;
                    }

                    if (confirmeEmail.isEmpty()) {
                        mostrarSnackbar(v, "Confirme sua senha", Color.RED);
                        return;
                    }

                    if (!email.equals(confirmeEmail)) {
                        mostrarSnackbar(v, "Os Emails não coincidem", Color.RED);
                        return;
                    }

                    if (confirmeSenha.isEmpty()) {
                        mostrarSnackbar(v, "Confirme sua senha", Color.RED);
                        return;
                    }

                    if (!senha.equals(confirmeSenha)) {
                        mostrarSnackbar(v, "As senhas não coincidem", Color.RED);
                        return;
                    }

                    // Todos os campos estão preenchidos corretamente
                    CadastrarUsuario(v);
                }
                control = true;
            }
        });

        iconUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(FormCadastro.this)
                        .cropSquare() // <-- Isso força o corte 1:1 (quadrado)
                        .compress(1024) // tamanho máximo em KB
                        .maxResultSize(1080, 1080) // resolução máxima
                        .start();
            }
        });

        text_alterFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(FormCadastro.this)
                        .cropSquare() // <-- Isso força o corte 1:1 (quadrado)
                        .compress(1024) // tamanho máximo em KB
                        .maxResultSize(1080, 1080) // resolução máxima
                        .start();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SelecaoDeCategorias();

        autoCompleteCategorias.postDelayed(() -> autoCompleteCategorias.showDropDown(), 500);


    }//Fim do OnCreate

    private void IniciarComponentes() {
        text_alterFoto = findViewById(R.id.text_alterFoto);
        edit_nome = findViewById(R.id.edit_nome);
        edit_desc = findViewById(R.id.edit_descricao);
        edit_cep = findViewById(R.id.edit_cep);
        edit_confirme_email = findViewById(R.id.edit_confirme_email);
        edit_email = findViewById(R.id.edit_email);
        layout_senha = findViewById(R.id.layout_senha);
        layout_confirme_senha = findViewById(R.id.layout_confirme_senha);
        edit_senha = findViewById(R.id.edit_senha);
        edit_confirme_senha = findViewById(R.id.edit_confirme_senha);
        bt_seguir = findViewById(R.id.bt_seguir);
        iconUser = findViewById(R.id.iconUser);
        autoCompleteCategorias = findViewById(R.id.autoCompleteCategorias);

        if (!tipoConta) {
            cliente = new Cliente();
            edit_nome.setHint("Nome");
            edit_desc.setHint("Idade");     // altera o hint
            edit_desc.setMaxLines(1);
            autoCompleteCategorias.setVisibility(View.GONE);
        } else {
            empreendimento = new Empreendimento();
        }
        control = false;
    }

    private void SelecaoDeCategorias() {
        buscarCategorias(categorias -> {
            if (categorias != null && !categorias.isEmpty()) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        categorias
                );
                //Toast.makeText(FormCadastro.this, "Categorias: " + categorias.toString(), Toast.LENGTH_LONG).show();
                autoCompleteCategorias.setAdapter(adapter);

                autoCompleteCategorias.setOnClickListener(v -> autoCompleteCategorias.showDropDown());

                autoCompleteCategorias.setOnItemClickListener((parent, view, position, id) -> {
                    categoria = parent.getItemAtPosition(position).toString();
                });
            } else {
                Log.d("CATEGORIAS", "Nenhuma categoria encontrada.");
                Toast.makeText(FormCadastro.this, "Categorias: " + categorias.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(FormCadastro.this, "Nenhuma categoria encontrada.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface FirebaseCallback {
        void onCallback(List<String> categorias);
    }

    private void buscarCategorias(FirebaseCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("categorias")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> ids = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        ids.add(doc.getId());
                    }

                    Log.d("FIREBASE", "Categorias encontradas: " + ids);
                    callback.onCallback(ids);
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE", "Erro ao buscar categorias", e);
                    callback.onCallback(new ArrayList<>()); // Retorna lista vazia em caso de erro
                });
    }

    // Método auxiliar para exibir Snackbar
    private void mostrarSnackbar(View view, String mensagem, int corFundo) {
        Snackbar snackbar = Snackbar.make(view, mensagem, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(corFundo);
        snackbar.setTextColor(Color.WHITE);
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                selectedImageUri = uri; // <- Atualiza aqui!
                iconUser.setImageURI(uri);
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ação cancelada", Toast.LENGTH_SHORT).show();
        }
    }


    private void CadastrarUsuario(View v) {

        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    SalvarDadosUsuario();

                    Snackbar snackbar = Snackbar.make(v, menssagens[1], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.GREEN);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                } else {
                    String erro;
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        erro = "Digite uma senha com o mínimo de 6 caracteres";
                    } catch (FirebaseAuthUserCollisionException e) {
                        erro = "E-mail já cadastrado";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erro = "E-mail invalido";
                    } catch (Exception e) {
                        erro = "Erro ao cadastrar usuário";
                    }

                    Snackbar snackbar = Snackbar.make(v, erro, Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();
                }
            }
        });
    }

    private void RemoveTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View viewAtual = getCurrentFocus();

        if (viewAtual != null) {
            imm.hideSoftInputFromWindow(viewAtual.getWindowToken(), 0);
        }
    }

    private void SalvarDadosUsuario() {
        if (tipoConta) {
            empreendimento.setNome(edit_nome.getText().toString());
            empreendimento.setDescricao(edit_desc.getText().toString());
            empreendimento.setCep(edit_cep.getText().toString());
            empreendimento.setTipoConta(tipoConta);
            empreendimento.setCategoria(categoria);
            empreendimento.setEmail(edit_email.getText().toString());

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> empresa = new HashMap<>();
            empresa.put("nome", empreendimento.getNome());
            empresa.put("descricao", empreendimento.getDescricao());
            empresa.put("CEP", empreendimento.getCep());
            empresa.put("TipoConta", empreendimento.getTipoConta());
            empresa.put("categoria", empreendimento.getCategoria());
            empresa.put("E-mail", empreendimento.getEmail());
            empresa.put("endereco", empreendimento.getEndereco());

            usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DocumentReference documentReference = db.collection("Cliente").document(usuarioID);

            // Cria o documento de contato
            Contato novoContato = new Contato();
            novoContato.setTipo("E-mail");
            novoContato.setContato(empreendimento.getEmail());

            // Adiciona na subcoleção 'contatos' dentro do usuário 'abc123'
            db.collection("Cliente")
                    .document(usuarioID)
                    .collection("contatos")
                    .add(novoContato)
                    .addOnSuccessListener(docRef -> Log.d("FIREBASE", "Contato adicionado com ID: " + docRef.getId()))
                    .addOnFailureListener(e -> Log.e("FIREBASE", "Erro ao adicionar contato", e));

            documentReference.set(empresa)
                    .addOnSuccessListener(unused -> {
                        Log.d("db", "Sucesso ao salvar os dados");
                    })
                    .addOnFailureListener(e -> {
                        Log.d("db_erro", "Erro ao salvar os dados" + e.toString());
                    });

        } else {
            cliente.setNome(edit_nome.getText().toString());
            cliente.setTipoConta(tipoConta);
            cliente.setIdade(Integer.parseInt(edit_desc.getText().toString()));
            cliente.setCep(edit_cep.getText().toString());
            cliente.setEmail(edit_email.getText().toString());

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> user = new HashMap<>();
            user.put("nome", cliente.getNome());
            user.put("idade", cliente.getIdade());
            user.put("telefone", cliente.getTell());
            user.put("TipoConta", cliente.getTipoConta());
            user.put("E-mail", cliente.getEmail());
            user.put("CEP", cliente.getCep());

            usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DocumentReference documentReference = db.collection("Cliente").document(usuarioID);
            documentReference.set(user)
                    .addOnSuccessListener(unused -> {
                        Log.d("db", "Sucesso ao salvar os dados");
                    })
                    .addOnFailureListener(e -> {
                        Log.d("db_erro", "Erro ao salvar os dados" + e.toString());
                    });
        }

        salvarImagemPerfil(usuarioID);

    }

    private void salvarImagemPerfil(String userId) {
        if (selectedImageUri == null) {
            Log.e("FIREBASE", "selectedImageUri está nulo! Não há imagem para enviar.");
            return;
        }

        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference()
                .child("fotos_perfil/" + userId + ".jpg");

        storageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        Log.d("FIREBASE", "Imagem enviada. URL: " + imageUrl);

                        FirebaseFirestore.getInstance()
                                .collection("Cliente")
                                .document(userId)
                                .update("fotoPerfil", imageUrl)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("FIREBASE", "fotoPerfil atualizado com sucesso");
                                    avisoVerificacaoEmail(); // <- mover para aqui
                                })
                                .addOnFailureListener(e -> Log.e("FIREBASE", "Erro ao salvar fotoPerfil", e));
                    });
                })
                .addOnFailureListener(e -> Log.e("FIREBASE", "Falha ao fazer upload da imagem", e));
    }

    private void avisoVerificacaoEmail(){
        AlertDialog.Builder builder = new AlertDialog.Builder(FormCadastro.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_verificacao_email, null);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !user.isEmailVerified()) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "E-mail de verificação enviado.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        Button btnReenviarEmail = view.findViewById(R.id.btnReenviarEmail);
        Button btnFechar = view.findViewById(R.id.btnFechar);

        AlertDialog dialog = builder.setView(view).create();

        btnReenviarEmail.setOnClickListener(v -> {
            if (user != null && !user.isEmailVerified()) {
                user.sendEmailVerification()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "E-mail de verificação reenviado.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        btnFechar.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(FormCadastro.this, FormLogin.class));
            finish();
            dialog.dismiss();
        });

        dialog.show();
    }

}