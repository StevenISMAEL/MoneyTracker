package com.example.moneytracker_silarac.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moneytracker_silarac.R;
import com.example.moneytracker_silarac.data.AppDatabase;
import com.example.moneytracker_silarac.utils.PrefsManager;
import com.google.android.material.textfield.TextInputEditText;

public class SettingsActivity extends AppCompatActivity {

    private TextInputEditText etName, etBudget;
    private Spinner spinnerCurrency;
    private Button btnSave, btnReset;
    private PrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefsManager = new PrefsManager(this);

        etName = findViewById(R.id.etEditName);
        etBudget = findViewById(R.id.etEditBudget);
        spinnerCurrency = findViewById(R.id.spinnerEditCurrency);
        btnSave = findViewById(R.id.btnSaveChanges);
        btnReset = findViewById(R.id.btnResetData);

        setupSpinner();
        loadCurrentData();

        btnSave.setOnClickListener(v -> saveChanges());
        btnReset.setOnClickListener(v -> showResetConfirmation());
    }

    private void setupSpinner() {
        String[] currencies = {"USD", "EUR", "MXN", "COP", "ARS"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(adapter);
    }

    private void loadCurrentData() {
        etName.setText(prefsManager.getUserName());
        etBudget.setText(String.valueOf(prefsManager.getBudget()));

        // Seleccionar la moneda actual en el spinner
        String currentCurrency = prefsManager.getCurrency();
        ArrayAdapter adapter = (ArrayAdapter) spinnerCurrency.getAdapter();
        int position = adapter.getPosition(currentCurrency);
        if (position >= 0) {
            spinnerCurrency.setSelection(position);
        }
    }

    private void saveChanges() {
        String name = etName.getText().toString().trim();
        String budgetStr = etBudget.getText().toString().trim();
        String currency = spinnerCurrency.getSelectedItem().toString();

        if (name.isEmpty() || budgetStr.isEmpty()) {
            Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        prefsManager.saveProfile(name, Float.parseFloat(budgetStr), currency);
        Toast.makeText(this, "Ajustes guardados", Toast.LENGTH_SHORT).show();

        // Reiniciar MainActivity para reflejar cambios
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showResetConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("¿Estás seguro?")
                .setMessage("Esto borrará todas tus transacciones y configuración. No se puede deshacer.")
                .setPositiveButton("Borrar Todo", (dialog, which) -> resetApp())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void resetApp() {
        // 1. Borrar Preferencias
        prefsManager.clearData();

        // 2. Borrar Base de Datos (en hilo secundario)
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase.getDatabase(this).clearAllTables();

            // Volver al hilo principal para navegar
            runOnUiThread(() -> {
                Toast.makeText(this, "App restablecida", Toast.LENGTH_LONG).show();
                // Ir a la pantalla de Configuración Inicial (Setup)
                Intent intent = new Intent(SettingsActivity.this, SetupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        });
    }
}