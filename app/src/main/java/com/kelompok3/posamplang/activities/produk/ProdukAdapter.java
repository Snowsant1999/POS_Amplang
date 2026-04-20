package com.kelompok3.posamplang.activities.produk;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.models.Produk;
import com.google.android.material.button.MaterialButton;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProdukAdapter extends RecyclerView.Adapter<ProdukAdapter.ProdukViewHolder> {

    private List<Produk> produkList;

    public ProdukAdapter(List<Produk> produkList) {
        this.produkList = produkList;
    }

    @NonNull
    @Override
    public ProdukViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produk, parent, false);
        return new ProdukViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdukViewHolder holder, int position) {
        Produk produk = produkList.get(position);
        holder.tvNama.setText(produk.getNama());
        holder.tvKode.setText(produk.getKode());
        holder.tvMerek.setText(produk.getMerek());
        holder.tvKategori.setText(produk.getKategori());
        holder.tvSatuan.setText(produk.getSatuan());
        
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        formatter.setMaximumFractionDigits(0);
        holder.tvHargaBeli.setText(formatter.format(produk.getHargaBeli()).replace("Rp", "Rp "));
        holder.tvHargaJual.setText(formatter.format(produk.getHargaJual()).replace("Rp", "Rp "));
        
        holder.tvStok.setText(String.valueOf(produk.getStok()));
        holder.tvMinStok.setText(String.valueOf(produk.getMinStok()));
        holder.tvStatus.setText(produk.getStatus());

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditProdukActivity.class);
            // Anda bisa mengirim data produk via parcelable jika perlu
            v.getContext().startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(v.getContext())
                .setTitle("Hapus Produk")
                .setMessage("Apakah Anda yakin ingin menghapus produk " + produk.getNama() + "?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    int currentPosition = holder.getAdapterPosition();
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        produkList.remove(currentPosition);
                        notifyItemRemoved(currentPosition);
                        notifyItemRangeChanged(currentPosition, produkList.size());
                        Toast.makeText(v.getContext(), "Produk berhasil dihapus", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
        });
    }

    @Override
    public int getItemCount() {
        return produkList.size();
    }

    public static class ProdukViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvKode, tvMerek, tvKategori, tvSatuan, tvHargaBeli, tvHargaJual, tvStok, tvMinStok, tvStatus;
        com.google.android.material.button.MaterialButton btnEdit;
        ImageButton btnDelete;

        public ProdukViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvKode = itemView.findViewById(R.id.tvKode);
            tvMerek = itemView.findViewById(R.id.tvMerek);
            tvKategori = itemView.findViewById(R.id.tvKategori);
            tvSatuan = itemView.findViewById(R.id.tvSatuan);
            tvHargaBeli = itemView.findViewById(R.id.tvHargaBeli);
            tvHargaJual = itemView.findViewById(R.id.tvHargaJual);
            tvStok = itemView.findViewById(R.id.tvStok);
            tvMinStok = itemView.findViewById(R.id.tvMinStok);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
