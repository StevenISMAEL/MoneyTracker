package com.example.moneytracker_silarac.ui;

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
import com.example.moneytracker_silarac.api.ApiClient;
import com.example.moneytracker_silarac.api.ExchangeRateResponse;
import com.example.moneytracker_silarac.data.Category;
import com.example.moneytracker_silarac.data.Transaction;
import com.example.moneytracker_silarac.utils.PrefsManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTransactionActivity extends AppCompatActivity {

    public static final String EXTRA_TRANSACTION = "com.example.moneytracker.EXTRA_TRANSACTION";

    private AppViewModel mViewModel;
    private TextInputEditText etAmount, etDescription;
    private EditText etForeignAmount;
    private Spinner spinnerForeignCurrency;
    private Button btnConvert;
    private RadioButton rbExpense, rbIncome;
    private Spinner spinnerCategories;
    private Button btnSave;

    private List<Category> loadedCategories = new ArrayList<>();
    private List<String> categoryNames = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;
    private PrefsManager prefsManager;

    // Variable para saber si estamos editando
    private Transaction transactionToEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        mViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        prefsManager = new PrefsManager(this);

        initViews();

        // Verificar si venimos a EDITAR
        if (getIntent().hasExtra(EXTRA_TRANSACTION)) {
            setTitle("Editar Transacción");
            btnSave.setText("Actualizar");
            transactionToEdit = (Transaction) getIntent().getSerializableExtra(EXTRA_TRANSACTION);
        }

        setupSpinners();
        setupListeners();
    }

    private void initViews() {
        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        etForeignAmount = findViewById(R.id.etForeignAmount);
        spinnerForeignCurrency = findViewById(R.id.spinnerForeignCurrency);
        btnConvert = findViewById(R.id.btnConvert);
        rbExpense = findViewById(R.id.rbExpense);
        rbIncome = findViewById(R.id.rbIncome);
        spinnerCategories = findViewById(R.id.spinnerCategories);
        btnSave = findViewById(R.id.btnSaveTransaction);
    }

    private void setupSpinners() {
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(spinnerAdapter);

        // Spinner Monedas Extranjeras
        String[] currencies = {"USD", "EUR", "MXN", "COP", "ARS"};
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerForeignCurrency.setAdapter(currencyAdapter);

        // Cargar Categorías y luego llenar datos si es edición
        mViewModel.getAllCategories().observe(this, categories -> {
            loadedCategories.clear();
            categoryNames.clear();
            for (Category c : categories) {
                loadedCategories.add(c);
                categoryNames.add(c.name);
            }
            spinnerAdapter.notifyDataSetChanged();

            // Si estamos editando, llenar los campos AHORA que tenemos las categorías
            if (transactionToEdit != null) {
                fillDataForEdit();
            }
        });
    }

    private void fillDataForEdit() {
        etAmount.setText(String.valueOf(transactionToEdit.amount));
        etDescription.setText(transactionToEdit.description);

        if ("INCOME".equals(transactionToEdit.type)) {
            rbIncome.setChecked(true);
        } else {
            rbExpense.setChecked(true);
        }

        // Seleccionar la categoría correcta en el spinner
        for (int i = 0; i < loadedCategories.size(); i++) {
            if (loadedCategories.get(i).id == transactionToEdit.categoryId) {
                spinnerCategories.setSelection(i);
                break;
            }
        }
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveTransaction());
        btnConvert.setOnClickListener(v -> convertCurrency());
    }

    private void convertCurrency() {
        // (Tu lógica de conversión existente...)
        String foreignAmountStr = etForeignAmount.getText().toString();
        if (foreignAmountStr.isEmpty()) return;

        double foreignAmount = Double.parseDouble(foreignAmountStr);
        String selectedForeignCurrency = spinnerForeignCurrency.getSelectedItem().toString();
        String myBaseCurrency = prefsManager.getCurrency();

        ApiClient.getService().getRates(selectedForeignCurrency).enqueue(new Callback<ExchangeRateResponse>() {
            @Override
            public void onResponse(Call<ExchangeRateResponse> call, Response<ExchangeRateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Double rate = response.body().rates.get(myBaseCurrency);
                    if (rate != null) {
                        double convertedAmount = foreignAmount * rate;
                        etAmount.setText(String.format("%.2f", convertedAmount));
                        Toast.makeText(AddTransactionActivity.this, "Conversión realizada", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<ExchangeRateResponse> call, Throwable t) {}
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

        if (transactionToEdit == null) {
            // MODO CREAR: Nueva transacción
            Transaction transaction = new Transaction(
                    type, amount, categoryId, description, System.currentTimeMillis(), "Efectivo"
            );
            mViewModel.insertTransaction(transaction);
            Toast.makeText(this, "Guardado!", Toast.LENGTH_SHORT).show();
        } else {
            // MODO EDITAR: Actualizar datos de la existente
            transactionToEdit.amount = amount;
            transactionToEdit.description = description;
            transactionToEdit.type = type;
            transactionToEdit.categoryId = categoryId;
            // No cambiamos la fecha original al editar

            mViewModel.updateTransaction(transactionToEdit);
            Toast.makeText(this, "Actualizado!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}