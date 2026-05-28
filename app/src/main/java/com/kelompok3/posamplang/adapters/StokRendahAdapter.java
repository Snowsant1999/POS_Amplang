package com.kelompok3.posamplang.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.models.Produk;

import java.util.List;

public class StokRendahAdapter extends RecyclerView.Adapter<StokRendahAdapter.ViewHolder> {

    private final List<Produk> list;

    public StokRendahAdapter(List<Produk> list) {
        this.list = list;
    }

    /** Refresh data tanpa harus membuat adapter baru */
    public void updateData(List<Produk> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stok_rendah, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Produk p = list.get(position);
        h.tvNama.setText(p.getNama_produk());
        h.tvStok.setText(p.getStok_tersedia() + " " + p.getUnit());

        // Warna merah jika kritis (≤5), orange jika menipis (≤10)
        if (p.getStok_tersedia() <= 5) {
            h.tvStok.setTextColor(Color.parseColor("#C62828"));
            h.tvStok.setBackgroundColor(Color.parseColor("#20C62828"));
        } else {
            h.tvStok.setTextColor(Color.parseColor("#E65100"));
            h.tvStok.setBackgroundColor(Color.parseColor("#20E65100"));
        }
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvStok;
        ViewHolder(@NonNull View v) {
            super(v);
            tvNama = v.findViewById(R.id.tv_nama_produk_stok);
            tvStok = v.findViewById(R.id.tv_jumlah_stok);
        }
    }
}
