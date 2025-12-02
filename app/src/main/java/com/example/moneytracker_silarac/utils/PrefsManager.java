package com.example.moneytracker_silarac.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {
    private static final String PREF_NAME = "MoneyTrackerPrefs";
    private static final String KEY_IS_SETUP = "is_setup";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_BUDGET = "monthly_budget";
    private static final String KEY_CURRENCY = "currency";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public PrefsManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Guardar configuración inicial
    public void saveProfile(String name, float budget, String currency) {
        editor.putString(KEY_USER_NAME, name);
        editor.putFloat(KEY_BUDGET, budget);
        editor.putString(KEY_CURRENCY, currency);
        editor.putBoolean(KEY_IS_SETUP, true); // Marcamos que ya se configuró
        editor.apply();
    }

    // Verificar si es la primera vez que se abre la app
    public boolean isSetupDone() {
        return prefs.getBoolean(KEY_IS_SETUP, false);
    }

    // Getters
    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "Usuario");
    }

    public float getBudget() {
        return prefs.getFloat(KEY_BUDGET, 0);
    }

    public String getCurrency() {
        return prefs.getString(KEY_CURRENCY, "USD");
    }

    // Método para borrar datos (Requisito: Restablecer datos)
    public void clearData() {
        editor.clear();
        editor.apply();
    }
}