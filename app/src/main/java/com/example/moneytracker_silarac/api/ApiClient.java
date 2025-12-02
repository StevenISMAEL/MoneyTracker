

package com.example.moneytracker_silarac.api;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // URL base de la API gratuita
    private static final String BASE_URL = "https://api.exchangerate-api.com/";
    private static Retrofit retrofit = null;

    // Método estático para obtener la instancia del servicio
    public static ExchangeRateService getService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // Convierte JSON a objetos Java
                    .build();
        }
        return retrofit.create(ExchangeRateService.class);
    }
}