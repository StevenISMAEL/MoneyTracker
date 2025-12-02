package com.example.moneytracker_silarac.api;


import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class ExchangeRateResponse {
    @SerializedName("base")
    public String baseCurrency;

    @SerializedName("date")
    public String date;

    @SerializedName("rates")
    public Map<String, Double> rates; // Un mapa con "EUR": 0.85, "MXN": 20.5, etc.
}