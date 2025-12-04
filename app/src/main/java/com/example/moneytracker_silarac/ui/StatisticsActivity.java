package com.example.moneytracker_silarac.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.moneytracker_silarac.R;
import com.example.moneytracker_silarac.data.CategoryTotal;
import com.example.moneytracker_silarac.data.Transaction;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class StatisticsActivity extends AppCompatActivity {

    private PieChart pieChart;
    private BarChart barChart; // Historial Diario
    private BarChart barChartCategories;
    private AppViewModel mViewModel;

    // Vistas de Tarjetas
    private TextView tvTotalExpenses, tvDailyAverage, tvTopCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Vincular Vistas
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);
        barChartCategories = findViewById(R.id.barChartCategories); // NUEVO
        tvTotalExpenses = findViewById(R.id.tvTotalExpenses);
        tvDailyAverage = findViewById(R.id.tvDailyAverage);
        tvTopCategory = findViewById(R.id.tvTopCategory);
        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // Configuración Inicial de Gráficos
        setupPieChart();
        setupBarChart(barChart);
        setupBarChart(barChartCategories); // Reusamos configuración base

        mViewModel = new ViewModelProvider(this).get(AppViewModel.class);

        // Cargar Datos
        loadStatistics();
    }

    private void setupPieChart() {
        pieChart.setDescription(null);
        pieChart.setCenterText("Gastos");
        pieChart.setCenterTextSize(18f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.getLegend().setEnabled(false); // Ocultar leyenda para más espacio
        pieChart.animateY(1000);
    }

    private void setupBarChart(BarChart chart) {
        chart.setDescription(null);
        chart.setFitBars(true);
        chart.setDrawGridBackground(false);
        chart.animateY(1000);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false); // Ocultar eje derecho
    }

    private void loadStatistics() {
        long now = System.currentTimeMillis();

        // 1. Cargar Datos para el PIE CHART, BAR CHART CATEGORIAS y la TARJETA de "Mayor Gasto"
        mViewModel.getCategoryTotals("EXPENSE", 0, now).observe(this, categoryTotals -> {
            updatePieChart(categoryTotals);
            updateCategoryBarChart(categoryTotals); // NUEVO
            updateTopCategoryCard(categoryTotals);
        });

        // 2. Cargar Datos para el BAR CHART DIARIO y la TARJETA de "Total" y "Promedio"
        mViewModel.getTransactionsByFilter(0, now, "EXPENSE").observe(this, transactions -> {
            updateBarChartAndCards(transactions);
        });
    }

    private void updatePieChart(List<CategoryTotal> categoryTotals) {
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (CategoryTotal cat : categoryTotals) {
            entries.add(new PieEntry((float) cat.totalAmount, cat.categoryName));
            try {
                colors.add(Color.parseColor(cat.color));
            } catch (Exception e) {
                colors.add(Color.LTGRAY);
            }
        }

        if (entries.isEmpty()) {
            pieChart.setCenterText("Sin Datos");
            pieChart.setData(null);
            pieChart.invalidate();
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("$%.0f", value);
            }
        });

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    private void updateCategoryBarChart(List<CategoryTotal> categoryTotals) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        int index = 0;

        for (CategoryTotal cat : categoryTotals) {
            entries.add(new BarEntry(index, (float) cat.totalAmount));
            labels.add(cat.categoryName);
            try {
                colors.add(Color.parseColor(cat.color));
            } catch (Exception e) {
                colors.add(Color.LTGRAY);
            }
            index++;
        }

        if (entries.isEmpty()) {
            barChartCategories.setData(null);
            barChartCategories.invalidate();
            return;
        }

        BarDataSet set = new BarDataSet(entries, "Categorías");
        set.setColors(colors);
        set.setValueTextSize(10f);
        set.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("$%.0f", value);
            }
        });

        BarData data = new BarData(set);
        data.setBarWidth(0.6f);

        barChartCategories.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChartCategories.setData(data);
        barChartCategories.invalidate();
    }

    private void updateTopCategoryCard(List<CategoryTotal> categoryTotals) {
        if (categoryTotals == null || categoryTotals.isEmpty()) {
            tvTopCategory.setText("-");
            return;
        }

        CategoryTotal top = categoryTotals.get(0);
        for (CategoryTotal cat : categoryTotals) {
            if (cat.totalAmount > top.totalAmount) {
                top = cat;
            }
        }
        tvTopCategory.setText(top.categoryName + " ($" + String.format("%.0f", top.totalAmount) + ")");
        try {
            tvTopCategory.setTextColor(Color.parseColor(top.color));
        } catch (Exception e) {}
    }

    private void updateBarChartAndCards(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            tvTotalExpenses.setText("$0.00");
            tvDailyAverage.setText("$0.00");
            return;
        }

        double totalExpense = 0;
        for (Transaction t : transactions) {
            totalExpense += t.amount;
        }
        tvTotalExpenses.setText(String.format("$%.2f", totalExpense));

        Map<String, Double> dailyMap = new TreeMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());

        for (Transaction t : transactions) {
            String day = sdf.format(new Date(t.date));
            dailyMap.put(day, dailyMap.getOrDefault(day, 0.0) + t.amount);
        }

        double average = totalExpense / Math.max(1, dailyMap.size());
        tvDailyAverage.setText(String.format("$%.2f", average));

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, Double> entry : dailyMap.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue().floatValue()));
            labels.add(entry.getKey());
            index++;
        }

        BarDataSet set = new BarDataSet(entries, "Gasto Diario");
        set.setColor(Color.parseColor("#2196F3"));
        set.setValueTextSize(10f);

        BarData data = new BarData(set);
        data.setBarWidth(0.6f);

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.setData(data);
        barChart.invalidate();
    }
}