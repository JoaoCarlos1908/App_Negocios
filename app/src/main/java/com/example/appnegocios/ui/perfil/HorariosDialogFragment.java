package com.example.appnegocios.ui.perfil;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.appnegocios.R;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import Class.Horarios.DiaHorario;
import Class.Horarios.DiaSemana;
import Class.Horarios.HorarioIntervalo;
import Class.Horarios.HorariosFuncionamento;

public class HorariosDialogFragment extends DialogFragment {

    private Button[] diasSemana = new Button[7];
    private Button bt_salvar, bt_cancelar;
    private TextView ad_horario;
    private Boolean[] diasEdit = new Boolean[7];
    private Boolean statusAberto;
    private CheckBox cb_aberto, cb_fechado;
    private HorariosFuncionamento horarios = new HorariosFuncionamento();
    private DiaHorario[] dias = new DiaHorario[7];

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

        iniciarComponentes(view);

        for (int i = 0; i < 7; i++) {
            int finalI = i; // Salva o índice atual de forma fixa
            diasSemana[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (diasEdit[finalI]) {
                        diasEdit[finalI] = false;
                        diasSemana[finalI].setBackgroundResource(R.drawable.light_button);
                    } else {
                        diasEdit[finalI] = true;
                        diasSemana[finalI].setBackgroundResource(R.drawable.button_check_hr);
                    }
                }
            });
        }

        ad_horario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout conteinerHorarios = view.findViewById(R.id.conteinerHorarios);
                if (conteinerHorarios.getChildCount() < 6) {
                    View item = getLayoutInflater().inflate(R.layout.layout_view_horarios, conteinerHorarios, false);

                    EditText etAbertura = item.findViewById(R.id.etAbertura);
                    EditText etFechamento = item.findViewById(R.id.etFechamento);
                    TextView btRemover = item.findViewById(R.id.bt_remover);

                    etAbertura.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            abrirMaterialTimePicker(etAbertura);
                        }
                    });

                    etFechamento.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            abrirMaterialTimePicker(etFechamento);
                        }
                    });

                    btRemover.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            conteinerHorarios.removeView(item);
                        }
                    });


                    conteinerHorarios.addView(item);
                } else {
                    Toast.makeText(getContext(), "Máximo de 6 horários atingido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cb_aberto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_fechado.setChecked(false);
                statusAberto = true;
                LinearLayout conteinerHorarios = view.findViewById(R.id.conteinerHorarios);
                conteinerHorarios.removeAllViews();
            }
        });

        cb_fechado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_aberto.setChecked(false);
                statusAberto = false;
                LinearLayout conteinerHorarios = view.findViewById(R.id.conteinerHorarios);
                conteinerHorarios.removeAllViews();
            }
        });

        bt_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        bt_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarHorarios(view);
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    (int) (Resources.getSystem().getDisplayMetrics().widthPixels * 0.9),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }


    private void iniciarComponentes(View view) {
        diasSemana[0] = view.findViewById(R.id.bt_dom);
        diasSemana[1] = view.findViewById(R.id.bt_seg);
        diasSemana[2] = view.findViewById(R.id.bt_ter);
        diasSemana[3] = view.findViewById(R.id.bt_qua);
        diasSemana[4] = view.findViewById(R.id.bt_qui);
        diasSemana[5] = view.findViewById(R.id.bt_sex);
        diasSemana[6] = view.findViewById(R.id.bt_sab);
        for (int i = 0; i < diasEdit.length; i++) {
            diasEdit[i] = false;
        }

        ad_horario = view.findViewById(R.id.tvAdicionarHorario);
        cb_aberto = view.findViewById(R.id.cbAberto24h);
        cb_fechado = view.findViewById(R.id.cbFechado);

        for (int i = 0; i < dias.length; i++) {
            dias[i] = new DiaHorario();
        }

        bt_salvar = view.findViewById(R.id.btnSalvar);
        bt_cancelar = view.findViewById(R.id.btnCancelar);
    }

    private void abrirMaterialTimePicker(EditText viewHora) {
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setHour(8)
                .setMinute(0)
                .build();

        // Esta linha é CRUCIAL!
        picker.show(requireActivity().getSupportFragmentManager(), "TIME_PICKER");

        picker.addOnPositiveButtonClickListener(dialog -> {
            int hora = picker.getHour();
            int minuto = picker.getMinute();
            String horario = String.format(Locale.getDefault(), "%02d:%02d", hora, minuto);
            viewHora.setText(horario);
        });
    }

    private void salvarHorarios(View view) {

        for (int i = 0; i < dias.length; i++) {
            if (diasEdit[i]) {
                if (cb_aberto.isChecked() || cb_fechado.isChecked()) {
                    if (statusAberto) {
                        dias[i].setAberto24h(true);
                        horarios.setHorarioDoDia(DiaSemana.values()[i], dias[i]);
                    } else {
                        dias[i].setFechado(true);
                        horarios.setHorarioDoDia(DiaSemana.values()[i], dias[i]);
                    }
                } else {
                    LinearLayout conteinerHorarios = view.findViewById(R.id.conteinerHorarios);

                    for (int x = 0; x < conteinerHorarios.getChildCount(); x++) {
                        View itemHorario = conteinerHorarios.getChildAt(x);

                        EditText etAbertura = itemHorario.findViewById(R.id.etAbertura);
                        EditText etFechamento = itemHorario.findViewById(R.id.etFechamento);

                        String abertura = etAbertura.getText().toString();
                        String fechamento = etFechamento.getText().toString();

                        if (!abertura.isEmpty() && !fechamento.isEmpty()) {
                            dias[i].adicionarIntervalo(new HorarioIntervalo(abertura, fechamento));
                        }

                    }
                    horarios.setHorarioDoDia(DiaSemana.values()[i], dias[i]);
                }
                dias[i] = new DiaHorario(); // limpa antes de configurar de novo
            }
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> horariosMap = new HashMap<>();

        // Converte EnumMap em Map<String, DiaHorario>
        for (DiaSemana dia : DiaSemana.values()) {
            DiaHorario horario = horarios.getHorarioDoDia(dia);
            horariosMap.put(dia.name(), horario); // chave como "SEGUNDA", "TERCA" etc.
        }

        db.collection("Cliente")
                .document(usuarioID)
                .collection("horariosFuncionamento")
                .document("tabelaHorarios")
                .set(horariosMap)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Horários salvos com sucesso!");
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

}
