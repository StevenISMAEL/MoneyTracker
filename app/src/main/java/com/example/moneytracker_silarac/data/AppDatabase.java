package com.example.moneytracker_silarac.data; // <--- CORREGIDO

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Category.class, Transaction.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract AppDao appDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "money_tracker_db")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                AppDao dao = INSTANCE.appDao();

                dao.insertCategory(new Category("Alimentación", "EXPENSE", "ic_food", "#FF5722"));
                dao.insertCategory(new Category("Transporte", "EXPENSE", "ic_transport", "#2196F3"));
                dao.insertCategory(new Category("Entretenimiento", "EXPENSE", "ic_movie", "#9C27B0"));
                dao.insertCategory(new Category("Salud", "EXPENSE", "ic_health", "#F44336"));

                dao.insertCategory(new Category("Salario", "INCOME", "ic_salary", "#4CAF50"));
                dao.insertCategory(new Category("Freelance", "INCOME", "ic_computer", "#8BC34A"));
            });
        }
    };
}