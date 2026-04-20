package com.kelompok3.posamplang.adapters;

import android.content.Intent;
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
import com.kelompok3.posamplang.models.Produk;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * ProdukAdapter — Adapter RecyclerView untuk menampilkan daftar produk.
 *
 * Digunakan oleh ProdukListActivity untuk merender setiap item produk
 * beserta tombol Edit dan Hapus.
 *
 * TODO: Hubungkan aksi hapus ke ProdukDao.deleteProduk() setelah Room Database siap.
 * TODO: Kirim data Produk via Parcelable ke EditProdukActivity untuk mode edit sesungguhnya.
 */
public class ProdukAdapter extends RecyclerView.Adapter<ProdukAdapter.ProdukViewHolder> {

    // -------------------------------------------------------------------------
    // Data
    // -------------------------------------------------------------------------

    private final List<Produk> produkList;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public ProdukAdapter(List<Produk> produkList) {
        this.produkList = produkList;
    }

    // -------------------------------------------------------------------------
    // RecyclerView.Adapter — Override
    // -------------------------------------------------------------------------

    @NonNull
    @Override
    public ProdukViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_produk, parent, false);
        return new ProdukViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdukViewHolder holder, int position) {
        Produk produk = produkList.get(position);
        holder.bind(produk, position);
    }

    @Override
    public int getItemCount() {
        return produkList.size();
    }

    // -------------------------------------------------------------------------
    // ViewHolder
    // -------------------------------------------------------------------------

    public class ProdukViewHolder extends RecyclerView.ViewHolder {

        private final TextView     tvNama;
        private final TextView     tvKode;
        private final TextView     tvMerek;
        private final TextView     tvKategori;
        private final TextView     tvSatuan;
        private final TextView     tvHargaBeli;
        private final TextView     tvHargaJual;
        private final TextView     tvStok;
        private final TextView     tvMinStok;
        private final TextView     tvStatus;
        private final MaterialButton btnEdit;
        private final ImageButton  btnDelete;

        public ProdukViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama      = itemView.findViewById(R.id.tvNama);
            tvKode      = itemView.findViewById(R.id.tvKode);
            tvMerek     = itemView.findViewById(R.id.tvMerek);
            tvKategori  = itemView.findViewById(R.id.tvKategori);
            tvSatuan    = itemView.findViewById(R.id.tvSatuan);
            tvHargaBeli = itemView.findViewById(R.id.tvHargaBeli);
            tvHargaJual = itemView.findViewById(R.id.tvHargaJual);
            tvStok      = itemView.findViewById(R.id.tvStok);
            tvMinStok   = itemView.findViewById(R.id.tvMinStok);
            tvStatus    = itemView.findViewById(R.id.tvStatus);
            btnEdit     = itemView.findViewById(R.id.btnEdit);
            btnDelete   = itemView.findViewById(R.id.btnDelete);
        }

        /** Mengisi data produk ke dalam tampilan item dan mendaftarkan listener. */
        public void bind(Produk produk, int position) {
            // Tampilkan data produk
            tvNama.setText(produk.getNama_produk());
            tvKode.setText("ID: " + produk.getId_produk());
            tvMerek.setText("Merek ID: " + produk.getId_merek());
            tvKategori.setText("Kategori ID: " + produk.getId_kategori_produk());
            tvSatuan.setText(produk.getUnit());
            tvHargaBeli.setText("-"); // Field harga beli tidak ada di tabel produk (lihat ERD)
            tvHargaJual.setText(formatRupiah(produk.getHarga_produk()));
            tvStok.setText(String.valueOf(produk.getStok_tersedia()));
            tvMinStok.setText("-");
            tvStatus.setText("Aktif");

            // Tombol Edit — buka EditProdukActivity
            // TODO: Kirim data produk via Parcelable agar form edit terisi otomatis
            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), EditProdukActivity.class);
                v.getContext().startActivity(intent);
            });

            // Tombol Hapus — konfirmasi via dialog sebelum menghapus
            btnDelete.setOnClickListener(v ->
                    new MaterialAlertDialogBuilder(v.getContext())
                            .setTitle("Hapus Produk")
                            .setMessage("Apakah Anda yakin ingin menghapus produk \"" + produk.getNama_produk() + "\"?")
                            .setPositiveButton("Hapus", (dialog, which) -> {
                                int currentPosition = getAdapterPosition();
                                if (currentPosition != RecyclerView.NO_ID) {
                                    produkList.remove(currentPosition);
                                    notifyItemRemoved(currentPosition);
                                    notifyItemRangeChanged(currentPosition, produkList.size());
                                    Toast.makeText(v.getContext(), "Produk berhasil dihapus", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Batal", null)
                            .show()
            );
        }

        /** Memformat angka harga menjadi format Rupiah Indonesia. */
        private String formatRupiah(double amount) {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            formatter.setMaximumFractionDigits(0);
            return formatter.format(amount).replace("Rp", "Rp ");
        }
    }
}
