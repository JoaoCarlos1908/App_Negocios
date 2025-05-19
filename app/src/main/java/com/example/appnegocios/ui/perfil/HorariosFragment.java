package com.example.appnegocios.ui.perfil;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Class.Horarios.DiaHorario;
import Class.Horarios.DiaSemana;
import Class.Horarios.HorarioIntervalo;
import Class.Horarios.HorariosFuncionamento;

public class HorariosFragment extends Fragment {

    private FragmentHorariosBinding binding;
    private Button bt_editTodos, bt_editSabDom, bt_editSegSex;
    private ImageView[] editDia = new ImageView[7];
    private TextView[] editText = new TextView[7];
    private HorariosFuncionamento horarios = new HorariosFuncionamento();

    public View onCreateView(@NonNull LayoutInflater inflater,

            ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horarios, container, false);

        iniciarComponentes(view);

        getParentFragmentManager().setFragmentResultListener("horariosAtualizados", this, (requestKey, bundle) -> {
            boolean atualizar = bundle.getBoolean("atualizar", false);
            if (atualizar) {
                recuperarHorarios(horarios); // Chama seu método para atualizar a tela
            }
        });

        bt_editTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HorariosDialogFragment dialog = HorariosDialogFragment.newInstance(
                        true, true, true, true, true, true, true);
                dialog.setOnDialogCloseListener(() -> recuperarHorarios(horarios));
                dialog.show(getParentFragmentManager(), "Dialog");
            }
        });

        bt_editSegSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HorariosDialogFragment dialog = HorariosDialogFragment.newInstance(
                        false, true, true, true, true, true, false);
                dialog.setOnDialogCloseListener(() -> recuperarHorarios(horarios));
                dialog.show(getParentFragmentManager(), "Dialog");
            }
        });

        bt_editSabDom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HorariosDialogFragment dialog = HorariosDialogFragment.newInstance(
                        true, false, false, false, false, false, true);
                dialog.setOnDialogCloseListener(() -> recuperarHorarios(horarios));
                dialog.show(getParentFragmentManager(), "Dialog");
            }
        });

        editDia[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HorariosDialogFragment dialog = HorariosDialogFragment.newInstance(
                        true, false, false, false, false, false, false);
                dialog.setOnDialogCloseListener(() -> recuperarHorarios(horarios));
                dialog.show(getParentFragmentManager(), "Dialog");
            }
        });
        editDia[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HorariosDialogFragment dialog = HorariosDialogFragment.newInstance(
                        false, true, false, false, false, false, false);
                dialog.setOnDialogCloseListener(() -> recuperarHorarios(horarios));
                dialog.show(getParentFragmentManager(), "Dialog");
            }
        });
        editDia[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HorariosDialogFragment dialog = HorariosDialogFragment.newInstance(
                        false, false, true, false, false, false, false);
                dialog.setOnDialogCloseListener(() -> recuperarHorarios(horarios));
                dialog.show(getParentFragmentManager(), "Dialog");
            }
        });
        editDia[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HorariosDialogFragment dialog = HorariosDialogFragment.newInstance(
                        false, false, false, true, false, false, false);
                dialog.setOnDialogCloseListener(() -> recuperarHorarios(horarios));
                dialog.show(getParentFragmentManager(), "Dialog");
            }
        });
        editDia[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HorariosDialogFragment dialog = HorariosDialogFragment.newInstance(
                        false, false, false, false, true, false, false);
                dialog.setOnDialogCloseListener(() -> recuperarHorarios(horarios));
                dialog.show(getParentFragmentManager(), "Dialog");
            }
        });
        editDia[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HorariosDialogFragment dialog = HorariosDialogFragment.newInstance(
                        false, false, false, false, false, true, false);
                dialog.setOnDialogCloseListener(() -> recuperarHorarios(horarios));
                dialog.show(getParentFragmentManager(), "Dialog");
            }
        });
        editDia[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HorariosDialogFragment dialog = HorariosDialogFragment.newInstance(
                        false, false, false, false, false, false, true);
                dialog.setOnDialogCloseListener(() -> recuperarHorarios(horarios));
                dialog.show(getParentFragmentManager(), "Dialog");
            }
        });

        return view;
    }

    private void iniciarComponentes(View view){
        bt_editTodos = view.findViewById(R.id.bt_editar_todos);
        bt_editSabDom = view.findViewById(R.id.bt_editar_sabDom);
        bt_editSegSex = view.findViewById(R.id.bt_editar_segSex);

        editDia[0] = view.findViewById(R.id.edit_dom);
        editDia[1] = view.findViewById(R.id.edit_seg);
        editDia[2] = view.findViewById(R.id.edit_ter);
        editDia[3] = view.findViewById(R.id.edit_qua);
        editDia[4] = view.findViewById(R.id.edit_qui);
        editDia[5] = view.findViewById(R.id.edit_sex);
        editDia[6] = view.findViewById(R.id.edit_sab);

        editText[0] = view.findViewById(R.id.text_domH);
        editText[1] = view.findViewById(R.id.text_segH);
        editText[2] = view.findViewById(R.id.text_terH);
        editText[3] = view.findViewById(R.id.text_quaH);
        editText[4] = view.findViewById(R.id.text_quiH);
        editText[5] = view.findViewById(R.id.text_sexH);
        editText[6] = view.findViewById(R.id.text_sabH);

        recuperarHorarios(horarios);
        preencherHorarios(horarios, editText);
    }

    private void recuperarHorarios(HorariosFuncionamento horarios) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Cliente")
                .document(usuarioID)
                .collection("horariosFuncionamento")
                .document("tabelaHorarios")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> data = documentSnapshot.getData();

                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            String diaStr = entry.getKey();
                            Map<String, Object> diaMap = (Map<String, Object>) entry.getValue();

                            DiaHorario diaHorario = new DiaHorario();
                            diaHorario.setAberto24h((boolean) diaMap.getOrDefault("aberto24h", false));
                            diaHorario.setFechado((boolean) diaMap.getOrDefault("fechado", false));

                            List<Map<String, String>> intervalos = (List<Map<String, String>>) diaMap.get("intervalos");
                            if (intervalos != null) {
                                for (Map<String, String> intervaloMap : intervalos) {
                                    String abertura = intervaloMap.get("horarioAbertura");
                                    String fechamento = intervaloMap.get("horarioFechamento");
                                    diaHorario.adicionarIntervalo(new HorarioIntervalo(abertura, fechamento));
                                }
                            }

                            DiaSemana dia = DiaSemana.valueOf(diaStr);
                            horarios.setHorarioDoDia(dia, diaHorario);
                        }

                        // Agora preenche na UI
                        preencherHorarios(horarios, editText);
                    } else {
                        Log.e("Firestore", "Documento 'tabelaHorarios' não encontrado.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Erro ao buscar horários: " + e.getMessage());
                });
    }

    public void preencherHorarios(HorariosFuncionamento horarios, TextView[] diasTextView) {
        if (diasTextView.length != DiaSemana.values().length) {
            throw new IllegalArgumentException("O array de TextView deve ter exatamente 7 posições, uma para cada dia da semana.");
        }

        for (int i = 0; i < DiaSemana.values().length; i++) {
            DiaHorario diaHorario = horarios.getHorarioDoDia(DiaSemana.values()[i]);

            if (diaHorario.isAberto24h()) {
                diasTextView[i].setText("Aberto 24h");
            } else if (diaHorario.isFechado()) {
                diasTextView[i].setText("Fechado");
            } else {
                List<HorarioIntervalo> intervalos = diaHorario.getIntervalos();
                if (intervalos != null && !intervalos.isEmpty()) {
                    StringBuilder builder = new StringBuilder();
                    for (HorarioIntervalo intervalo : intervalos) {
                        builder.append(intervalo).append("\n");
                    }
                    diasTextView[i].setText(builder.toString().trim());
                } else {
                    diasTextView[i].setText("Sem horário");
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}