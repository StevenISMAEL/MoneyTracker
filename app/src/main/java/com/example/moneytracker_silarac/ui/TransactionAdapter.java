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
import com.example.moneytracker_silarac.data.Category; // Importar Category
import com.example.moneytracker_silarac.data.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactions = new ArrayList<>();
    private List<Category> categories = new ArrayList<>(); // NUEVO: Lista de categorías

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Transaction transaction);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    // NUEVO: Método para recibir las categorías desde MainActivity
    public void setCategories(List<Category> categories) {
        this.categories = categories;
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

        // --- CAMBIO PRINCIPAL: Buscar el nombre de la categoría ---
        String categoryName = "Desconocido";
        String categoryColor = "#000000"; // Color por defecto

        // Buscamos en la lista de categorías cuál coincide con el ID
        for (Category cat : categories) {
            if (cat.id == current.categoryId) {
                categoryName = cat.name;
                categoryColor = cat.color;
                break;
            }
        }

        // Mostrar Nombre en lugar de ID
        holder.tvCategoryName.setText(categoryName);

        // Opcional: Cambiar color del icono según la categoría
        try {
            holder.imgIcon.setColorFilter(Color.parseColor(categoryColor));
        } catch (Exception e) {
            holder.imgIcon.setColorFilter(Color.GRAY);
        }
        // -----------------------------------------------------------

        holder.tvDescription.setText(current.description);
        holder.tvDate.setText(dateFormat.format(new Date(current.date)));

        if (current.type.equals("INCOME")) {
            holder.tvAmount.setText("+ $" + String.format("%.2f", current.amount));
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            holder.tvAmount.setText("- $" + String.format("%.2f", current.amount));
            holder.tvAmount.setTextColor(Color.parseColor("#F44336"));
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

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(transactions.get(position));
                }
            });
        }
    }
}