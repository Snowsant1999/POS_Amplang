package com.kelompok3.posamplang.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.models.DetailStokRequest;
import com.kelompok3.posamplang.utils.FormatUtils;

import java.util.List;

public class RequestDetailAdapter extends RecyclerView.Adapter<RequestDetailAdapter.ViewHolder> {

    private final List<DetailStokRequest> details;

    public RequestDetailAdapter(List<DetailStokRequest> details) {
        this.details = details;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DetailStokRequest detail = details.get(position);
        holder.tvNama.setText(detail.getNama_produk());
        holder.tvTipe.setText(detail.isProduk_baru() ? "Baru" : "Tersedia");
        holder.tvQty.setText(String.valueOf(detail.getJumlah_stok()));
        holder.tvHarga.setText(FormatUtils.formatRupiah(detail.getHarga_beli()));
        holder.tvTotal.setText(FormatUtils.formatRupiah(detail.getTotal_harga()));
    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvNama;
        final TextView tvTipe;
        final TextView tvQty;
        final TextView tvHarga;
        final TextView tvTotal;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_detail_item_nama);
            tvTipe = itemView.findViewById(R.id.tv_detail_item_tipe);
            tvQty = itemView.findViewById(R.id.tv_detail_item_qty);
            tvHarga = itemView.findViewById(R.id.tv_detail_item_harga);
            tvTotal = itemView.findViewById(R.id.tv_detail_item_total);
        }
    }
}
