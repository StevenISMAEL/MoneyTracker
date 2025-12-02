package com.example.moneytracker_silarac.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import com.example.moneytracker_silarac.utils.PrefsManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppViewModel mViewModel;
    private TextView tvTotalBalance, tvTotalIncome, tvTotalExpense;
    private TextView tvBudgetStatus;
    private ProgressBar progressBarBudget;
    private TransactionAdapter adapter;
    private List<Transaction> currentList;
    private PrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefsManager = new PrefsManager(this);

        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        tvTotalIncome = findViewById(R.id.tvTotalIncome);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        tvBudgetStatus = findViewById(R.id.tvBudgetStatus);
        progressBarBudget = findViewById(R.id.progressBarBudget);

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        RecyclerView recyclerView = findViewById(R.id.recyclerTransactions);

        ImageButton btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        Button btnStats = findViewById(R.id.btnViewStats);
        btnStats.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            startActivity(intent);
        });

        adapter = new TransactionAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(transaction -> {
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
            intent.putExtra(AddTransactionActivity.EXTRA_TRANSACTION, transaction);
            startActivity(intent);
        });

        mViewModel = new ViewModelProvider(this).get(AppViewModel.class);

        // --- OBSERVADORES ---

        // 1. Observar Transacciones (Como antes)
        mViewModel.getAllTransactions().observe(this, transactions -> {
            currentList = transactions;
            adapter.setTransactions(transactions);
            calculateBalance(transactions);
        });

        // 2. NUEVO: Observar Categorías y pasarlas al adaptador
        mViewModel.getAllCategories().observe(this, categories -> {
            adapter.setCategories(categories);
        });
        // ----------------------------------------------------

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
            startActivity(intent);
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (currentList != null) {
                    int position = viewHolder.getAdapterPosition();
                    Transaction transactionToDelete = currentList.get(position);
                    mViewModel.deleteTransaction(transactionToDelete);
                    Toast.makeText(MainActivity.this, "Transacción eliminada", Toast.LENGTH_SHORT).show();
                }
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void calculateBalance(List<Transaction> transactions) {
        double income = 0;
        double expense = 0;

        if (transactions != null) {
            for (Transaction t : transactions) {
                if ("INCOME".equals(t.type)) {
                    income += t.amount;
                } else {
                    expense += t.amount;
                }
            }
        }

        double balance = income - expense;

        tvTotalIncome.setText(String.format("+ $%.2f", income));
        tvTotalExpense.setText(String.format("- $%.2f", expense));
        tvTotalBalance.setText(String.format("$%.2f", balance));

        checkBudget(expense);
    }

    private void checkBudget(double totalExpense) {
        float monthlyBudget = prefsManager.getBudget();

        if (monthlyBudget <= 0) {
            tvBudgetStatus.setText("Presupuesto no configurado");
            progressBarBudget.setProgress(0);
            return;
        }

        int percentage = (int) ((totalExpense / monthlyBudget) * 100);

        progressBarBudget.setProgress(Math.min(percentage, 100));
        tvBudgetStatus.setText(percentage + "% gastado de $" + monthlyBudget);

        if (percentage >= 100) {
            progressBarBudget.setProgressTintList(ColorStateList.valueOf(Color.RED));
            tvBudgetStatus.setTextColor(Color.RED);
        } else if (percentage >= 80) {
            progressBarBudget.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));
            tvBudgetStatus.setTextColor(Color.parseColor("#FF9800"));
        } else {
            progressBarBudget.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#2196F3")));
            tvBudgetStatus.setTextColor(Color.BLACK);
        }
    }
}