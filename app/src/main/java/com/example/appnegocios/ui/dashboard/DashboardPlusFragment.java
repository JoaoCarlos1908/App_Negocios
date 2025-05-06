package com.example.appnegocios.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.appnegocios.R;
import com.example.appnegocios.databinding.FragmentDashboardPlusBinding;
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

import java.util.ArrayList;
import java.util.List;

public class DashboardPlusFragment extends Fragment {

    private FragmentDashboardPlusBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard_plus, container, false);

        LineChart lineChart = view.findViewById(R.id.lineChart);

        // Datas representadas no eixo X
        String[] dias = new String[]{"01/05", "02/05", "03/05", "04/05", "05/05"};

        // Configuração do eixo X com datas
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
        List<Entry> visualizacoes = new ArrayList<>();
        visualizacoes.add(new Entry(0, 100));
        visualizacoes.add(new Entry(1, 150));
        visualizacoes.add(new Entry(2, 80));
        visualizacoes.add(new Entry(3, 200));
        visualizacoes.add(new Entry(4, 170));

        LineDataSet dataSetVisualizacoes = new LineDataSet(visualizacoes, "Visualizações");
        dataSetVisualizacoes.setColor(Color.parseColor("#3F51B5"));
        dataSetVisualizacoes.setCircleColor(Color.parseColor("#3F51B5"));
        dataSetVisualizacoes.setLineWidth(2f);
        dataSetVisualizacoes.setDrawFilled(true);
        dataSetVisualizacoes.setFillColor(Color.parseColor("#99CCFF"));

        // Cliques
        List<Entry> cliques = new ArrayList<>();
        cliques.add(new Entry(0, 40));
        cliques.add(new Entry(1, 60));
        cliques.add(new Entry(2, 45));
        cliques.add(new Entry(3, 80));
        cliques.add(new Entry(4, 65));

        LineDataSet dataSetCliques = new LineDataSet(cliques, "Cliques");
        dataSetCliques.setColor(Color.parseColor("#4CAF50")); // Verde
        dataSetCliques.setCircleColor(Color.parseColor("#4CAF50"));
        dataSetCliques.setLineWidth(2f);
        dataSetCliques.setDrawFilled(true);
        dataSetCliques.setFillColor(Color.parseColor("#A5D6A7"));

        // Avaliações
        List<Entry> avaliacoes = new ArrayList<>();
        avaliacoes.add(new Entry(0, 10));
        avaliacoes.add(new Entry(1, 20));
        avaliacoes.add(new Entry(2, 15));
        avaliacoes.add(new Entry(3, 25));
        avaliacoes.add(new Entry(4, 22));

        LineDataSet dataSetAvaliacoes = new LineDataSet(avaliacoes, "Avaliações");
        dataSetAvaliacoes.setColor(Color.parseColor("#FF9800")); // Laranja
        dataSetAvaliacoes.setCircleColor(Color.parseColor("#FF9800"));
        dataSetAvaliacoes.setLineWidth(2f);
        dataSetAvaliacoes.setDrawFilled(true);
        dataSetAvaliacoes.setFillColor(Color.parseColor("#FFE0B2"));

        // Monta o gráfico com todos os datasets
        LineData lineData = new LineData(dataSetVisualizacoes, dataSetCliques, dataSetAvaliacoes);
        lineChart.setData(lineData);
        lineChart.getDescription().setText("Engajamento no perfil");
        lineChart.getDescription().setTextColor(Color.DKGRAY);
        lineChart.getDescription().setTextSize(12f);
        lineChart.animateX(1000);

        // Configuração da legenda
        Legend legend = lineChart.getLegend();
        legend.setEnabled(true);
        legend.setTextSize(12f);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setForm(Legend.LegendForm.LINE);

        // Gráfico de pizza para avaliações por estrela
        PieChart pieChart = view.findViewById(R.id.pieChart);

// Exemplo fictício: número de avaliações de 5 a 1 estrelas
        int[] estrelas = {30, 25, 20, 10, 5}; // 5★, 4★, ..., 1★
        String[] rotulos = {"5 estrelas", "4 estrelas", "3 estrelas", "2 estrelas", "1 estrela"};

        List<PieEntry> entriesPie = new ArrayList<>();
        for (int i = 0; i < estrelas.length; i++) {
            entriesPie.add(new PieEntry(estrelas[i], rotulos[i]));
        }

        PieDataSet pieDataSet = new PieDataSet(entriesPie, "");
        pieDataSet.setColors(new int[] {
                Color.parseColor("#4CAF50"),  // 5★ - verde
                Color.parseColor("#8BC34A"),  // 4★
                Color.parseColor("#FFEB3B"),  // 3★ - amarelo
                Color.parseColor("#FFC107"),  // 2★ - laranja claro
                Color.parseColor("#F44336")   // 1★ - vermelho
        });
        pieDataSet.setValueTextSize(14f);
        pieDataSet.setValueTextColor(Color.WHITE);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        //pieChart.setCenterText("Avaliações");
        //pieChart.setCenterTextSize(18f);
        pieChart.setDrawEntryLabels(false); // Oculta os rótulos dentro do gráfico
        pieChart.getDescription().setEnabled(false);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.animateY(1000);

        Legend legendPie = pieChart.getLegend();
        legendPie.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legendPie.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legendPie.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legendPie.setDrawInside(false); // Deixa a legenda fora do gráfico

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
