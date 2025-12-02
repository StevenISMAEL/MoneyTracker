package com.example.moneytracker_silarac.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.moneytracker_silarac.R;
import com.example.moneytracker_silarac.data.CategoryTotal;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    private PieChart pieChart;
    private AppViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        pieChart = findViewById(R.id.pieChart);
        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // Configuración básica del gráfico
        pieChart.setDescription(null);
        pieChart.setCenterText("Gastos");
        pieChart.setCenterTextSize(20f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.animateY(1000); // Animación al abrir

        mViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        loadChartData();
    }

    private void loadChartData() {
        // Consultar gastos (EXPENSE) desde el inicio de los tiempos (0) hasta hoy
        long now = System.currentTimeMillis();
        mViewModel.getCategoryTotals("EXPENSE", 0, now).observe(this, categoryTotals -> {

            List<PieEntry> entries = new ArrayList<>();
            List<Integer> colors = new ArrayList<>();

            for (CategoryTotal cat : categoryTotals) {
                // Agregar rebanada: Valor y Nombre
                entries.add(new PieEntry((float) cat.totalAmount, cat.categoryName));

                // Intentar usar el color de la categoría, si falla usar uno por defecto
                try {
                    colors.add(Color.parseColor(cat.color));
                } catch (Exception e) {
                    colors.add(Color.LTGRAY);
                }
            }

            if (entries.isEmpty()) {
                pieChart.setCenterText("Sin Gastos");
                return;
            }

            PieDataSet dataSet = new PieDataSet(entries, "Categorías");
            dataSet.setColors(colors); // Usar nuestros colores
            dataSet.setValueTextSize(14f);
            dataSet.setValueTextColor(Color.WHITE);

            PieData data = new PieData(dataSet);
            pieChart.setData(data);
            pieChart.invalidate(); // Refrescar gráfico
        });
    }
}