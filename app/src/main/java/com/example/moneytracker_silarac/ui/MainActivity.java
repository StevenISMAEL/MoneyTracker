package com.example.moneytracker_silarac.ui; // <--- TU PAQUETE CORRECTO

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button; // Importar Button
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytracker_silarac.R;
import com.example.moneytracker_silarac.data.Transaction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppViewModel mViewModel;
    private TextView tvTotalBalance, tvTotalIncome, tvTotalExpense;
    private TransactionAdapter adapter;
    private List<Transaction> currentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Vincular Vistas
        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        tvTotalIncome = findViewById(R.id.tvTotalIncome);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        RecyclerView recyclerView = findViewById(R.id.recyclerTransactions);

        // --- NUEVO: Botón de Estadísticas ---
        Button btnStats = findViewById(R.id.btnViewStats);
        btnStats.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            startActivity(intent);
        });
        // ------------------------------------

        // 2. Configurar RecyclerView
        adapter = new TransactionAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 3. Configurar ViewModel
        mViewModel = new ViewModelProvider(this).get(AppViewModel.class);

        // 4. Observar cambios
        mViewModel.getAllTransactions().observe(this, transactions -> {
            currentList = transactions;
            adapter.setTransactions(transactions);
            calculateBalance(transactions);
        });

        // 5. Configurar FAB
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
            startActivity(intent);
        });

        // 6. Swipe to Delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Transaction transactionToDelete = currentList.get(position);
                mViewModel.deleteTransaction(transactionToDelete);
                Toast.makeText(MainActivity.this, "Transacción eliminada", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
    }

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

        tvTotalIncome.setText(String.format("+ $%.2f", income));
        tvTotalExpense.setText(String.format("- $%.2f", expense));
        tvTotalBalance.setText(String.format("$%.2f", balance));
    }
}