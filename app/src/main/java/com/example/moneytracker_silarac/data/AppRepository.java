package com.example.moneytracker_silarac.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

// El Repositorio gestiona las consultas de datos y permite usar múltiples backends.
// En este caso, usa el DAO (Base de datos local).
public class AppRepository {

    private AppDao mAppDao;
    private LiveData<List<Category>> mAllCategories;
    private LiveData<List<Transaction>> mAllTransactions;

    // Constructor: Inicializa la base de datos y obtiene los datos iniciales
    public AppRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mAppDao = db.appDao();
        mAllCategories = mAppDao.getAllCategories();
        mAllTransactions = mAppDao.getAllTransactions();
    }

    // --- MÉTODOS DE LECTURA (Retornan LiveData para que la UI reaccione) ---

    public LiveData<List<Category>> getAllCategories() {
        return mAllCategories;
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return mAllTransactions;
    }

    public LiveData<List<Category>> getCategoriesByType(String type) {
        return mAppDao.getCategoriesByType(type);
    }

    // Obtener transacciones filtradas
    public LiveData<List<Transaction>> getTransactionsByFilter(long start, long end, String type) {
        return mAppDao.getTransactionsByDateAndType(start, end, type);
    }

    // Obtener estadísticas por categoría (para gráficos)
    public LiveData<List<CategoryTotal>> getCategoryTotals(String type, long start, long end) {
        return mAppDao.getCategoryTotals(type, start, end);
    }

    public LiveData<Double> getTotalAmount(String type, long start, long end) {
        return mAppDao.getTotalAmountByType(type, start, end);
    }

    // --- MÉTODOS DE ESCRITURA (Ejecutados en hilo secundario) ---

    public void insertCategory(Category category) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mAppDao.insertCategory(category);
        });
    }

    public void insertTransaction(Transaction transaction) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mAppDao.insertTransaction(transaction);
        });
    }

    public void updateTransaction(Transaction transaction) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mAppDao.updateTransaction(transaction);
        });
    }

    public void deleteTransaction(Transaction transaction) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mAppDao.deleteTransaction(transaction);
        });
    }
}