package com.kelompok3.posamplang.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.models.Produk;

import java.util.List;

public class MenuKasirAdapter extends RecyclerView.Adapter<MenuKasirAdapter.MenuViewHolder> {

    private List<Produk> produkList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Produk produk);
    }

    public MenuKasirAdapter(List<Produk> produkList, OnItemClickListener listener) {
        this.produkList = produkList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_kasir, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        Produk produk = produkList.get(position);
        holder.tvNamaProduk.setText(produk.getNama_produk());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(produk));
    }

    @Override
    public int getItemCount() {
        return produkList.size();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaProduk;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaProduk = itemView.findViewById(R.id.tv_nama_produk);
        }
    }
}
