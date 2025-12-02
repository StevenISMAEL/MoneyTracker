package com.example.moneytracker_silarac.api;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ExchangeRateService {
    // Ejemplo: https://api.exchangerate-api.com/v4/latest/USD
    @GET("v4/latest/{baseCurrency}")
    Call<ExchangeRateResponse> getRates(@Path("baseCurrency") String baseCurrency);
}