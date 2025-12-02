package com.example.moneytracker_silarac.ui;


import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.moneytracker_silarac.data.AppRepository;
import com.example.moneytracker_silarac.data.Category;
import com.example.moneytracker_silarac.data.CategoryTotal;
import com.example.moneytracker_silarac.data.Transaction;

import java.util.List;

public class AppViewModel extends AndroidViewModel {

    private AppRepository mRepository;
    private LiveData<List<Transaction>> mAllTransactions;
    private LiveData<List<Category>> mAllCategories;

    public AppViewModel(@NonNull Application application) {
        super(application);
        mRepository = new AppRepository(application);
        mAllTransactions = mRepository.getAllTransactions();
        mAllCategories = mRepository.getAllCategories();
    }

    // --- LECTURA ---
    public LiveData<List<Transaction>> getAllTransactions() { return mAllTransactions; }
    public LiveData<List<Category>> getAllCategories() { return mAllCategories; }

    public LiveData<List<Category>> getCategoriesByType(String type) {
        return mRepository.getCategoriesByType(type);
    }

    public LiveData<Double> getTotalAmount(String type, long start, long end) {
        return mRepository.getTotalAmount(type, start, end);
    }

    public LiveData<List<CategoryTotal>> getCategoryTotals(String type, long start, long end) {
        return mRepository.getCategoryTotals(type, start, end);
    }

    // --- ESCRITURA ---
    public void insertTransaction(Transaction transaction) { mRepository.insertTransaction(transaction); }
    public void deleteTransaction(Transaction transaction) { mRepository.deleteTransaction(transaction); }
    // AGREGA ESTE MÉTODO:
    public void updateTransaction(Transaction transaction) { mRepository.updateTransaction(transaction); }
    public void insertCategory(Category category) { mRepository.insertCategory(category); }


}