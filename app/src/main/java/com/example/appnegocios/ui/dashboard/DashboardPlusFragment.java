package com.example.appnegocios.ui.dashboard;

import static com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.LEFT;
import static com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.RIGHT;
import static com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL;
import static com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.CENTER;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.appnegocios.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardPlusFragment extends Fragment {

    private View view;
    private LineChart lineChart;
    private TextView quant_cliks, quant_views, quant_avaliacoes, quant_reclamacoes;
    private DocumentReference documentReference;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_dashboard_plus, container, false);
        lineChart = view.findViewById(R.id.lineChart);

        iniciarComponentes();

        //Exibir visualizações e clicks contatos
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    Long views = documentSnapshot.getLong("views");
                    if (views != null) {
                        quant_views.setText(String.valueOf(views));
                    } else {
                        quant_views.setText("0");
                    }

                    Long clicksContatos = documentSnapshot.getLong("clicksContatos");
                    if (clicksContatos != null) {
                        quant_cliks.setText(String.valueOf(clicksContatos));
                    } else {
                        quant_cliks.setText("0");
                    }

                }
            }
        });
        exibirTaxasAvaliacoesReclamacoes();

        carregarDadosUltimos7Dias(usuarioID, (dias, visualizacoes, cliques, avaliacoes) -> {
            // Eixo X
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    int index = (int) value;
                    if (index >= 0 && index < dias.length) {
                        return dias[index];
                    } else {
                        return "";
                    }
                }
            });
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setDrawGridLines(false);

            // Visualizações
            LineDataSet dataSetVisualizacoes = new LineDataSet(visualizacoes, "Visualizações");
            dataSetVisualizacoes.setColor(Color.parseColor("#3F51B5"));
            dataSetVisualizacoes.setCircleColor(Color.parseColor("#3F51B5"));
            dataSetVisualizacoes.setDrawFilled(true);
            dataSetVisualizacoes.setFillColor(Color.parseColor("#99CCFF"));
            dataSetVisualizacoes.setValueTextSize(12f); // ou 16f, conforme necessário

            // Cliques
            LineDataSet dataSetCliques = new LineDataSet(cliques, "Cliques");
            dataSetCliques.setColor(Color.parseColor("#4CAF50"));
            dataSetCliques.setCircleColor(Color.parseColor("#4CAF50"));
            dataSetCliques.setDrawFilled(true);
            dataSetCliques.setFillColor(Color.parseColor("#A5D6A7"));
            dataSetCliques.setValueTextSize(12f);

            // Avaliações
            LineDataSet dataSetAvaliacoes = new LineDataSet(avaliacoes, "Avaliações");
            dataSetAvaliacoes.setColor(Color.parseColor("#FF9800"));
            dataSetAvaliacoes.setCircleColor(Color.parseColor("#FF9800"));
            dataSetAvaliacoes.setDrawFilled(true);
            dataSetAvaliacoes.setFillColor(Color.parseColor("#FFE0B2"));
            dataSetAvaliacoes.setValueTextSize(12f);

            // Junta tudo
            LineData lineData = new LineData(dataSetVisualizacoes, dataSetCliques, dataSetAvaliacoes);
            lineChart.setData(lineData);
            lineChart.getDescription().setText("Engajamento no perfil");
            lineChart.getDescription().setTextColor(Color.DKGRAY);
            lineChart.getDescription().setTextSize(12f);
            lineChart.animateX(1000);

            Legend legend = lineChart.getLegend();
            legend.setEnabled(true);
            legend.setTextSize(12f);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            legend.setForm(Legend.LegendForm.LINE);
            legend.setXEntrySpace(20f); // espaçamento horizontal entre os itens
            legend.setYEntrySpace(5f);  // espaçamento vertical entre linhas (se tiver)
        });


        PieChart pieChart = view.findViewById(R.id.pieChart);
        carregarDadosAvaliacoes(usuarioID, pieChart);

        View avali = view.findViewById(R.id.conteinerAvaliacoes);
        avali.setOnClickListener(v -> {

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
            // Limpa a pilha até o início antes de navegar
            navController.popBackStack(R.id.nav_dashboard, true); // ou o fragmento anterior que você quer remover
            navController.navigate(R.id.nav_avaliacoes);
        });

        View recla = view.findViewById(R.id.conteinerReclamacoes);
        recla.setOnClickListener(v -> {

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);

            // Limpa a pilha até o início antes de navegar
            navController.popBackStack(R.id.nav_dashboard, true); // ou o fragmento anterior que você quer remover
            navController.navigate(R.id.nav_reclamacoes);
        });

        return view;
    }

    private void iniciarComponentes() {
        quant_cliks = view.findViewById(R.id.quant_cliks);
        quant_views = view.findViewById(R.id.quant_views);
        quant_avaliacoes = view.findViewById(R.id.quant_avaliacoes);
        quant_reclamacoes = view.findViewById(R.id.quant_reclamacoes);
        documentReference = db.collection("Cliente").document(usuarioID);
    }

    private void exibirTaxasAvaliacoesReclamacoes() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Primeiro: calcular média de avaliações
        db.collection("Avaliacoes")
                .whereEqualTo("idEmpreendimento", usuarioID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalEstrelas = 0;
                    int totalAvaliacoes = 0;

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Long estrelas = doc.getLong("estrelas");

                        if (estrelas != null) {
                            totalEstrelas += estrelas;
                            totalAvaliacoes++;
                        }
                    }
                    quant_avaliacoes.setText(Integer.toString(totalAvaliacoes));
                })
                .addOnFailureListener(e -> {
                    Log.e("ERRO", "Erro ao buscar avaliações", e);
                });

        // Segundo: calcular porcentagem de reclamações respondidas
        db.collection("Reclamacoes")
                .whereEqualTo("idEmpreendimento", usuarioID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalReclamacoes = 0;
                    int reclamacoesRespondidas = 0;

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Boolean respondida = doc.getBoolean("respondida");

                        if (respondida != null) {
                            totalReclamacoes++;
                            if (respondida) {
                                reclamacoesRespondidas++;
                            }
                        }
                    }
                    quant_reclamacoes.setText(Integer.toString(totalReclamacoes));
                })
                .addOnFailureListener(e -> {
                    Log.e("ERRO", "Erro ao buscar reclamações", e);
                });
    }

    public static void carregarDadosUltimos7Dias(String idUser, ChartDataCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference viewsRef = db.collection("Cliente").document(idUser).collection("dadosSemana");

        viewsRef.get().addOnSuccessListener(querySnapshot -> {
            List<DocumentSnapshot> documentos = querySnapshot.getDocuments();

            // Ordenar por ID (formato yyyy-MM-dd)
            Collections.sort(documentos, Comparator.comparing(DocumentSnapshot::getId));

            int totalDocs = documentos.size();
            int inicio = Math.max(0, totalDocs - 7);
            List<DocumentSnapshot> ultimosSete = documentos.subList(inicio, totalDocs);

            List<Entry> visualizacoes = new ArrayList<>();
            List<Entry> cliques = new ArrayList<>();
            List<Entry> avaliacoes = new ArrayList<>();
            String[] dias = new String[ultimosSete.size()];

            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat formatoSaida = new SimpleDateFormat("dd/MM", Locale.getDefault());

            for (int i = 0; i < ultimosSete.size(); i++) {
                DocumentSnapshot doc = ultimosSete.get(i);
                String idData = doc.getId();

                long v = doc.getLong("contador") != null ? doc.getLong("contador") : 0;
                long c = doc.getLong("cliques") != null ? doc.getLong("cliques") : 0;
                long a = doc.getLong("avaliacoes") != null ? doc.getLong("avaliacoes") : 0;

                try {
                    Date data = formatoEntrada.parse(idData);
                    dias[i] = formatoSaida.format(data);
                } catch (ParseException e) {
                    dias[i] = idData;
                }

                visualizacoes.add(new Entry(i, v));
                cliques.add(new Entry(i, c));
                avaliacoes.add(new Entry(i, a));
            }

            callback.onDataLoaded(dias, visualizacoes, cliques, avaliacoes);

        }).addOnFailureListener(e -> {
            Log.e("FIREBASE", "Erro ao carregar dados", e);
            callback.onDataLoaded(new String[0], new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        });
    }

    // Interface de retorno de dados
    public interface ChartDataCallback {
        void onDataLoaded(String[] dias, List<Entry> visualizacoes, List<Entry> cliques, List<Entry> avaliacoes);
    }

    private void carregarDadosAvaliacoes(String idEmpreendimento, PieChart pieChart) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference avaliacoesRef = db.collection("Avaliacoes");

        // Mapa para contar estrelas (1 a 5)
        int[] contagemEstrelas = new int[5]; // Índice 0 = 1 estrela, 4 = 5 estrelas

        avaliacoesRef
                .whereEqualTo("idEmpreendimento", idEmpreendimento)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Long estrelas = doc.getLong("estrelas");
                        if (estrelas != null && estrelas >= 1 && estrelas <= 5) {
                            contagemEstrelas[estrelas.intValue() - 1]++;
                        }
                    }

                    // Inverte para mostrar do 5 até 1
                    int[] estrelasInverso = new int[5];
                    for (int i = 0; i < 5; i++) {
                        estrelasInverso[i] = contagemEstrelas[4 - i];
                    }

                    String[] rotulos = {"5 estrelas", "4 estrelas", "3 estrelas", "2 estrelas", "1 estrela"};
                    List<PieEntry> entriesPie = new ArrayList<>();
                    for (int i = 0; i < estrelasInverso.length; i++) {
                        if (estrelasInverso[i] > 0) {
                            entriesPie.add(new PieEntry(estrelasInverso[i], rotulos[i]));
                        }
                    }

                    PieDataSet pieDataSet = new PieDataSet(entriesPie, "");
                    pieDataSet.setColors(new int[]{
                            Color.parseColor("#4CAF50"),  // 5★ - verde
                            Color.parseColor("#8BC34A"),  // 4★
                            Color.parseColor("#FFEB3B"),  // 3★ - amarelo
                            Color.parseColor("#FFC107"),  // 2★ - laranja claro
                            Color.parseColor("#F44336")   // 1★ - vermelho
                    });
                    pieDataSet.setValueTextSize(14f);
                    pieDataSet.setValueTextColor(Color.BLACK);

                    PieData pieData = new PieData(pieDataSet);
                    pieChart.setData(pieData);
                    pieChart.setUsePercentValues(true);
                    pieChart.setDrawEntryLabels(false);
                    pieChart.getDescription().setEnabled(false);
                    pieChart.setExtraOffsets(20f, 10f, 20f, 10f);
                    pieChart.setHoleRadius(50f);
                    pieChart.setTransparentCircleRadius(55f);
                    pieChart.animateY(1000);

                    Legend legend = pieChart.getLegend();
                    legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM); // legenda abaixo
                    legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                    legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                    legend.setDrawInside(false);
                    legend.setTextSize(12f); // Tamanho da legenda
                    legend.setXEntrySpace(15f); // espaço horizontal entre itens (quando estiver em linha)
                    legend.setXOffset(-20f);

                })
                .addOnFailureListener(e -> Log.e("FIREBASE", "Erro ao carregar avaliações", e));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
