package com.kelompok3.posamplang.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.models.LaporanHarian;
import com.kelompok3.posamplang.utils.FormatUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class LaporanHarianAdapter extends RecyclerView.Adapter<LaporanHarianAdapter.ViewHolder> {

    public interface Listener {
        void onLihatClick(LaporanHarian laporan);
        void onPrintClick(LaporanHarian laporan);
        void onDeleteClick(LaporanHarian laporan);
    }

    private final List<LaporanHarian> laporanList;
    private final Listener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));

    public LaporanHarianAdapter(List<LaporanHarian> laporanList, Listener listener) {
        this.laporanList = laporanList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_laporan_harian, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LaporanHarian laporan = laporanList.get(position);
        holder.tvTanggal.setText(dateFormat.format(laporan.getTanggal_laporan()));
        holder.tvPemasukan.setText(FormatUtils.formatRupiah(laporan.getPemasukan()));
        holder.tvPengeluaran.setText(FormatUtils.formatRupiah(laporan.getPengeluaran()));
        holder.tvSaldo.setText(FormatUtils.formatRupiah(laporan.getSaldo_bersih()));
        holder.tvTransaksi.setText(String.valueOf(laporan.getJumlah_transaksi()));
        holder.row.setBackgroundColor(position % 2 == 0 ? 0xFFFFFFFF : 0xFFF7F7F7);
        holder.btnLihat.setOnClickListener(v -> listener.onLihatClick(laporan));
        holder.btnPrint.setOnClickListener(v -> listener.onPrintClick(laporan));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(laporan));
    }

    @Override
    public int getItemCount() {
        return laporanList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final View row;
        final TextView tvTanggal;
        final TextView tvPemasukan;
        final TextView tvPengeluaran;
        final TextView tvSaldo;
        final TextView tvTransaksi;
        final MaterialButton btnLihat;
        final MaterialButton btnPrint;
        final ImageButton btnDelete;

        ViewHolder(@NonNull View view) {
            super(view);
            row = view.findViewById(R.id.row_container);
            tvTanggal = view.findViewById(R.id.tvTanggal);
            tvPemasukan = view.findViewById(R.id.tvPemasukan);
            tvPengeluaran = view.findViewById(R.id.tvPengeluaran);
            tvSaldo = view.findViewById(R.id.tvSaldoBersih);
            tvTransaksi = view.findViewById(R.id.tvTransaksi);
            btnLihat = view.findViewById(R.id.btnLihat);
            btnPrint = view.findViewById(R.id.btnPrint);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }
}
