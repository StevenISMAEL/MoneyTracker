package com.example.moneytracker_silarac.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytracker_silarac.R;
import com.example.moneytracker_silarac.data.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactions = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction current = transactions.get(position);

        // Configurar Textos
        // Nota: En una versión avanzada, buscaríamos el nombre real de la categoría usando el ID
        holder.tvCategoryName.setText("Cat ID: " + current.categoryId);
        holder.tvDescription.setText(current.description);
        holder.tvDate.setText(dateFormat.format(new Date(current.date)));

        // Formatear Monto y Color
        if (current.type.equals("INCOME")) {
            holder.tvAmount.setText("+ $" + String.format("%.2f", current.amount));
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50")); // Verde
        } else {
            holder.tvAmount.setText("- $" + String.format("%.2f", current.amount));
            holder.tvAmount.setTextColor(Color.parseColor("#F44336")); // Rojo
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName, tvDescription, tvAmount, tvDate;
        ImageView imgIcon;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
            imgIcon = itemView.findViewById(R.id.imgCategoryIcon);
        }
    }
}