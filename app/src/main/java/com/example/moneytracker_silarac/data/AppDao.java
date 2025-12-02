package com.example.moneytracker_silarac.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AppDao {

    // --- CATEGORÍAS ---
    // Insertar una nueva categoría
    @Insert
    void insertCategory(Category category);

    // Obtener categorías por tipo (Ingreso o Gasto)
    @Query("SELECT * FROM categories WHERE type = :type")
    LiveData<List<Category>> getCategoriesByType(String type);

    // Obtener todas las categorías
    @Query("SELECT * FROM categories")
    LiveData<List<Category>> getAllCategories();

    // --- TRANSACCIONES ---
    @Insert
    void insertTransaction(Transaction transaction);

    @Update
    void updateTransaction(Transaction transaction);

    @Delete
    void deleteTransaction(Transaction transaction);

    // Obtener todas las transacciones ordenadas por fecha (más reciente primero)
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    LiveData<List<Transaction>> getAllTransactions();

    // Filtro avanzado: Rango de fechas y tipo
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate AND type = :type ORDER BY date DESC")
    LiveData<List<Transaction>> getTransactionsByDateAndType(long startDate, long endDate, String type);

    // --- ESTADÍSTICAS ---
    // Suma total de ingresos o gastos en un rango de fechas
    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type AND date BETWEEN :startDate AND :endDate")
    LiveData<Double> getTotalAmountByType(String type, long startDate, long endDate);

    // Agrupar gastos por categoría (para gráficos)
    // Une la tabla Transacciones con Categorías para obtener nombre y color
    @Query("SELECT c.name as categoryName, SUM(t.amount) as totalAmount, c.color as color " +
            "FROM transactions t " +
            "INNER JOIN categories c ON t.categoryId = c.id " +
            "WHERE t.type = :type AND t.date BETWEEN :startDate AND :endDate " +
            "GROUP BY t.categoryId")
    LiveData<List<CategoryTotal>> getCategoryTotals(String type, long startDate, long endDate);
}