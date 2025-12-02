package com.example.moneytracker_silarac.ui; // <--- TU PAQUETE

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.moneytracker_silarac.R;
import com.example.moneytracker_silarac.api.ApiClient; // Importar API
import com.example.moneytracker_silarac.api.ExchangeRateResponse; // Importar Modelo
import com.example.moneytracker_silarac.data.Category;
import com.example.moneytracker_silarac.data.Transaction;
import com.example.moneytracker_silarac.utils.PrefsManager; // Importar Prefs
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTransactionActivity extends AppCompatActivity {

    private AppViewModel mViewModel;
    private TextInputEditText etAmount, etDescription;
    private EditText etForeignAmount; // Nuevo campo
    private Spinner spinnerForeignCurrency; // Nuevo spinner
    private Button btnConvert; // Nuevo botón

    private RadioButton rbExpense, rbIncome;
    private Spinner spinnerCategories;
    private Button btnSave;

    private List<Category> loadedCategories = new ArrayList<>();
    private List<String> categoryNames = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;
    private PrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        mViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        prefsManager = new PrefsManager(this);

        initViews();
        setupSpinners();
        setupListeners();
    }

    private void initViews() {
        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);

        // Vistas de Conversión
        etForeignAmount = findViewById(R.id.etForeignAmount);
        spinnerForeignCurrency = findViewById(R.id.spinnerForeignCurrency);
        btnConvert = findViewById(R.id.btnConvert);

        rbExpense = findViewById(R.id.rbExpense);
        rbIncome = findViewById(R.id.rbIncome);
        spinnerCategories = findViewById(R.id.spinnerCategories);
        btnSave = findViewById(R.id.btnSaveTransaction);
    }

    private void setupSpinners() {
        // 1. Spinner de Categorías (BD Local)
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(spinnerAdapter);

        mViewModel.getAllCategories().observe(this, categories -> {
            loadedCategories.clear();
            categoryNames.clear();
            for (Category c : categories) {
                loadedCategories.add(c);
                categoryNames.add(c.name);
            }
            spinnerAdapter.notifyDataSetChanged();
        });

        // 2. Spinner de Monedas Extranjeras (Hardcoded para el ejemplo)
        String[] currencies = {"USD", "EUR", "MXN", "COP", "ARS"};
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerForeignCurrency.setAdapter(currencyAdapter);
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveTransaction());

        // Listener para el botón CONVERTIR
        btnConvert.setOnClickListener(v -> convertCurrency());
    }

    private void convertCurrency() {
        String foreignAmountStr = etForeignAmount.getText().toString();
        if (foreignAmountStr.isEmpty()) {
            Toast.makeText(this, "Ingresa un monto extranjero", Toast.LENGTH_SHORT).show();
            return;
        }

        double foreignAmount = Double.parseDouble(foreignAmountStr);
        String selectedForeignCurrency = spinnerForeignCurrency.getSelectedItem().toString();
        String myBaseCurrency = prefsManager.getCurrency(); // La moneda que configuró el usuario (ej: MXN)

        // Llamada a la API
        // Pedimos las tasas teniendo como base la moneda extranjera
        // Ej: Si seleccionó USD, pedimos cuánto vale 1 USD en las demás monedas
        ApiClient.getService().getRates(selectedForeignCurrency).enqueue(new Callback<ExchangeRateResponse>() {
            @Override
            public void onResponse(Call<ExchangeRateResponse> call, Response<ExchangeRateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Double rate = response.body().rates.get(myBaseCurrency);

                    if (rate != null) {
                        double convertedAmount = foreignAmount * rate;
                        etAmount.setText(String.format("%.2f", convertedAmount));
                        Toast.makeText(AddTransactionActivity.this,
                                "Tasa: 1 " + selectedForeignCurrency + " = " + rate + " " + myBaseCurrency,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AddTransactionActivity.this, "Moneda no encontrada en API", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddTransactionActivity.this, "Error en API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ExchangeRateResponse> call, Throwable t) {
                Toast.makeText(AddTransactionActivity.this, "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

        int selectedPosition = spinnerCategories.getSelectedItemPosition();
        if (selectedPosition == -1) return;
        int categoryId = loadedCategories.get(selectedPosition).id;

        Transaction transaction = new Transaction(
                type, amount, categoryId, description, System.currentTimeMillis(), "Efectivo"
        );

        mViewModel.insertTransaction(transaction);
        Toast.makeText(this, "Guardado!", Toast.LENGTH_SHORT).show();
        finish();
    }
}