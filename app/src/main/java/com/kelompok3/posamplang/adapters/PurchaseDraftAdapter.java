package com.kelompok3.posamplang.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.models.PurchaseDraftItem;
import com.kelompok3.posamplang.utils.FormatUtils;

import java.util.List;

public class PurchaseDraftAdapter extends RecyclerView.Adapter<PurchaseDraftAdapter.ViewHolder> {

    public interface Listener {
        void onRemove(int position);
    }

    private final List<PurchaseDraftItem> items;
    private final Listener listener;

    public PurchaseDraftAdapter(List<PurchaseDraftItem> items, Listener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_purchase_draft, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PurchaseDraftItem item = items.get(position);
        holder.tvNama.setText(item.getNamaProduk());
        holder.tvTipe.setText(item.isProdukBaru() ? "Produk baru" : "Produk ada");
        holder.tvJumlah.setText(String.valueOf(item.getJumlah()));
        holder.tvTotal.setText(FormatUtils.formatRupiah(item.getTotalHarga()));
        holder.btnRemove.setOnClickListener(v -> listener.onRemove(holder.getBindingAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvNama;
        final TextView tvTipe;
        final TextView tvJumlah;
        final TextView tvTotal;
        final ImageButton btnRemove;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_draft_nama);
            tvTipe = itemView.findViewById(R.id.tv_draft_tipe);
            tvJumlah = itemView.findViewById(R.id.tv_draft_jumlah);
            tvTotal = itemView.findViewById(R.id.tv_draft_total);
            btnRemove = itemView.findViewById(R.id.btn_remove_draft);
        }
    }
}
