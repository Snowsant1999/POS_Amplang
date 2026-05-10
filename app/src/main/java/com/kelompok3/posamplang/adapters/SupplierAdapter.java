package com.kelompok3.posamplang.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.activities.supplier.SupplierDetailActivity;
import com.kelompok3.posamplang.models.Supplier;

import java.util.List;

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.ViewHolder> {

    private List<Supplier> supplierList;
    private Context context;

    public SupplierAdapter(List<Supplier> supplierList, Context context) {
        this.supplierList = supplierList;
        this.context = context;
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
        holder.tvEmail.setText("-"); // Email tidak ada di ERD
        
        if (supplier.isAktif()) {
            holder.tvStatus.setText("Aktif");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_aktif);
        } else {
            holder.tvStatus.setText("Nonaktif");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_nonaktif);
        }

        holder.tvNama.setOnClickListener(v -> {
            Intent intent = new Intent(context, SupplierDetailActivity.class);
            intent.putExtra("SUPPLIER_ID", supplier.getId_supplier());
            intent.putExtra("SUPPLIER_NAMA", supplier.getNama_supplier());
            context.startActivity(intent);
        });

        holder.btnEdit.setOnClickListener(v -> {
            // Logic Edit
        });

        holder.btnDelete.setOnClickListener(v -> {
            // Logic Delete
        });
    }

    @Override
    public int getItemCount() {
        return supplierList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvKontak, tvAlamat, tvEmail, tvStatus;
        com.google.android.material.button.MaterialButton btnEdit;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_nama_supplier);
            tvKontak = itemView.findViewById(R.id.tv_kontak);
            tvAlamat = itemView.findViewById(R.id.tv_alamat);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
