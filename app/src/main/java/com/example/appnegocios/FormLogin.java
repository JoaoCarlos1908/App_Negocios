package com.example.appnegocios;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FormLogin extends AppCompatActivity {

    private TextView text_tela_cadastro, text_recuperar_senha;
    private EditText edit_email, edit_senha;
    private Button bt_entrar;
    private ProgressBar progressBar;
    String[] menssagens = {"Preencha todos os campos", "Login efetuado com sucesso"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_login);

        IniciarComponentes();

        bt_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveTeclado();
                String email = edit_email.getText().toString();
                String senha = edit_senha.getText().toString();

                if(email.isEmpty() || senha.isEmpty()){
                    Snackbar snackbar = Snackbar.make(v, menssagens[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();
                }else{
                    AutenticarUsuario(v);
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        text_tela_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveTeclado();
                Intent intent = new Intent(FormLogin.this, FormTipoConta.class);
                startActivity(intent);
            }
        });

        text_recuperar_senha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveTeclado();
                recoverPassword(v);
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
    private void recoverPassword(View view){
        String email = edit_email.getText().toString().trim();

        if (email.isEmpty()) {
            Snackbar.make(view, "Digite seu e-mail", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.RED)
                    .setTextColor(Color.WHITE)
                    .show();
            return;
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Snackbar snackbar = Snackbar.make(view, "Se o e-mail estiver cadastrado, você receberá uma mensagem de recuperação", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.GREEN);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else{
                    String erro;
                    try {
                        throw task.getException();
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        erro = "E-mail inválido";
                    }catch(Exception e){
                        erro = "Erro ao realizar recuperação de senha";
                    }
                    Snackbar snackbar = Snackbar.make(view, erro, Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();
                }
            }
        });
    }
    private void AutenticarUsuario(View view){
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.VISIBLE);

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String usuarioID = user.getUid();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    // Primeiro, tenta buscar na coleção "Empresa"
                    db.collection("Empresa").document(usuarioID).get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Usuário é uma empresa
                            new Handler().postDelayed(() -> {
                                Intent intent = new Intent(getApplicationContext(), FormDashboard.class); // Tela de empresa
                                startActivity(intent);
                                finish();
                            }, 3000);

                        } else {
                            // Se não estiver em "Empresa", tenta em "Cliente"
                            db.collection("Cliente").document(usuarioID).get().addOnSuccessListener(docCliente -> {
                                if (docCliente.exists()) {
                                    // Usuário é um cliente
                                    new Handler().postDelayed(() -> {
                                        Intent intent = new Intent(getApplicationContext(), FormTelaPrincipal.class); // Tela de cliente
                                        startActivity(intent);
                                        finish();
                                    }, 3000);
                                } else {
                                    // UID não encontrado em nenhuma coleção
                                    Snackbar.make(view, "Usuário não cadastrado em nenhuma categoria.", Snackbar.LENGTH_SHORT)
                                            .setBackgroundTint(Color.RED)
                                            .setTextColor(Color.WHITE)
                                            .show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
                else {
                    String erro;
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erro = "E-mail ou senha inválido";
                    } catch (Exception e) {
                        erro = "Erro ao realizar login do Usuário";
                    }
                    Snackbar snackbar = Snackbar.make(view, erro, Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();
                }
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            validarUsuarioLogado(user);
        }
    }

    private void validarUsuarioLogado(FirebaseUser user) {

        String usuarioID = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Primeiro tenta verificar se é empresa
        db.collection("Empresa").document(usuarioID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // É uma empresa
                        Intent intent = new Intent(FormLogin.this, FormDashboard.class); // tela da empresa
                        startActivity(intent);
                        finish();
                    } else {
                        // Se não for empresa, verifica se é cliente
                        db.collection("Cliente").document(usuarioID).get()
                                .addOnSuccessListener(docCliente -> {
                                    if (docCliente.exists()) {
                                        // É um cliente
                                        Intent intent = new Intent(FormLogin.this, FormTelaPrincipal.class); // tela do cliente
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Nenhuma categoria encontrada
                                        Toast.makeText(FormLogin.this, "Usuário não classificado como Empresa ou Cliente.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

    }

    private void IniciarComponentes(){
        text_tela_cadastro = findViewById(R.id.text_tela_cadastro);
        text_recuperar_senha = findViewById(R.id.text_recuperar_senha);
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);
        bt_entrar = findViewById(R.id.bt_entrar);
        progressBar = findViewById(R.id.progessbar);
    }
}