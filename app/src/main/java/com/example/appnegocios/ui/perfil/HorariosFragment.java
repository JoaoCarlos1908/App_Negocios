package com.example.appnegocios.ui.perfil;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.appnegocios.R;
import com.example.appnegocios.databinding.FragmentHorariosBinding;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.Locale;

public class HorariosFragment extends Fragment {

    private FragmentHorariosBinding binding;
    private Button bt_editTodos, bt_editSabDom, bt_editSegSex, bt_cancelar, bt_salvar;
    private int hora, minuto;
    private String horario;

    public View onCreateView(@NonNull LayoutInflater inflater,

            ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horarios, container, false);

        iniciarComponentes(view);

        bt_editTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HorariosDialogFragment().show(getParentFragmentManager(), "Dialog");
            }
        });

        return view;
    }

    private void iniciarComponentes(View view){
        bt_editTodos = view.findViewById(R.id.bt_editar_todos);
        bt_editSabDom = view.findViewById(R.id.bt_editar_sabDom);
        bt_editSegSex = view.findViewById(R.id.bt_editar_segSex);
        bt_cancelar = view.findViewById(R.id.bt_cancelar);
        bt_salvar = view.findViewById(R.id.bt_salvar);
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}