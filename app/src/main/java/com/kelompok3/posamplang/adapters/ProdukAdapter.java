package com.kelompok3.posamplang.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.activities.produk.EditProdukActivity;
import com.kelompok3.posamplang.activities.produk.ProdukListActivity;
import com.kelompok3.posamplang.database.AppDatabase;
import com.kelompok3.posamplang.models.Kategori;
import com.kelompok3.posamplang.models.Merek;
import com.kelompok3.posamplang.models.Produk;
import com.kelompok3.posamplang.utils.FormatUtils;

import java.util.List;
import java.util.concurrent.Executors;

public class ProdukAdapter extends RecyclerView.Adapter<ProdukAdapter.ProdukViewHolder> {

    /** Callback untuk memberi tahu Activity bahwa data perlu di-reload */
    public interface OnDataChangedListener {
        void onDataChanged();
    }

    private final List<Produk>           produkList;
    private       OnDataChangedListener  listener;

    public ProdukAdapter(List<Produk> produkList) {
        this.produkList = produkList;
    }

    public void setOnDataChangedListener(OnDataChangedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProdukViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_produk, parent, false);
        return new ProdukViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdukViewHolder holder, int position) {
        holder.bind(produkList.get(position));
    }

    @Override
    public int getItemCount() {
        return produkList.size();
    }

    public class ProdukViewHolder extends RecyclerView.ViewHolder {

        private final TextView       tvNama;
        private final TextView       tvMerek;
        private final TextView       tvKategori;
        private final TextView       tvSatuan;
        private final TextView       tvHargaJual;
        private final TextView       tvStok;
        private final TextView       tvStatus;
        private final MaterialButton btnEdit;
        private final ImageButton    btnDelete;

        public ProdukViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama      = itemView.findViewById(R.id.tvNama);
            tvMerek     = itemView.findViewById(R.id.tvMerek);
            tvKategori  = itemView.findViewById(R.id.tvKategori);
            tvSatuan    = itemView.findViewById(R.id.tvSatuan);
            tvHargaJual = itemView.findViewById(R.id.tvHargaJual);
            tvStok      = itemView.findViewById(R.id.tvStok);
            tvStatus    = itemView.findViewById(R.id.tvStatus);
            btnEdit     = itemView.findViewById(R.id.btnEdit);
            btnDelete   = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Produk produk) {
            AppDatabase db = AppDatabase.getInstance(itemView.getContext());

            // Data yang langsung tersedia
            tvNama.setText(produk.getNama_produk());
            tvSatuan.setText(produk.getUnit());
            tvHargaJual.setText(FormatUtils.formatRupiah(produk.getHarga_produk()));
            tvStok.setText(String.valueOf(produk.getStok_tersedia()));

            // Warna stok merah jika kritis
            if (produk.getStok_tersedia() <= 10) {
                tvStok.setTextColor(Color.parseColor("#C62828"));
            } else {
                tvStok.setTextColor(Color.parseColor("#212121"));
            }

            // Status selalu Aktif (bisa dikembangkan nanti)
            tvStatus.setText("Aktif");
            tvStatus.setBackgroundResource(R.drawable.bg_status_aktif);

            // Ambil nama Merek & Kategori dari DB (background thread)
            Executors.newSingleThreadExecutor().execute(() -> {
                Merek merek       = db.merekDao().getById(produk.getId_merek());
                Kategori kategori = db.kategoriDao().getById(produk.getId_kategori_produk());

                String namaMerek    = (merek    != null) ? merek.getNama_merek()         : "-";
                String namaKategori = (kategori != null) ? kategori.getNama_kategori()   : "-";

                itemView.post(() -> {
                    tvMerek.setText(namaMerek);
                    tvKategori.setText(namaKategori);
                });
            });

            // Tombol Edit — memanggil showEditProdukDialog di activity induk
            btnEdit.setOnClickListener(v -> {
                android.content.Context context = v.getContext();
                while (context instanceof android.content.ContextWrapper) {
                    if (context instanceof ProdukListActivity) {
                        ((ProdukListActivity) context).showEditProdukDialog(produk);
                        return;
                    }
                    context = ((android.content.ContextWrapper) context).getBaseContext();
                }
            });

            // Tombol Hapus — konfirmasi dialog + hapus dari DB
            btnDelete.setOnClickListener(v ->
                    new MaterialAlertDialogBuilder(v.getContext())
                            .setTitle("Hapus Produk")
                            .setMessage("Yakin ingin menghapus \"" + produk.getNama_produk() + "\"?")
                            .setPositiveButton("Hapus", (dialog, which) -> {
                                Executors.newSingleThreadExecutor().execute(() -> {
                                    db.produkDao().delete(produk);
                                    // Beritahu Activity untuk reload data dari DB
                                    if (listener != null) {
                                        ((android.app.Activity) v.getContext()).runOnUiThread(() -> {
                                            Toast.makeText(v.getContext(), "Produk \"" + produk.getNama_produk() + "\" dihapus", Toast.LENGTH_SHORT).show();
                                            listener.onDataChanged();
                                        });
                                    }
                                });
                            })
                            .setNegativeButton("Batal", null)
                            .show()
            );
        }
    }
}
