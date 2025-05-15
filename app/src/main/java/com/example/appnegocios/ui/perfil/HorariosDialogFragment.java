package com.example.appnegocios.ui.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.appnegocios.R;

public class HorariosDialogFragment extends DialogFragment {

    public interface OnHorariosSalvosListener {
        void onHorariosSalvos(String abertura, String fechamento, boolean aberto24h, boolean fechado);
    }

    private OnHorariosSalvosListener listener;

    public void setOnHorariosSalvosListener(OnHorariosSalvosListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_horario, container, false);




        EditText etAbertura = view.findViewById(R.id.etAbertura);
        EditText etFechamento = view.findViewById(R.id.etFechamento);
        CheckBox cbAberto24h = view.findViewById(R.id.cbAberto24h);
        CheckBox cbFechado = view.findViewById(R.id.cbFechado);
        Button btnSalvar = view.findViewById(R.id.btnSalvar);
        Button btnCancelar = view.findViewById(R.id.btnCancelar);

        btnCancelar.setOnClickListener(v -> dismiss());

        btnSalvar.setOnClickListener(v -> {
            String abertura = etAbertura.getText().toString();
            String fechamento = etFechamento.getText().toString();
            boolean aberto24h = cbAberto24h.isChecked();
            boolean fechado = cbFechado.isChecked();

            if (listener != null) {
                listener.onHorariosSalvos(abertura, fechamento, aberto24h, fechado);
            }

            dismiss();
        });

        return view;
    }

    private void iniciarComponentes(View view){

    }

}
