package com.kelompok3.posamplang.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.models.StokRequestSummary;
import com.kelompok3.posamplang.utils.FormatUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SupplierRequestAdapter extends RecyclerView.Adapter<SupplierRequestAdapter.ViewHolder> {

    public interface Listener {
        void onDetailClick(StokRequestSummary request);
        void onStatusClick(StokRequestSummary request);
    }

    private final List<StokRequestSummary> requests;
    private final Listener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));

    public SupplierRequestAdapter(List<StokRequestSummary> requests, Listener listener) {
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_supplier_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StokRequestSummary request = requests.get(position);
        holder.tvTanggal.setText(dateFormat.format(new Date(request.getTanggal_request())));
        holder.tvNomor.setText(request.getNomor_request());
        holder.tvTotal.setText(FormatUtils.formatRupiah(request.getTotal_harga()));
        holder.tvStatus.setText(request.getStatus());

        if ("Selesai".equals(request.getStatus())) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_aktif);
        } else if ("Diproses".equals(request.getStatus())) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_diproses);
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_nonaktif);
        }

        boolean dapatDiubah = "Diproses".equals(request.getStatus());
        holder.btnStatus.setVisibility(dapatDiubah ? View.VISIBLE : View.GONE);
        holder.btnDetail.setVisibility(dapatDiubah ? View.GONE : View.VISIBLE);
        holder.btnDetail.setOnClickListener(v -> listener.onDetailClick(request));
        holder.btnStatus.setOnClickListener(v -> listener.onStatusClick(request));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTanggal;
        final TextView tvNomor;
        final TextView tvTotal;
        final TextView tvStatus;
        final MaterialButton btnDetail;
        final MaterialButton btnStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTanggal = itemView.findViewById(R.id.tv_request_tanggal);
            tvNomor = itemView.findViewById(R.id.tv_request_nomor);
            tvTotal = itemView.findViewById(R.id.tv_request_total);
            tvStatus = itemView.findViewById(R.id.tv_request_status);
            btnDetail = itemView.findViewById(R.id.btn_request_detail);
            btnStatus = itemView.findViewById(R.id.btn_request_status);
        }
    }
}
