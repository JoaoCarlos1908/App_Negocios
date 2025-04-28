package com.example.appnegocios;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

public class FormTipoConta extends AppCompatActivity {


    private CheckBox cb_empreendimento, cb_cliente;
    private TextView desc_conta;
    private Button bt_seguir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_tipo_conta);

        IniciarComponentes();

        cb_empreendimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb_empreendimento.isChecked()){
                    cb_cliente.setChecked(false);
                    desc_conta.setText(getText(R.string.descricao_conta_empreendedora));
                } else{
                    desc_conta.setText(getText(R.string.aviso_tipoConta));
                }
            }
        });

        cb_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb_cliente.isChecked()){
                    cb_empreendimento.setChecked(false);
                    desc_conta.setText(getText(R.string.descricao_conta_cliente));
                } else{
                    desc_conta.setText(getText(R.string.aviso_tipoConta));
                }
            }
        });

        bt_seguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_empreendimento.isChecked() && !cb_cliente.isChecked()){
                    Snackbar snackbar = Snackbar.make(v, "Selecione uma das opções para continuar", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();
                }else{
                    Intent intent = new Intent(FormTipoConta.this, FormCadastro.class);
                    startActivity(intent);
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void IniciarComponentes(){
        cb_empreendimento = findViewById(R.id.cb_empreendedora);
        cb_cliente = findViewById(R.id.cb_cliente);
        desc_conta = findViewById(R.id.text_descricao_conta);
        bt_seguir = findViewById(R.id.bt_seguir);
    }

}