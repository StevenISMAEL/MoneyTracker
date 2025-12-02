package com.example.moneytracker_silarac.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.io.Serializable; // <--- IMPORTAR ESTO

@Entity(
        tableName = "transactions",
        foreignKeys = @ForeignKey(
                entity = Category.class,
                parentColumns = "id",
                childColumns = "categoryId",
                onDelete = ForeignKey.SET_NULL
        )
)
// AGREGAR "implements Serializable" AQUÍ ABAJO
public class Transaction implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    // ... (El resto de tus campos siguen igual)
    public String type;
    public double amount;
    public int categoryId;
    public String description;
    public long date;
    public String paymentMethod;

    public Transaction(String type, double amount, int categoryId, String description, long date, String paymentMethod) {
        this.type = type;
        this.amount = amount;
        this.categoryId = categoryId;
        this.description = description;
        this.date = date;
        this.paymentMethod = paymentMethod;
    }
}