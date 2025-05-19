package com.example.appnegocios.ui.perfil;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import java.util.List;
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
    private final Boolean[] diasEdit = {false, false, false, false, false, false, false};
    private Boolean statusAberto, aberto, fechado;
    private CheckBox cb_aberto;
    private CheckBox cb_fechado;
    private HorariosFuncionamento horarios = new HorariosFuncionamento();
    private DiaHorario[] dias = new DiaHorario[7];

    public static HorariosDialogFragment newInstance(Boolean dom, Boolean seg,
                                                     Boolean ter, Boolean qua, Boolean qui, Boolean sex,
                                                     Boolean sab) {
        HorariosDialogFragment fragment = new HorariosDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean("dom",dom);
        args.putBoolean("seg",seg);
        args.putBoolean("ter",ter);
        args.putBoolean("qua",qua);
        args.putBoolean("qui",qui);
        args.putBoolean("sex",sex);
        args.putBoolean("sab",sab);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            aberto = getArguments().getBoolean("aberto", false);
            fechado = getArguments().getBoolean("fechado", false);
            diasEdit[0] = getArguments().getBoolean("dom", false);
            diasEdit[1] = getArguments().getBoolean("seg", false);
            diasEdit[2] = getArguments().getBoolean("ter", false);
            diasEdit[3] = getArguments().getBoolean("qua", false);
            diasEdit[4] = getArguments().getBoolean("qui", false);
            diasEdit[5] = getArguments().getBoolean("sex", false);
            diasEdit[6] = getArguments().getBoolean("sab", false);
        }
        statusAberto = aberto != null && aberto;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_horario, container, false);

        iniciarComponentes(view);

        for (int i = 0; i < diasEdit.length; i++) {
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
                    cb_fechado.setChecked(false);
                    cb_aberto.setChecked(false);
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
                Bundle result = new Bundle();
                result.putBoolean("atualizar", true); // ou outras informações

                getParentFragmentManager().setFragmentResult("horariosAtualizados", result);
                dismiss(); // fecha o dialog
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
        for (int i = 0; i < 7; i++) {
            int finalI = i; // Salva o índice atual de forma fixa
            if (diasEdit[finalI]) {
                diasSemana[finalI].setBackgroundResource(R.drawable.button_check_hr);
                diasEdit[finalI] = true;
            } else {
                diasSemana[finalI].setBackgroundResource(R.drawable.light_button);
                diasEdit[finalI] = false;
            }
        }
        ad_horario = view.findViewById(R.id.tvAdicionarHorario);
        cb_aberto = view.findViewById(R.id.cbAberto24h);
        cb_aberto.setChecked(aberto);
        cb_fechado = view.findViewById(R.id.cbFechado);
        cb_fechado.setChecked(fechado);
        for (int i = 0; i < dias.length; i++) {
            dias[i] = new DiaHorario();
        }
        bt_salvar = view.findViewById(R.id.btnSalvar);
        bt_cancelar = view.findViewById(R.id.btnCancelar);
        recuperarHorarios(horarios);
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
        recuperarHorarios(horarios);
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

        for (DiaSemana dia : DiaSemana.values()) {
            if (diasEdit[dia.ordinal()]) {
                DiaHorario horario = horarios.getHorarioDoDia(dia);

                db.collection("Cliente")
                        .document(usuarioID)
                        .collection("horariosFuncionamento")
                        .document("tabelaHorarios")
                        .update(dia.name(), horario).addOnSuccessListener(aVoid -> {
                            System.out.println("Horários salvos com sucesso!");
                        })
                        .addOnFailureListener(e -> {
                            e.printStackTrace();
                        });
            }
        }
    }

    private void recuperarHorarios(HorariosFuncionamento horarios) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Cliente")
                .document(userId)
                .collection("horariosFuncionamento")
                .document("tabelaHorarios")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        for (DiaSemana dia : DiaSemana.values()) {
                            Map<String, Object> diaMap = (Map<String, Object>) documentSnapshot.get(dia.name());

                            if (diaMap != null) {
                                DiaHorario diaHorario = new DiaHorario();
                                Boolean aberto24h = (Boolean) diaMap.get("aberto24h");
                                Boolean fechado = (Boolean) diaMap.get("fechado");

                                diaHorario.setAberto24h(aberto24h != null && aberto24h);
                                diaHorario.setFechado(fechado != null && fechado);

                                // Recupera os intervalos se não for 24h ou fechado
                                if (!diaHorario.isAberto24h() && !diaHorario.isFechado()) {
                                    List<Map<String, String>> intervalos = (List<Map<String, String>>) diaMap.get("intervalos");
                                    if (intervalos != null) {
                                        for (Map<String, String> intervalo : intervalos) {
                                            String abertura = intervalo.get("horarioAbetura");
                                            String fechamento = intervalo.get("horarioFechamento");
                                            diaHorario.adicionarIntervalo(new HorarioIntervalo(abertura, fechamento));
                                        }
                                    }
                                }

                                horarios.setHorarioDoDia(dia, diaHorario);
                            }
                        }
                        // Agora você tem o objeto `horarios` completo
                    } else {
                        Log.d("Firestore", "Documento não encontrado.");
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    public interface OnDialogCloseListener {
        void onDialogClosed();
    }

    private OnDialogCloseListener listener;

    public void setOnDialogCloseListener(OnDialogCloseListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null) {
            listener.onDialogClosed();
        }
    }


}

