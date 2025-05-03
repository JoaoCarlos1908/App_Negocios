package com.example.appnegocios;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import Class.Empreendimento;
import Class.Cliente;

import java.util.HashMap;
import java.util.Map;

public class FormCadastro extends AppCompatActivity {
    private Empreendimento empreendimento;
    private Cliente cliente;
    private EditText edit_nome, edit_desc,edit_email, edit_senha, edit_confirme_senha;
    private TextView text_alterFoto;
    private Button bt_cadastrar;
    private String[] menssagens = {"Preencha todos os campos", "Cadastro realizado com sucesso"};
    private String usuarioID, categoria;
    private ImageView iconUser;
    private Boolean tipoConta;
    private ActivityResultLauncher<Intent> imagePicklauncher;
    private Uri selectedImageUri;
    private AutoCompleteTextView autoCompleteCategorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_cadastro);




        imagePicklauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null){
                            selectedImageUri = data.getData();
                        }
                    }
                }
                );
        IniciarComponentes();

        bt_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveTeclado();

                String nome = edit_nome.getText().toString().trim();
                String email = edit_email.getText().toString().trim();
                String senha = edit_senha.getText().toString();
                String confirmeSenha = edit_confirme_senha.getText().toString();

                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                    mostrarSnackbar(v, menssagens[0], Color.RED);
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


    }//Fim do OnCreate

    private void SelecaoDeCategorias() {
        String[] categorias = getResources().getStringArray(R.array.categorias_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categorias);

        autoCompleteCategorias.setAdapter(adapter);

        // Exibe as opções ao clicar no campo
        autoCompleteCategorias.setOnClickListener(v -> autoCompleteCategorias.showDropDown());

        // Captura a categoria selecionada
        autoCompleteCategorias.setOnItemClickListener((parent, view, position, id) -> {
            categoria = parent.getItemAtPosition(position).toString();
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
            // O URI da imagem estará presente
            Uri uri = data.getData();

            // Use o URI diretamente no ImageView
            iconUser.setImageURI(uri);

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ação cancelada", Toast.LENGTH_SHORT).show();
        }
    }

    private void IniciarComponentes(){
        text_alterFoto = findViewById(R.id.text_alterFoto);
        edit_nome = findViewById(R.id.edit_nome);
        edit_desc = findViewById(R.id.edit_descricao);
        edit_email  = findViewById(R.id.edit_email);
        edit_senha  = findViewById(R.id.edit_senha);
        edit_confirme_senha = findViewById(R.id.edit_confirme_senha);
        bt_cadastrar  = findViewById(R.id.bt_seguir);
        iconUser = findViewById(R.id.iconUser);
        autoCompleteCategorias = findViewById(R.id.autoCompleteCategorias);

        tipoConta = getIntent().getBooleanExtra("Tipo_Conta", false);
        if (!tipoConta) {
            cliente = new Cliente();
            edit_nome.setHint("Nome");
            edit_desc.setHint("CEP");     // altera o hint
            edit_desc.setMaxLines(1);     // altera o número máximo de linhas
            autoCompleteCategorias.setVisibility(View.GONE);
        }else {
            empreendimento = new Empreendimento();
        }

    }
    private void CadastrarUsuario(View v){

        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    SalvarDadosUsuario();

                    Snackbar snackbar = Snackbar.make(v, menssagens[1], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.GREEN);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else {
                    String erro;
                    try {
                        throw task.getException();
                    }catch(FirebaseAuthWeakPasswordException e){
                        erro = "Digite uma senha com o mínimo de 6 caracteres";
                    }catch(FirebaseAuthUserCollisionException e){
                        erro = "E-mail já cadastrado";
                    }catch(FirebaseAuthInvalidCredentialsException e) {
                        erro = "E-mail invalido";
                    }catch(Exception e){
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

    private void RemoveTeclado(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View viewAtual = getCurrentFocus();

        if (viewAtual != null) {
            imm.hideSoftInputFromWindow(viewAtual.getWindowToken(), 0);
        }
    }
    private void SalvarDadosUsuario(){
        if(tipoConta){
            empreendimento.setNome(edit_nome.getText().toString());
            empreendimento.setDescricao(edit_desc.getText().toString());
            empreendimento.setTipoConta(tipoConta);
            empreendimento.setCategoria(categoria);
            empreendimento.setEmail(edit_email.getText().toString());

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> empresa = new HashMap<>();
            empresa.put("nome",empreendimento.getNome());
            empresa.put("descrição", empreendimento.getDescricao());
            empresa.put("telefone", empreendimento.getTell());
            empresa.put("TipoConta", empreendimento.getTipoConta());
            empresa.put("categoria", empreendimento.getCategoria());
            empresa.put("E-mail", empreendimento.getEmail());
            empresa.put("Endereço", empreendimento.getEndereco());

            usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DocumentReference documentReference = db.collection("Empresa").document(usuarioID);
            documentReference.set(empresa).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("db","Sucesso ao salvar os dados");

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("db_erro","Erro ao salvar os dados" + e.toString());
                        }
                    });
        }else{
            cliente.setNome(edit_nome.getText().toString());
            cliente.setTipoConta(tipoConta);
            cliente.setCep(edit_desc.getText().toString());
            cliente.setEmail(edit_email.getText().toString());

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> user = new HashMap<>();
            user.put("nome",cliente.getNome());
            user.put("telefone", cliente.getTell());
            user.put("TipoConta", cliente.getTipoConta());
            user.put("E-mail", cliente.getEmail());
            user.put("CEP", cliente.getCep());

            usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DocumentReference documentReference = db.collection("Cliente").document(usuarioID);
            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("db","Sucesso ao salvar os dados");

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("db_erro","Erro ao salvar os dados" + e.toString());
                        }
                    });
        }

        Intent intent = new Intent(FormCadastro.this, FormDashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}