package com.kelompok3.posamplang.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.models.Supplier;

import java.util.List;

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.ViewHolder> {

    public interface OnSupplierActionListener {
        void onDetailClick(Supplier supplier);
        void onEditClick(Supplier supplier);
        void onDeleteClick(Supplier supplier);
    }

    private final List<Supplier> supplierList;
    private final OnSupplierActionListener listener;

    public SupplierAdapter(List<Supplier> supplierList, OnSupplierActionListener listener) {
        this.supplierList = supplierList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_supplier, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Supplier supplier = supplierList.get(position);
        holder.tvNama.setText(supplier.getNama_supplier());
        holder.tvKontak.setText(supplier.getNo_telepon());
        holder.tvAlamat.setText(supplier.getAlamat_supplier());
        holder.tvEmail.setText(TextUtils.isEmpty(supplier.getEmail()) ? "-" : supplier.getEmail());

        holder.tvStatus.setText(supplier.isAktif() ? "Aktif" : "Nonaktif");
        holder.tvStatus.setBackgroundResource(
                supplier.isAktif() ? R.drawable.bg_status_aktif : R.drawable.bg_status_nonaktif);

        holder.tvNama.setOnClickListener(v -> listener.onDetailClick(supplier));
        holder.btnDetail.setOnClickListener(v -> listener.onDetailClick(supplier));
        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(supplier));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(supplier));
    }

    @Override
    public int getItemCount() {
        return supplierList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNama;
        private final TextView tvKontak;
        private final TextView tvAlamat;
        private final TextView tvEmail;
        private final TextView tvStatus;
        private final MaterialButton btnDetail;
        private final MaterialButton btnEdit;
        private final ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_nama_supplier);
            tvKontak = itemView.findViewById(R.id.tv_kontak);
            tvAlamat = itemView.findViewById(R.id.tv_alamat);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnDetail = itemView.findViewById(R.id.btn_detail);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
