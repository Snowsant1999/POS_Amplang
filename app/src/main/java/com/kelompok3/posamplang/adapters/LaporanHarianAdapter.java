package com.kelompok3.posamplang.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.models.LaporanHarian;
import java.util.List;

public class LaporanHarianAdapter extends RecyclerView.Adapter<LaporanHarianAdapter.ViewHolder> {

    private List<LaporanHarian> laporanList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onLihatClick(int position);
        void onPrintClick(int position);
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public LaporanHarianAdapter(List<LaporanHarian> laporanList) {
        this.laporanList = laporanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_laporan_harian, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LaporanHarian laporan = laporanList.get(position);
        holder.tvTanggal.setText(laporan.getTanggal());
        holder.tvPenjualanAwal.setText("Rp" + String.format("%,d", laporan.getPenjualanAwal()));
        holder.tvPenjualanTunai.setText("Rp" + String.format("%,d", laporan.getPenjualanTunai()));
        holder.tvUangTunai.setText("Rp" + String.format("%,d", laporan.getUangTunai()));
        holder.tvTotalPenjualan.setText("Rp" + String.format("%,d", laporan.getTotalPenjualan()));
        holder.tvCash.setText("Rp" + String.format("%,d", laporan.getCash()));
        holder.tvPembayaranOnline.setText("Rp" + String.format("%,d", laporan.getPembayaranOnline()));
        holder.tvBon.setText("Rp" + String.format("%,d", laporan.getBon()));

        // Baris belang-beling
        if (position % 2 == 0) {
            holder.rowContainer.setBackgroundColor(0xFFFFFFFF);
        } else {
            holder.rowContainer.setBackgroundColor(0xFFF2F2F2);
        }

        // Set Click Listeners
        holder.btnLihat.setOnClickListener(v -> {
            if (listener != null) listener.onLihatClick(position);
        });
        holder.btnPrint.setOnClickListener(v -> {
            if (listener != null) listener.onPrintClick(position);
        });
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(position);
        });
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return laporanList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTanggal, tvPenjualanAwal, tvPenjualanTunai, tvUangTunai, 
                 tvTotalPenjualan, tvCash, tvPembayaranOnline, tvBon;
        View rowContainer, btnLihat, btnPrint, btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowContainer = itemView.findViewById(R.id.row_container);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvPenjualanAwal = itemView.findViewById(R.id.tvPenjualanAwal);
            tvPenjualanTunai = itemView.findViewById(R.id.tvPenjualanTunai);
            tvUangTunai = itemView.findViewById(R.id.tvUangTunai);
            tvTotalPenjualan = itemView.findViewById(R.id.tvTotalPenjualan);
            tvCash = itemView.findViewById(R.id.tvCash);
            tvPembayaranOnline = itemView.findViewById(R.id.tvPembayaranOnline);
            tvBon = itemView.findViewById(R.id.tvBon);
            
            btnLihat = itemView.findViewById(R.id.btnLihat);
            btnPrint = itemView.findViewById(R.id.btnPrint);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}