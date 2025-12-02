package com.example.moneytracker_silarac.ui; // <--- TU PAQUETE CORRECTO

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Importaciones de TU proyecto (asegúrate que digan moneytracker_silarac)
import com.example.moneytracker_silarac.R;
import com.example.moneytracker_silarac.data.Transaction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppViewModel mViewModel;
    private TextView tvTotalBalance, tvTotalIncome, tvTotalExpense;
    private TransactionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Usamos el layout del Dashboard

        // 1. Vincular Vistas (TextViews del resumen y lista)
        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        tvTotalIncome = findViewById(R.id.tvTotalIncome);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        RecyclerView recyclerView = findViewById(R.id.recyclerTransactions);

        // 2. Configurar RecyclerView (La lista de movimientos)
        adapter = new TransactionAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 3. Configurar ViewModel (Conexión con la Base de Datos)
        mViewModel = new ViewModelProvider(this).get(AppViewModel.class);

        // 4. Observar cambios en las Transacciones
        // Cada vez que agregues o borres algo, este código se ejecutará automáticamente.
        mViewModel.getAllTransactions().observe(this, transactions -> {
            // A) Actualizar la lista visual
            adapter.setTransactions(transactions);

            // B) Recalcular los números del balance (Ingresos vs Gastos)
            calculateBalance(transactions);
        });

        // 5. Configurar Botón Agregar (+)
        // Esto abre la pantalla de formulario
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
            startActivity(intent);
        });
    }

    // Método auxiliar para sumar ingresos y gastos
    private void calculateBalance(List<Transaction> transactions) {
        double income = 0;
        double expense = 0;

        for (Transaction t : transactions) {
            if ("INCOME".equals(t.type)) {
                income += t.amount;
            } else {
                expense += t.amount;
            }
        }

        double balance = income - expense;

        // Mostrar los textos formateados con 2 decimales
        tvTotalIncome.setText(String.format("+ $%.2f", income));
        tvTotalExpense.setText(String.format("- $%.2f", expense));
        tvTotalBalance.setText(String.format("$%.2f", balance));
    }
}