package com.example.moneytracker_silarac.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.moneytracker_silarac.R;
import com.example.moneytracker_silarac.data.Category;
import com.example.moneytracker_silarac.data.Transaction;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AddTransactionActivity extends AppCompatActivity {

    private AppViewModel mViewModel;
    private TextInputEditText etAmount, etDescription;
    private RadioButton rbExpense, rbIncome;
    private Spinner spinnerCategories;
    private Button btnSave;

    // Listas para manejar el Spinner
    private List<Category> loadedCategories = new ArrayList<>();
    private List<String> categoryNames = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        mViewModel = new ViewModelProvider(this).get(AppViewModel.class);

        initViews();
        setupSpinner();
        setupListeners();
    }

    private void initViews() {
        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        rbExpense = findViewById(R.id.rbExpense);
        rbIncome = findViewById(R.id.rbIncome);
        spinnerCategories = findViewById(R.id.spinnerCategories);
        btnSave = findViewById(R.id.btnSaveTransaction);
    }

    private void setupSpinner() {
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(spinnerAdapter);

        // Cargar categorías desde la BD
        // Por defecto cargamos todas. En una mejora, filtraríamos por Tipo (Gasto/Ingreso) al cambiar el RadioButton.
        mViewModel.getAllCategories().observe(this, categories -> {
            loadedCategories.clear();
            categoryNames.clear();

            for (Category c : categories) {
                loadedCategories.add(c);
                categoryNames.add(c.name);
            }
            spinnerAdapter.notifyDataSetChanged();
        });
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveTransaction());
    }

    private void saveTransaction() {
        String amountStr = etAmount.getText().toString();
        String description = etDescription.getText().toString();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Ingresa un monto", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String type = rbIncome.isChecked() ? "INCOME" : "EXPENSE";

        // Obtener categoría seleccionada
        int selectedPosition = spinnerCategories.getSelectedItemPosition();
        if (selectedPosition == -1) {
            Toast.makeText(this, "Selecciona una categoría", Toast.LENGTH_SHORT).show();
            return;
        }
        int categoryId = loadedCategories.get(selectedPosition).id;

        // Crear objeto Transacción
        Transaction transaction = new Transaction(
                type,
                amount,
                categoryId,
                description,
                System.currentTimeMillis(), // Fecha actual
                "Efectivo" // Por defecto
        );

        // Guardar en BD
        mViewModel.insertTransaction(transaction);

        Toast.makeText(this, "Guardado!", Toast.LENGTH_SHORT).show();
        finish(); // Volver al Dashboard
    }
}