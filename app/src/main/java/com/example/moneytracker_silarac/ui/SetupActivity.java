package com.example.moneytracker_silarac.ui;


import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneytracker_silarac.R; // Asegúrate de que R se importe de TU paquete
import com.example.moneytracker_silarac.utils.PrefsManager;
import com.google.android.material.textfield.TextInputEditText;

public class SetupActivity extends AppCompatActivity {

    private TextInputEditText etName, etBudget;
    private Spinner spinnerCurrency;
    private Button btnSave;
    private PrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Verificar si ya configuramos la app. Si es así, ir directo al Dashboard.
        prefsManager = new PrefsManager(this);
        if (prefsManager.isSetupDone()) {
            goToMain();
            return;
        }

        setContentView(R.layout.activity_setup);

        // 2. Vincular vistas
        etName = findViewById(R.id.etName);
        etBudget = findViewById(R.id.etBudget);
        spinnerCurrency = findViewById(R.id.spinnerCurrency);
        btnSave = findViewById(R.id.btnSaveSetup);

        // 3. Configurar Spinner de Monedas
        String[] currencies = {"USD", "EUR", "MXN", "COP", "ARS"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(adapter);

        // 4. Guardar datos
        btnSave.setOnClickListener(v -> saveSetup());
    }

    private void saveSetup() {
        String name = etName.getText().toString().trim();
        String budgetStr = etBudget.getText().toString().trim();
        String currency = spinnerCurrency.getSelectedItem().toString();

        if (name.isEmpty() || budgetStr.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        float budget = Float.parseFloat(budgetStr);

        // Guardar en SharedPreferences
        prefsManager.saveProfile(name, budget, currency);

        // Ir a la pantalla principal
        goToMain();
    }

    private void goToMain() {
        // AQUÍ INICIAREMOS LA MAIN ACTIVITY (La crearemos en el siguiente paso)
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

        // TEMPORAL: Solo para probar que funciona esta pantalla
        Toast.makeText(this, "Configuración Guardada. Listo para ir al Dashboard", Toast.LENGTH_LONG).show();
    }
}